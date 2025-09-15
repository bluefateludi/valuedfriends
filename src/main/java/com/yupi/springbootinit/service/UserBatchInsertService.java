package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.User;

import java.util.List;

/**
 * 用户批量插入服务接口
 * 
 * @author AI Assistant
 */
public interface UserBatchInsertService extends IService<User> {
    
    /**
     * 批量插入用户数据
     * 
     * @param users 用户列表
     * @param batchSize 批次大小
     * @return 成功插入的数量
     */
    int batchInsertUsers(List<User> users, int batchSize);
    
    /**
     * 生成并批量插入指定数量的随机用户数据
     * 
     * @param totalCount 总数量
     * @param batchSize 批次大小
     * @return 成功插入的数量
     */
    int generateAndBatchInsert(int totalCount, int batchSize);
    
    /**
     * 验证插入的数据
     */
    void verifyInsertedData();
}