package com.yupi.springbootinit.model.dto.user;

import com.yupi.springbootinit.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户推荐请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRecommendRequest extends PageRequest implements Serializable {
    
    /**
     * 目标用户ID
     */
    private Long userId;
    
    /**
     * 最小相似度阈值（0-1之间，默认0.1）
     */
    private Double minSimilarity = 0.1;
    
    /**
     * 推荐用户数量限制（默认10个）
     */
    private Integer limit = 10;
    
    /**
     * 是否排除已关注的用户（默认true）
     */
    private Boolean excludeFollowed = true;
    
    private static final long serialVersionUID = 1L;
}