package com.yupi.springbootinit.controller;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.mapper.UserMapper;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.runner.UserDataInsertRunner;
import com.yupi.springbootinit.service.UserBatchInsertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据插入控制器
 * 提供手动触发数据插入的接口
 * 
 * @author AI Assistant
 */
@RestController
@RequestMapping("/data")
@Slf4j
public class DataInsertController {

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private UserDataInsertRunner userDataInsertRunner;
    
    @Resource
    private UserBatchInsertService userBatchInsertService;

    private final Random random = new Random();
    private static final String SALT = "yupi";
    
    // 数据生成库
    private final String[] surnames = {
        "李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴",
        "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"
    };
    
    private final String[] givenNames = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
        "洋", "勇", "艳", "杰", "涛", "明", "超", "秀兰", "霞", "平"
    };
    
    private final String[] skillTags = {
        "Java", "Python", "JavaScript", "C++", "Go", "React", "Vue", "Spring",
        "MyBatis", "Redis", "MySQL", "Docker", "微服务", "分布式", "大数据"
    };

    /**
     * 手动触发十万条数据插入
     */
    @PostMapping("/insert/users/100k")
    public BaseResponse<String> insertHundredThousandUsers() {
        try {
            log.info("接收到手动插入十万条用户数据的请求");
            
            // 检查是否已有数据
            Long existingCount = userMapper.selectCount(null);
            if (existingCount >= 100000) {
                return ResultUtils.error(40001, "数据库中已存在大量用户数据(" + existingCount + "条)，无需重复插入");
            }
            
            // 异步执行数据插入
            CompletableFuture.runAsync(() -> {
                try {
                    userBatchInsertService.generateAndBatchInsert(100000, 1000);
                } catch (Exception e) {
                    log.error("异步数据插入失败", e);
                }
            });
            
            return ResultUtils.success("数据插入任务已启动，正在后台执行。预计需要几分钟时间完成十万条数据的插入。");
            
        } catch (Exception e) {
            log.error("启动数据插入任务失败", e);
            return ResultUtils.error(50001, "启动数据插入任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 插入指定数量的用户数据
     */
    @PostMapping("/insert/users/{count}")
    public BaseResponse<String> insertUsers(@PathVariable Integer count) {
        try {
            if (count <= 0 || count > 100000) {
                return ResultUtils.error(40000, "数据量必须在1-100000之间");
            }
            
            log.info("接收到插入{}条用户数据的请求", count);
            
            // 同步执行小批量数据插入
            if (count <= 1000) {
                int successCount = userBatchInsertService.generateAndBatchInsert(count, Math.min(count, 100));
                return ResultUtils.success("成功插入 " + successCount + " 条用户数据");
            } else {
                // 异步执行大批量数据插入
                CompletableFuture.runAsync(() -> {
                    try {
                        userBatchInsertService.generateAndBatchInsert(count, 1000);
                    } catch (Exception e) {
                        log.error("异步数据插入失败", e);
                    }
                });
                return ResultUtils.success("数据插入任务已启动，正在后台执行 " + count + " 条数据的插入。");
            }
            
        } catch (Exception e) {
            log.error("插入用户数据失败", e);
            return ResultUtils.error(50001, "插入用户数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询当前数据库中的用户数量
     */
    @GetMapping("/users/count")
    public BaseResponse<Map<String, Object>> getUserCount() {
        try {
            Long totalCount = userMapper.selectCount(null);
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalUsers", totalCount);
            result.put("message", "当前数据库中共有 " + totalCount + " 条用户数据");
            
            return ResultUtils.success(result);
            
        } catch (Exception e) {
            log.error("查询用户数量失败", e);
            return ResultUtils.error(50001, "查询用户数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户数据样例
     */
    @GetMapping("/users/sample")
    public BaseResponse<List<User>> getUserSample(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            if (limit <= 0 || limit > 100) {
                return ResultUtils.error(40000, "样例数量必须在1-100之间");
            }
            
            List<User> sampleUsers = userMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .last("LIMIT " + limit)
            );
            
            return ResultUtils.success(sampleUsers);
            
        } catch (Exception e) {
            log.error("获取用户样例失败", e);
            return ResultUtils.error(50001, "获取用户样例失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空所有用户数据（谨慎使用）
     */
    @DeleteMapping("/users/clear")
    public BaseResponse<String> clearAllUsers(@RequestParam String confirmCode) {
        try {
            // 安全确认码
            if (!"CONFIRM_CLEAR_ALL_DATA".equals(confirmCode)) {
                return ResultUtils.error(40003, "确认码错误，操作被拒绝");
            }
            
            Long beforeCount = userMapper.selectCount(null);
            
            // 执行清空操作
            userMapper.delete(null);
            
            log.warn("已清空所有用户数据，原有数据量: {}", beforeCount);
            
            return ResultUtils.success("已成功清空所有用户数据，原有 " + beforeCount + " 条数据已被删除");
            
        } catch (Exception e) {
            log.error("清空用户数据失败", e);
            return ResultUtils.error(50001, "清空用户数据失败: " + e.getMessage());
        }
    }
    

}