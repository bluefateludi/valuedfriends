package com.yupi.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分批匹配响应
 * @author yupi
 */
@Data
public class BatchMatchResponse implements Serializable {

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
     * 实际处理的批次数量
     */
    private Integer actualBatches;

    /**
     * 总候选用户数量
     */
    private Integer totalCandidates;

    /**
     * 是否来自缓存
     */
    private Boolean fromCache;

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTime;

    /**
     * 算法版本
     */
    private String algorithmVersion;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 下次轮换偏移量建议
     */
    private Integer nextRotationOffset;

    /**
     * 性能统计信息
     */
    private PerformanceStats performanceStats;

    @Data
    public static class PerformanceStats implements Serializable {
        /**
         * 数据库查询耗时（毫秒）
         */
        private Long dbQueryTime;

        /**
         * 相似度计算耗时（毫秒）
         */
        private Long similarityCalcTime;

        /**
         * 缓存操作耗时（毫秒）
         */
        private Long cacheTime;

        /**
         * 平均每个用户处理耗时（毫秒）
         */
        private Double avgUserProcessTime;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}