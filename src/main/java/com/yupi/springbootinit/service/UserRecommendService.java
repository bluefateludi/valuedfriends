package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.dto.user.UserRecommendRequest;
import com.yupi.springbootinit.model.vo.UserRecommendListVO;
import com.yupi.springbootinit.model.vo.UserRecommendVO;
import java.util.List;

/**
 * 用户推荐服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface UserRecommendService {
    
    /**
     * 根据用户标签推荐相似用户
     *
     * @param userId 目标用户ID
     * @param limit 推荐数量限制
     * @param minSimilarity 最小相似度阈值
     * @return 推荐用户列表
     */
    List<UserRecommendVO> recommendUsersByTags(Long userId, Integer limit, Double minSimilarity);
    
    /**
     * 根据推荐请求获取推荐结果
     *
     * @param request 推荐请求参数
     * @return 推荐结果
     */
    UserRecommendListVO getRecommendations(UserRecommendRequest request);
    
    /**
     * 计算两个用户之间的标签相似度
     *
     * @param userTags1 用户1的标签列表
     * @param userTags2 用户2的标签列表
     * @return 相似度评分（0-1之间）
     */
    Double calculateTagSimilarity(List<String> userTags1, List<String> userTags2);
    
    /**
     * 解析用户标签JSON字符串
     *
     * @param tagsJson 标签JSON字符串
     * @return 标签列表
     */
    List<String> parseUserTags(String tagsJson);
    
    /**
     * 获取匹配的标签列表
     *
     * @param userTags1 用户1的标签列表
     * @param userTags2 用户2的标签列表
     * @return 匹配的标签列表
     */
    List<String> getMatchedTags(List<String> userTags1, List<String> userTags2);
}