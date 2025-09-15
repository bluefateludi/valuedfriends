package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.mapper.UserMapper;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.UserBatchInsertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户批量插入服务实现类
 * 使用MyBatis-Plus的真正批量插入功能
 * 
 * @author AI Assistant
 */
@Service
@Slf4j
public class UserBatchInsertServiceImpl extends ServiceImpl<UserMapper, User> implements UserBatchInsertService {

    private static final String SALT = "yupi";
    private final Random random = new Random();
    
    // 数据生成库
    private final String[] surnames = {
        "李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴",
        "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗",
        "梁", "宋", "郑", "谢", "韩", "唐", "冯", "于", "董", "萧"
    };
    
    private final String[] givenNames = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
        "洋", "勇", "艳", "杰", "涛", "明", "超", "秀兰", "霞", "平",
        "刚", "桂英", "建华", "文", "华", "金凤", "素华", "春梅", "玉兰", "建国"
    };
    
    private final String[] skillTags = {
        "Java", "Python", "JavaScript", "C++", "Go", "Rust", "TypeScript",
        "React", "Vue", "Angular", "Spring", "MyBatis", "Redis", "MySQL",
        "MongoDB", "Docker", "Kubernetes", "微服务", "分布式", "大数据",
        "机器学习", "人工智能", "区块链", "云计算", "DevOps", "前端开发",
        "后端开发", "全栈开发", "移动开发", "游戏开发", "数据分析", "产品经理"
    };

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertUsers(List<User> users, int batchSize) {
        if (users == null || users.isEmpty()) {
            return 0;
        }
        
        AtomicInteger successCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        
        log.info("开始批量插入{}条用户数据，批次大小: {}", users.size(), batchSize);
        
        try {
            // 使用MyBatis-Plus的saveBatch方法进行真正的批量插入
            boolean result = this.saveBatch(users, batchSize);
            
            if (result) {
                successCount.set(users.size());
                log.info("批量插入成功，共插入{}条数据", users.size());
            } else {
                log.error("批量插入失败");
            }
            
        } catch (Exception e) {
            log.error("批量插入异常，回退到逐条插入: {}", e.getMessage());
            
            // 如果批量插入失败，回退到逐条插入
            for (User user : users) {
                try {
                    boolean singleResult = this.save(user);
                    if (singleResult) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception singleException) {
                    log.warn("逐条插入用户失败: {}, 错误: {}", user.getUserAccount(), singleException.getMessage());
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("批量插入完成！耗时: {}ms, 成功: {}条, 成功率: {:.2f}%", 
            duration, successCount.get(), (double) successCount.get() / users.size() * 100);
        
        return successCount.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateAndBatchInsert(int totalCount, int batchSize) {
        if (totalCount <= 0) {
            return 0;
        }
        
        AtomicInteger totalSuccessCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        int totalBatches = (totalCount + batchSize - 1) / batchSize;
        
        log.info("开始生成并批量插入{}条用户数据，批次大小: {}, 总批次: {}", totalCount, batchSize, totalBatches);
        
        for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
            int startIndex = batchIndex * batchSize;
            int endIndex = Math.min(startIndex + batchSize, totalCount);
            int currentBatchSize = endIndex - startIndex;
            
            // 生成当前批次数据
            List<User> batchUsers = generateUserBatch(startIndex, currentBatchSize);
            
            // 批量插入当前批次
            try {
                boolean batchResult = this.saveBatch(batchUsers, currentBatchSize);
                
                if (batchResult) {
                    totalSuccessCount.addAndGet(currentBatchSize);
                    log.debug("批次{}批量插入成功，插入{}条数据", batchIndex + 1, currentBatchSize);
                } else {
                    log.warn("批次{}批量插入失败，尝试逐条插入", batchIndex + 1);
                    
                    // 回退到逐条插入
                    for (User user : batchUsers) {
                        try {
                            boolean singleResult = this.save(user);
                            if (singleResult) {
                                totalSuccessCount.incrementAndGet();
                            }
                        } catch (Exception singleException) {
                            log.warn("逐条插入用户失败: {}", user.getUserAccount());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("批次{}插入异常: {}", batchIndex + 1, e.getMessage());
                
                // 异常时回退到逐条插入
                for (User user : batchUsers) {
                    try {
                        boolean singleResult = this.save(user);
                        if (singleResult) {
                            totalSuccessCount.incrementAndGet();
                        }
                    } catch (Exception singleException) {
                        log.warn("逐条插入用户失败: {}", user.getUserAccount());
                    }
                }
            }
            
            // 进度日志
            if ((batchIndex + 1) % 10 == 0 || batchIndex == totalBatches - 1) {
                double progress = (double) (batchIndex + 1) / totalBatches * 100;
                log.info("数据生成和插入进度: {:.1f}% ({}/{}批), 已成功: {}条", 
                    progress, batchIndex + 1, totalBatches, totalSuccessCount.get());
            }
            
            // 短暂休息，避免数据库压力过大
            if (batchIndex < totalBatches - 1) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("数据插入被中断", e);
                }
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double avgSpeed = totalSuccessCount.get() > 0 ? (double) totalSuccessCount.get() / (duration / 1000.0) : 0;
        
        log.info("批量数据生成和插入完成！总耗时: {}ms, 成功: {}条, 成功率: {:.2f}%, 平均速度: {:.0f}条/秒", 
            duration, totalSuccessCount.get(), (double) totalSuccessCount.get() / totalCount * 100, avgSpeed);
        
        return totalSuccessCount.get();
    }
    
    @Override
    public void verifyInsertedData() {
        // 查询总数据量
        long totalCount = this.count();
        log.info("数据库中用户总数: {}", totalCount);
        
        if (totalCount > 0) {
            // 查询前10条数据进行验证
            List<User> sampleUsers = this.list(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .orderByDesc("create_time")
                    .last("LIMIT 10")
            );
            
            log.info("最新插入的10条用户数据样例:");
            for (User user : sampleUsers) {
                log.info("ID: {}, 账号: {}, 姓名: {}, 手机: {}, 标签: {}",
                    user.getId(), user.getUserAccount(), user.getUsername(), 
                    user.getPhone(), user.getTags());
            }
        } else {
            log.warn("数据库中没有用户数据");
        }
    }
    
    /**
     * 生成一批用户数据
     */
    private List<User> generateUserBatch(int startIndex, int batchSize) {
        List<User> users = new ArrayList<>(batchSize);
        
        for (int i = 0; i < batchSize; i++) {
            User user = generateRandomUser(startIndex + i + 1);
            users.add(user);
        }
        
        return users;
    }
    
    /**
     * 生成单个随机用户
     */
    private User generateRandomUser(int index) {
        User user = new User();
        
        // 生成用户名
        String surname = surnames[random.nextInt(surnames.length)];
        String givenName = givenNames[random.nextInt(givenNames.length)];
        user.setUsername(surname + givenName);
        
        // 生成用户账号
        user.setUserAccount("user_" + String.format("%06d", index));
        
        // 生成加密密码
        String password = "password123";
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        user.setUserPassword(encryptedPassword);
        
        // 生成其他字段
        user.setAvatarUrl("https://avatars.example.com/user_" + index + ".jpg");
        user.setGender(random.nextInt(2));
        user.setPhone(generatePhoneNumber());
        user.setEmail(user.getUserAccount() + "@example.com");
        user.setUserStatus(0);
        user.setUserRole(0);
        user.setPlanetCode(String.format("%08d", random.nextInt(100000000)));
        user.setTags(generateSkillTags());
        
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setIsDelete(0);
        
        return user;
    }
    
    /**
     * 生成随机手机号
     */
    private String generatePhoneNumber() {
        String[] prefixes = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
                           "150", "151", "152", "153", "155", "156", "157", "158", "159",
                           "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};
        
        String prefix = prefixes[random.nextInt(prefixes.length)];
        StringBuilder phone = new StringBuilder(prefix);
        
        for (int i = 0; i < 8; i++) {
            phone.append(random.nextInt(10));
        }
        
        return phone.toString();
    }
    
    /**
     * 生成技能标签
     */
    private String generateSkillTags() {
        int tagCount = random.nextInt(5) + 1; // 1-5个标签
        Set<String> selectedTags = new HashSet<>();
        
        while (selectedTags.size() < tagCount) {
            String tag = skillTags[random.nextInt(skillTags.length)];
            selectedTags.add(tag);
        }
        
        List<String> tagList = new ArrayList<>(selectedTags);
        return "[\"" + String.join("\", \"", tagList) + "\"]";
    }
}