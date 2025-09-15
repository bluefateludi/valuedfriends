package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.user.UserRecommendRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.UserRecommendListVO;
import com.yupi.springbootinit.model.vo.UserRecommendVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.UserRecommendService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户推荐服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserRecommendServiceImpl implements UserRecommendService {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private Executor taskExecutor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 缓存键前缀
    private static final String RECOMMEND_CACHE_PREFIX = "user:recommend:";
    private static final String USER_TAGS_CACHE_PREFIX = "user:tags:";

    // 缓存过期时间（分钟）
    private static final long CACHE_EXPIRE_MINUTES = 30;

    @Override
    public List<UserRecommendVO> recommendUsersByTags(Long userId, Integer limit, Double minSimilarity) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        final double finalMinSimilarity;
        if (minSimilarity == null || minSimilarity < 0 || minSimilarity > 1) {
            finalMinSimilarity = 0.1;
        } else {
            finalMinSimilarity = minSimilarity;
        }

        // 尝试从缓存获取推荐结果
        String cacheKey = RECOMMEND_CACHE_PREFIX + userId + ":" + limit + ":" + finalMinSimilarity;
        try {
            @SuppressWarnings("unchecked")
            List<UserRecommendVO> cachedResult = (List<UserRecommendVO>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                log.info("从缓存获取用户 {} 的推荐结果", userId);
                return cachedResult;
            }
        } catch (Exception e) {
            log.warn("获取缓存失败: {}", e.getMessage());
        }

        // 获取目标用户信息
        User targetUser = userService.getById(userId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 解析目标用户标签
        List<String> targetTags = parseUserTags(targetUser.getTags());
        if (targetTags.isEmpty()) {
            log.info("用户 {} 没有标签，无法进行推荐", userId);
            return new ArrayList<>();
        }

        // 查询所有有标签的用户（排除目标用户和已删除用户）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", userId)
                .isNotNull("tags")
                .ne("tags", "")
                .ne("tags", "[]")
                .eq("userStatus", 0)
                .eq("isDelete", 0);

        List<User> candidateUsers = userService.list(queryWrapper);

        // 使用并发处理提高性能
        List<UserRecommendVO> recommendations = candidateUsers.parallelStream()
                .map(candidate -> {
                    List<String> candidateTags = parseUserTags(candidate.getTags());
                    if (candidateTags.isEmpty()) {
                        return null;
                    }

                    // 计算相似度
                    Double similarity = calculateTagSimilarity(targetTags, candidateTags);

                    // 过滤低相似度用户
                    if (similarity >= finalMinSimilarity) {
                        UserRecommendVO recommendVO = new UserRecommendVO();

                        // 转换用户信息
                        UserVO userVO = new UserVO();
                        BeanUtils.copyProperties(candidate, userVO);
                        recommendVO.setUser(userVO);

                        recommendVO.setSimilarityScore(similarity);
                        recommendVO.setMatchedTags(getMatchedTags(targetTags, candidateTags));
                        recommendVO
                                .setRecommendReason(generateRecommendReason(recommendVO.getMatchedTags(), similarity));

                        return recommendVO;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()))
                .limit(limit)
                .collect(Collectors.toList());

        // 异步缓存结果
        CompletableFuture.runAsync(() -> {
            try {
                redisTemplate.opsForValue().set(cacheKey, recommendations, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                log.info("缓存用户 {} 的推荐结果", userId);
            } catch (Exception e) {
                log.warn("缓存推荐结果失败: {}", e.getMessage());
            }
        }, taskExecutor);

        return recommendations;
    }

    @Override
    public UserRecommendListVO getRecommendations(UserRecommendRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        List<UserRecommendVO> recommendations = recommendUsersByTags(
                request.getUserId(),
                request.getLimit(),
                request.getMinSimilarity());

        UserRecommendListVO result = new UserRecommendListVO();
        result.setTargetUserId(request.getUserId());
        result.setRecommendUsers(recommendations);
        result.setTotalCount(recommendations.size());
        result.setAlgorithmVersion("v1.0-jaccard");
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    @Override
    public Double calculateTagSimilarity(List<String> userTags1, List<String> userTags2) {
        if (userTags1 == null || userTags2 == null || userTags1.isEmpty() || userTags2.isEmpty()) {
            return 0.0;
        }

        // 使用Jaccard相似度算法
        Set<String> set1 = new HashSet<>(userTags1);
        Set<String> set2 = new HashSet<>(userTags2);

        // 计算交集
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        // 计算并集
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        // Jaccard相似度 = |交集| / |并集|
        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }

    @Override
    public List<String> parseUserTags(String tagsJson) {
        if (StringUtils.isBlank(tagsJson)) {
            return new ArrayList<>();
        }

        try {
            // 尝试解析JSON数组
            List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {
            });
            return tags != null ? tags : new ArrayList<>();
        } catch (Exception e) {
            log.warn("解析用户标签失败: {}", tagsJson, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getMatchedTags(List<String> userTags1, List<String> userTags2) {
        if (userTags1 == null || userTags2 == null) {
            return new ArrayList<>();
        }

        Set<String> set1 = new HashSet<>(userTags1);
        Set<String> set2 = new HashSet<>(userTags2);

        set1.retainAll(set2);
        return new ArrayList<>(set1);
    }

    /**
     * 生成推荐原因描述
     *
     * @param matchedTags 匹配的标签
     * @param similarity  相似度
     * @return 推荐原因
     */
    private String generateRecommendReason(List<String> matchedTags, Double similarity) {
        if (matchedTags == null || matchedTags.isEmpty()) {
            return String.format("相似度: %.2f", similarity);
        }

        String tagsStr = String.join("、", matchedTags);
        return String.format("共同兴趣: %s (相似度: %.2f)", tagsStr, similarity);
    }
}