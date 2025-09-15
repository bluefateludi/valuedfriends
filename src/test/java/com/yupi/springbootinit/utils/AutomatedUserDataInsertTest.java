package com.yupi.springbootinit.utils;

import com.yupi.springbootinit.service.UserBatchInsertService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 自动化用户数据插入测试类
 * 实现十万条用户数据的自动生成和批量插入
 * 
 * @author AI Assistant
 */
@Slf4j
@SpringBootTest
public class AutomatedUserDataInsertTest {

    @Resource
    private UserBatchInsertService userBatchInsertService;

    /**
     * 主测试方法：自动化生成并插入十万条用户数据
     */
    @Test
    public void testAutomatedUserDataInsert() {
        int totalCount = 100000; // 十万条数据
        int batchSize = 1000;    // 每批1000条
        
        log.info("开始使用真正的批量插入自动化生成并插入{}条用户测试数据", totalCount);
        
        long startTime = System.currentTimeMillis();
        int successCount = userBatchInsertService.generateAndBatchInsert(totalCount, batchSize);
        long duration = System.currentTimeMillis() - startTime;
        
        // 验证结果
        log.info("自动化批量数据插入完成！");
        log.info("总耗时: {}ms ({:.2f}秒)", duration, duration / 1000.0);
        log.info("成功插入: {}条", successCount);
        log.info("失败: {}条", totalCount - successCount);
        log.info("成功率: {:.2f}%", (double) successCount / totalCount * 100);
        log.info("平均速度: {:.0f}条/秒", (double) successCount / (duration / 1000.0));
        log.info("插入方式: MyBatis-Plus真正的批量插入(saveBatch)");
        
        // 断言验证
        assert successCount > 0 : "应该至少插入一些数据";
        assert successCount >= totalCount * 0.95 : "成功率应该至少95%";
    }



    /**
     * 验证数据插入结果的测试方法
     */
    @Test
    public void testVerifyInsertedData() {
        log.info("=== 验证插入的数据 ===");
        
        // 使用服务验证数据
        userBatchInsertService.verifyInsertedData();
        
        log.info("数据验证完成！");
    }
}