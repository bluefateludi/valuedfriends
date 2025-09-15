package com.yupi.springbootinit.runner;

import com.yupi.springbootinit.service.UserBatchInsertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户数据自动插入运行器
 * 在应用启动时自动执行数据插入（可通过配置控制是否启用）
 * 
 * @author AI Assistant
 */
@Component
@Slf4j
public class UserDataInsertRunner implements ApplicationRunner {

    @Resource
    private UserBatchInsertService userBatchInsertService;

    // 配置参数
    @Value("${app.data.enable-auto-insert:false}")
    private boolean enableDataInsert;
    
    @Value("${app.data.insert-count:100000}")
    private int insertCount;
    
    @Value("${app.data.batch-size:1000}")
    private int batchSize;
    


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (enableDataInsert) {
            log.info("开始使用真正的批量插入自动插入{}条用户数据，批次大小: {}", insertCount, batchSize);
            
            long startTime = System.currentTimeMillis();
            int successCount = userBatchInsertService.generateAndBatchInsert(insertCount, batchSize);
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("自动批量数据插入完成！耗时: {}ms, 成功: {}条, 平均速度: {:.0f}条/秒, 插入方式: MyBatis-Plus真正的批量插入", 
                duration, successCount, (double) successCount / (duration / 1000.0));
        } else {
            log.info("自动数据插入功能已禁用");
        }
    }
    

    
    /**
     * 手动触发数据插入的方法（可通过接口调用）
     */
    public void manualInsertData() throws Exception {
        log.info("手动触发用户数据插入...");
        run(null);
    }
}