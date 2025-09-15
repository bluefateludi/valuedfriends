package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.MatchConfigMapper;
import com.yupi.springbootinit.mapper.UserMapper;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.*;
import com.yupi.springbootinit.service.MatchService;
import com.yupi.springbootinit.service.UserRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 匹配服务实现类
 * @author yupi
 */
@Service
@Slf4j
public class MatchServiceImpl implements MatchService {

    @Resource
    private MatchConfigMapper matchConfigMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRecommendService userRecommendService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ThreadPoolExecutor taskExecutor;

    private static final String BATCH_MATCH_CACHE_KEY = "batch_match:user:";
    private static final String ROTATION_OFFSET_KEY = "rotation_offset:user:";

    @Override
    public BatchMatchResponse batchMatchUsers(BatchMatchRequest request, User loginUser) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        
        // 如果请求中没有指定用户ID，使用当前登录用户ID
        if (request.getUserId() == null) {
            request.setUserId(loginUser.getId());
        }

        long startTime = System.currentTimeMillis();
        BatchMatchResponse response = new BatchMatchResponse();
        BatchMatchResponse.PerformanceStats stats = new BatchMatchResponse.PerformanceStats();

        // 设置默认参数
        setDefaultParams(request);

        // 检查缓存
        String cacheKey = BATCH_MATCH_CACHE_KEY + request.getUserId();
        if (Boolean.TRUE.equals(request.getEnableCache())) {
            BatchMatchResponse cachedResponse = getCachedResponse(cacheKey);
            if (cachedResponse != null) {
                cachedResponse.setFromCache(true);
                cachedResponse.setProcessingTime(System.currentTimeMillis() - startTime);
                return cachedResponse;
            }
        }

        // 获取目标用户信息
        User targetUser = userMapper.selectById(request.getUserId());
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 解析目标用户标签
        List<String> targetTags = userRecommendService.parseUserTags(targetUser.getTags());
        if (CollectionUtils.isEmpty(targetTags)) {
            log.warn("用户 {} 没有标签，无法进行匹配", request.getUserId());
            return createEmptyResponse(request.getUserId(), startTime);
        }

        // 分批查询和匹配
        List<UserRecommendVO> allRecommendations = new ArrayList<>();
        int actualBatches = 0;
        int totalCandidates = 0;
        long dbQueryTime = 0;
        long similarityCalcTime = 0;

        // 获取轮换偏移量
        int rotationOffset = getRotationOffset(request.getUserId());
        if (request.getRotationOffset() != null) {
            rotationOffset = request.getRotationOffset();
        }

        for (int batch = 0; batch < request.getMaxBatches(); batch++) {
            long batchStartTime = System.currentTimeMillis();
            
            // 计算分页参数（带轮换）
            int offset = batch * request.getBatchSize() + rotationOffset;
            Page<User> page = new Page<>(offset / request.getBatchSize() + 1, request.getBatchSize());
            
            // 查询候选用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("id", request.getUserId())
                    .isNotNull("tags")
                    .ne("tags", "")
                    .ne("tags", "[]")
                    .eq("userStatus", 0);
            
            Page<User> userPage = userMapper.selectPage(page, queryWrapper);
            List<User> candidates = userPage.getRecords();
            
            dbQueryTime += System.currentTimeMillis() - batchStartTime;
            
            if (CollectionUtils.isEmpty(candidates)) {
                break;
            }
            
            totalCandidates += candidates.size();
            actualBatches++;
            
            // 并发计算相似度
            long calcStartTime = System.currentTimeMillis();
            List<UserRecommendVO> batchRecommendations = calculateSimilarityBatch(
                    targetUser, targetTags, candidates, request.getMinSimilarity());
            similarityCalcTime += System.currentTimeMillis() - calcStartTime;
            
            allRecommendations.addAll(batchRecommendations);
            
            // 如果已经找到足够的推荐用户，提前结束
            if (allRecommendations.size() >= request.getMaxRecommendations()) {
                break;
            }
        }

        // 排序并限制结果数量
        List<UserRecommendVO> finalRecommendations = allRecommendations.stream()
                .sorted((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()))
                .limit(request.getMaxRecommendations())
                .collect(Collectors.toList());

        // 构建响应
        response.setTargetUserId(request.getUserId());
        response.setRecommendUsers(finalRecommendations);
        response.setTotalCount(finalRecommendations.size());
        response.setActualBatches(actualBatches);
        response.setTotalCandidates(totalCandidates);
        response.setFromCache(false);
        response.setAlgorithmVersion("v2.0-batch-jaccard");
        response.setTimestamp(System.currentTimeMillis());
        
        // 计算下次轮换偏移量
        int nextOffset = (rotationOffset + getIntConfigValue("rotation_offset_increment", 5)) % 100;
        response.setNextRotationOffset(nextOffset);
        updateRotationOffset(request.getUserId(), nextOffset);

        // 设置性能统计
        long totalTime = System.currentTimeMillis() - startTime;
        response.setProcessingTime(totalTime);
        stats.setDbQueryTime(dbQueryTime);
        stats.setSimilarityCalcTime(similarityCalcTime);
        stats.setAvgUserProcessTime(totalCandidates > 0 ? (double) totalTime / totalCandidates : 0.0);
        response.setPerformanceStats(stats);

        // 异步缓存结果
        if (Boolean.TRUE.equals(request.getEnableCache()) && !finalRecommendations.isEmpty()) {
            cacheResponse(cacheKey, response);
        }

        return response;
    }

    /**
     * 设置默认参数
     */
    private void setDefaultParams(BatchMatchRequest request) {
        if (request.getBatchSize() == null) {
            request.setBatchSize(getIntConfigValue("batch_size", 20));
        }
        if (request.getMaxBatches() == null) {
            request.setMaxBatches(getIntConfigValue("max_batches", 10));
        }
        if (request.getMinSimilarity() == null) {
            request.setMinSimilarity(getDoubleConfigValue("min_similarity", 0.1));
        }
        if (request.getMaxRecommendations() == null) {
            request.setMaxRecommendations(getIntConfigValue("max_recommendations", 50));
        }
        if (request.getEnableCache() == null) {
            request.setEnableCache(true);
        }
    }

    /**
     * 并发计算相似度
     */
    private List<UserRecommendVO> calculateSimilarityBatch(User targetUser, List<String> targetTags, 
                                                           List<User> candidates, Double minSimilarity) {
        List<CompletableFuture<UserRecommendVO>> futures = candidates.stream()
                .map(candidate -> CompletableFuture.supplyAsync(() -> {
                    List<String> candidateTags = userRecommendService.parseUserTags(candidate.getTags());
                    if (CollectionUtils.isEmpty(candidateTags)) {
                        return null;
                    }
                    
                    Double similarity = userRecommendService.calculateTagSimilarity(targetTags, candidateTags);
                    if (similarity < minSimilarity) {
                        return null;
                    }
                    
                    UserRecommendVO vo = new UserRecommendVO();
                    vo.setId(candidate.getId());
                    vo.setUsername(candidate.getUsername());
                    vo.setUserAccount(candidate.getUserAccount());
                    vo.setAvatarUrl(candidate.getAvatarUrl());
                    vo.setGender(candidate.getGender());
                    vo.setPhone(candidate.getPhone());
                    vo.setEmail(candidate.getEmail());
                    vo.setUserStatus(candidate.getUserStatus());
                    vo.setCreateTime(candidate.getCreateTime());
                    vo.setUserRole(candidate.getUserRole());
                    vo.setPlanetCode(candidate.getPlanetCode());
                    vo.setTags(candidateTags);
                    vo.setSimilarity(similarity);
                    vo.setMatchedTags(userRecommendService.getMatchedTags(targetTags, candidateTags));
                    
                    return vo;
                }, taskExecutor))
                .collect(Collectors.toList());
        
        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取缓存响应
     */
    private BatchMatchResponse getCachedResponse(String cacheKey) {
        try {
            return (BatchMatchResponse) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("获取缓存失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 缓存响应
     */
    private void cacheResponse(String cacheKey, BatchMatchResponse response) {
        CompletableFuture.runAsync(() -> {
            try {
                int expireMinutes = getIntConfigValue("cache_expire_minutes", 30);
                redisTemplate.opsForValue().set(cacheKey, response, expireMinutes, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("缓存结果失败: {}", e.getMessage());
            }
        }, taskExecutor);
    }

    /**
     * 获取轮换偏移量
     */
    private int getRotationOffset(Long userId) {
        try {
            String key = ROTATION_OFFSET_KEY + userId;
            Integer offset = (Integer) redisTemplate.opsForValue().get(key);
            return offset != null ? offset : 0;
        } catch (Exception e) {
            log.warn("获取轮换偏移量失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 更新轮换偏移量
     */
    private void updateRotationOffset(Long userId, int offset) {
        CompletableFuture.runAsync(() -> {
            try {
                String key = ROTATION_OFFSET_KEY + userId;
                redisTemplate.opsForValue().set(key, offset, 24, TimeUnit.HOURS);
            } catch (Exception e) {
                log.warn("更新轮换偏移量失败: {}", e.getMessage());
            }
        }, taskExecutor);
    }

    /**
     * 创建空响应
     */
    private BatchMatchResponse createEmptyResponse(Long userId, long startTime) {
        BatchMatchResponse response = new BatchMatchResponse();
        response.setTargetUserId(userId);
        response.setRecommendUsers(new ArrayList<>());
        response.setTotalCount(0);
        response.setActualBatches(0);
        response.setTotalCandidates(0);
        response.setFromCache(false);
        response.setProcessingTime(System.currentTimeMillis() - startTime);
        response.setAlgorithmVersion("v2.0-batch-jaccard");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        return matchConfigMapper.getConfigValueOrDefault(configKey, defaultValue);
    }

    @Override
    public Integer getIntConfigValue(String configKey, Integer defaultValue) {
        return matchConfigMapper.getIntConfigValue(configKey, defaultValue);
    }

    @Override
    public Boolean getBooleanConfigValue(String configKey, Boolean defaultValue) {
        return matchConfigMapper.getBooleanConfigValue(configKey, defaultValue);
    }

    @Override
    public Double getDoubleConfigValue(String configKey, Double defaultValue) {
        return matchConfigMapper.getDoubleConfigValue(configKey, defaultValue);
    }

    @Override
    public void clearUserMatchCache(Long userId) {
        try {
            String cacheKey = BATCH_MATCH_CACHE_KEY + userId;
            redisTemplate.delete(cacheKey);
            log.info("清除用户 {} 的匹配缓存", userId);
        } catch (Exception e) {
            log.warn("清除用户匹配缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public void warmupUserMatchCache(Long userId) {
        CompletableFuture.runAsync(() -> {
            try {
                BatchMatchRequest request = new BatchMatchRequest();
                request.setUserId(userId);
                request.setEnableCache(true);
                batchMatchUsers(request);
                log.info("预热用户 {} 的匹配缓存完成", userId);
            } catch (Exception e) {
                log.warn("预热用户匹配缓存失败: {}", e.getMessage());
            }
        }, taskExecutor);
    }
}