package com.yupi.springbootinit.model.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分批匹配请求
 * @author yupi
 */
@Data
public class BatchMatchRequest implements Serializable {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 批次大小（每批查询的用户数量）
     */
    @Min(value = 1, message = "批次大小不能小于1")
    @Max(value = 100, message = "批次大小不能大于100")
    private Integer batchSize;

    /**
     * 最大批次数量
     */
    @Min(value = 1, message = "最大批次数量不能小于1")
    @Max(value = 20, message = "最大批次数量不能大于20")
    private Integer maxBatches;

    /**
     * 最小相似度阈值
     */
    @Min(value = 0, message = "最小相似度不能小于0")
    @Max(value = 1, message = "最小相似度不能大于1")
    private Double minSimilarity;

    /**
     * 最大推荐用户数量
     */
    @Min(value = 1, message = "最大推荐用户数量不能小于1")
    @Max(value = 100, message = "最大推荐用户数量不能大于100")
    private Integer maxRecommendations;

    /**
     * 是否启用缓存
     */
    private Boolean enableCache;

    /**
     * 轮换偏移量（用于分页轮换）
     */
    private Integer rotationOffset;

    private static final long serialVersionUID = 1L;
}