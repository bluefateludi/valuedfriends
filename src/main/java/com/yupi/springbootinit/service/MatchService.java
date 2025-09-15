package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.BatchMatchRequest;
import com.yupi.springbootinit.model.vo.BatchMatchResponse;

/**
 * 匹配服务接口
 * 
 * @author yupi
 */
public interface MatchService {

    /**
     * 分批匹配用户
     * 
     * @param request   分批匹配请求
     * @param loginUser 当前登录用户
     * @return 分批匹配响应
     */
    BatchMatchResponse batchMatchUsers(BatchMatchRequest request, User loginUser);

    /**
     * 获取配置值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取整型配置值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    Integer getIntConfigValue(String configKey, Integer defaultValue);

    /**
     * 获取布尔型配置值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    Boolean getBooleanConfigValue(String configKey, Boolean defaultValue);

    /**
     * 获取双精度配置值
     * 
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    Double getDoubleConfigValue(String configKey, Double defaultValue);

    /**
     * 清除用户匹配缓存
     * 
     * @param userId 用户ID
     */
    void clearUserMatchCache(Long userId);

    /**
     * 预热用户匹配缓存
     * 
     * @param userId 用户ID
     */
    void warmupUserMatchCache(Long userId);
}