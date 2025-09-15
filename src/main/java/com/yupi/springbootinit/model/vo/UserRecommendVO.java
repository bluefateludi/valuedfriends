package com.yupi.springbootinit.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 用户推荐结果视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserRecommendVO implements Serializable {
    
    /**
     * 推荐用户信息
     */
    private UserVO user;
    
    /**
     * 相似度评分（0-1之间）
     */
    private Double similarityScore;
    
    /**
     * 匹配的标签列表
     */
    private List<String> matchedTags;
    
    /**
     * 推荐原因描述
     */
    private String recommendReason;
    
    private static final long serialVersionUID = 1L;
}