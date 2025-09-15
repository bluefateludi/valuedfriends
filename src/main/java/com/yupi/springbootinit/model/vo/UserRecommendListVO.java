package com.yupi.springbootinit.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 用户推荐列表结果视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserRecommendListVO implements Serializable {
    
    /**
     * 目标用户ID
     */
    private Long targetUserId;
    
    /**
     * 推荐用户列表
     */
    private List<UserRecommendVO> recommendUsers;
    
    /**
     * 总推荐数量
     */
    private Integer totalCount;
    
    /**
     * 推荐算法版本
     */
    private String algorithmVersion;
    
    /**
     * 推荐生成时间戳
     */
    private Long timestamp;
    
    private static final long serialVersionUID = 1L;
}