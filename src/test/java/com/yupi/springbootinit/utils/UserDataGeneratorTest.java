package com.yupi.springbootinit.utils;

import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户数据生成器测试类
 * 生成十万条随机用户测试数据
 */
public class UserDataGeneratorTest {

    // 常用中文姓氏
    private static final String[] SURNAMES = {
        "李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴",
        "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗",
        "梁", "宋", "郑", "谢", "韩", "唐", "冯", "于", "董", "萧",
        "程", "曹", "袁", "邓", "许", "傅", "沈", "曾", "彭", "吕",
        "苏", "卢", "蒋", "蔡", "贾", "丁", "魏", "薛", "叶", "阎"
    };

    // 常用中文名字
    private static final String[] GIVEN_NAMES = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
        "洋", "勇", "艳", "杰", "涛", "明", "超", "秀兰", "霞", "平",
        "刚", "桂英", "建华", "文", "华", "金凤", "素华", "春梅", "玉兰", "建国",
        "国强", "桂兰", "秀梅", "丽娟", "建军", "国华", "玉梅", "志强", "秀珍", "志明",
        "春花", "志华", "玉珍", "春兰", "红梅", "国庆", "志刚", "春英", "国平", "桂花",
        "小明", "小红", "小华", "小丽", "小强", "小军", "小燕", "小芳", "小敏", "小静",
        "晓东", "晓明", "晓华", "晓丽", "晓燕", "晓芳", "晓敏", "晓静", "晓刚", "晓强",
        "雨涵", "雨欣", "雨萱", "雨琪", "雨薇", "雨婷", "雨洁", "雨晴", "雨蒙", "雨桐",
        "思涵", "思雨", "思琪", "思萱", "思薇", "思婷", "思洁", "思晴", "思蒙", "思桐",
        "梦涵", "梦雨", "梦琪", "梦萱", "梦薇", "梦婷", "梦洁", "梦晴", "梦蒙", "梦桐"
    };

    // 技能标签池
    private static final String[] SKILL_TAGS = {
        "Java", "Python", "JavaScript", "C++", "Go", "Rust", "TypeScript", "PHP", "C#", "Swift",
        "前端开发", "后端开发", "全栈开发", "移动开发", "数据分析", "机器学习", "人工智能", "区块链",
        "云计算", "大数据", "DevOps", "测试开发", "产品设计", "UI设计", "UX设计", "项目管理",
        "技术写作", "开源贡献", "算法竞赛", "系统架构", "数据库设计", "网络安全", "游戏开发",
        "嵌入式开发", "桌面应用", "Web开发", "API设计", "微服务", "容器化", "自动化测试",
        "性能优化", "代码审查", "技术分享", "团队协作", "敏捷开发", "持续集成", "监控运维",
        "数据挖掘", "深度学习", "计算机视觉", "自然语言处理", "推荐系统", "搜索引擎", "分布式系统",
        "高并发", "高可用", "负载均衡", "缓存优化", "消息队列", "服务治理", "链路追踪",
        "技术调研", "架构设计", "代码重构", "技术选型", "性能调优", "故障排查", "技术培训",
        "开源维护", "技术博客", "技术演讲", "创新思维", "问题解决", "学习能力", "沟通协调"
    };

    // 手机号前缀（中国移动、联通、电信）
    private static final String[] PHONE_PREFIXES = {
        "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
        "147", "150", "151", "152", "153", "155", "156", "157", "158", "159",
        "180", "181", "182", "183", "184", "185", "186", "187", "188", "189",
        "170", "171", "172", "173", "174", "175", "176", "177", "178", "179"
    };

    private final Random random = new Random();

    @Test
    public void generateUserTestData() {
        try {
            generateUsersToFile(100000, "d:\\Program code\\java\\valuedfriends\\sql\\insert_100k_test_data.sql");
            System.out.println("成功生成十万条用户测试数据！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成指定数量的用户数据到SQL文件
     */
    public void generateUsersToFile(int count, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("-- 用户表测试数据插入语句\n");
            writer.write("-- 生成" + count + "条随机用户数据\n");
            writer.write("-- 数据格式与现有测试数据保持一致\n\n");
            writer.write("use valuesfriends;\n\n");
            
            writer.write("INSERT INTO\n");
            writer.write("    user (\n");
            writer.write("        username,\n");
            writer.write("        userAccount,\n");
            writer.write("        avatarUrl,\n");
            writer.write("        gender,\n");
            writer.write("        userPassword,\n");
            writer.write("        phone,\n");
            writer.write("        email,\n");
            writer.write("        userStatus,\n");
            writer.write("        userRole,\n");
            writer.write("        planetCode,\n");
            writer.write("        tags\n");
            writer.write("    )\n");
            writer.write("VALUES\n");

            Set<String> usedAccounts = new HashSet<>();
            Set<String> usedPhones = new HashSet<>();
            Set<String> usedEmails = new HashSet<>();
            Set<String> usedPlanetCodes = new HashSet<>();

            for (int i = 0; i < count; i++) {
                String username = generateRandomName();
                String userAccount = generateUniqueUserAccount(username, usedAccounts, i + 1);
                String avatarUrl = "https://example.com/avatar/" + (i + 1) + ".jpg";
                int gender = random.nextInt(2); // 0-女性，1-男性
                String userPassword = "b0dd3697a192885d7c055db46155b26a"; // MD5(123456)
                String phone = generateUniquePhone(usedPhones);
                String email = generateUniqueEmail(userAccount, usedEmails);
                int userStatus = 0; // 正常状态
                int userRole = random.nextInt(100) < 5 ? 1 : 0; // 5%概率为管理员
                String planetCode = generateUniquePlanetCode(usedPlanetCodes, i + 1);
                String tags = generateRandomTags();

                writer.write("    (\n");
                writer.write("        '" + username + "',\n");
                writer.write("        '" + userAccount + "',\n");
                writer.write("        '" + avatarUrl + "',\n");
                writer.write("        " + gender + ",\n");
                writer.write("        '" + userPassword + "',\n");
                writer.write("        '" + phone + "',\n");
                writer.write("        '" + email + "',\n");
                writer.write("        " + userStatus + ",\n");
                writer.write("        " + userRole + ",\n");
                writer.write("        '" + planetCode + "',\n");
                writer.write("        '" + tags + "'\n");
                
                if (i < count - 1) {
                    writer.write("    ),\n");
                } else {
                    writer.write("    );\n");
                }

                // 每1000条数据输出一次进度
                if ((i + 1) % 1000 == 0) {
                    System.out.println("已生成 " + (i + 1) + " 条数据...");
                }
            }

            writer.write("\n-- 数据说明：\n");
            writer.write("-- 1. username: 随机生成的中文姓名\n");
            writer.write("-- 2. userAccount: 基于姓名拼音生成的唯一账号\n");
            writer.write("-- 3. avatarUrl: 模拟头像链接地址\n");
            writer.write("-- 4. gender: 0-女性，1-男性，随机分布\n");
            writer.write("-- 5. userPassword: 使用MD5加密的密码（原密码为123456）\n");
            writer.write("-- 6. phone: 符合中国手机号码格式的11位数字\n");
            writer.write("-- 7. email: 基于账号生成的邮箱地址\n");
            writer.write("-- 8. userStatus: 0-正常状态\n");
            writer.write("-- 9. userRole: 0-普通用户，1-管理员（5%概率）\n");
            writer.write("-- 10. planetCode: 唯一的星球编号\n");
            writer.write("-- 11. tags: 随机技能标签的JSON数组\n");
        }
    }

    /**
     * 生成随机中文姓名
     */
    private String generateRandomName() {
        String surname = SURNAMES[random.nextInt(SURNAMES.length)];
        String givenName;
        
        if (random.nextBoolean()) {
            // 单字名
            givenName = GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
        } else {
            // 双字名
            givenName = GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)] + 
                       GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
        }
        
        return surname + givenName;
    }

    /**
     * 生成唯一的用户账号
     */
    private String generateUniqueUserAccount(String username, Set<String> usedAccounts, int index) {
        String pinyin = convertToPinyin(username);
        String baseAccount = pinyin.toLowerCase();
        
        String account = baseAccount + String.format("%06d", index);
        
        // 确保账号唯一
        while (usedAccounts.contains(account)) {
            account = baseAccount + String.format("%06d", index + random.nextInt(1000));
        }
        
        usedAccounts.add(account);
        return account;
    }

    /**
     * 简单的中文转拼音（仅处理常见字符）
     */
    private String convertToPinyin(String chinese) {
        Map<String, String> pinyinMap = new HashMap<>();
        // 姓氏拼音映射
        pinyinMap.put("李", "li"); pinyinMap.put("王", "wang"); pinyinMap.put("张", "zhang");
        pinyinMap.put("刘", "liu"); pinyinMap.put("陈", "chen"); pinyinMap.put("杨", "yang");
        pinyinMap.put("赵", "zhao"); pinyinMap.put("黄", "huang"); pinyinMap.put("周", "zhou");
        pinyinMap.put("吴", "wu"); pinyinMap.put("徐", "xu"); pinyinMap.put("孙", "sun");
        pinyinMap.put("胡", "hu"); pinyinMap.put("朱", "zhu"); pinyinMap.put("高", "gao");
        pinyinMap.put("林", "lin"); pinyinMap.put("何", "he"); pinyinMap.put("郭", "guo");
        pinyinMap.put("马", "ma"); pinyinMap.put("罗", "luo"); pinyinMap.put("梁", "liang");
        pinyinMap.put("宋", "song"); pinyinMap.put("郑", "zheng"); pinyinMap.put("谢", "xie");
        pinyinMap.put("韩", "han"); pinyinMap.put("唐", "tang"); pinyinMap.put("冯", "feng");
        
        // 常见名字拼音映射
        pinyinMap.put("伟", "wei"); pinyinMap.put("芳", "fang"); pinyinMap.put("娜", "na");
        pinyinMap.put("秀英", "xiuying"); pinyinMap.put("敏", "min"); pinyinMap.put("静", "jing");
        pinyinMap.put("丽", "li"); pinyinMap.put("强", "qiang"); pinyinMap.put("磊", "lei");
        pinyinMap.put("军", "jun"); pinyinMap.put("洋", "yang"); pinyinMap.put("勇", "yong");
        pinyinMap.put("艳", "yan"); pinyinMap.put("杰", "jie"); pinyinMap.put("涛", "tao");
        pinyinMap.put("明", "ming"); pinyinMap.put("超", "chao"); pinyinMap.put("华", "hua");
        pinyinMap.put("文", "wen"); pinyinMap.put("平", "ping"); pinyinMap.put("刚", "gang");
        pinyinMap.put("小", "xiao"); pinyinMap.put("红", "hong"); pinyinMap.put("东", "dong");
        pinyinMap.put("晓", "xiao"); pinyinMap.put("雨", "yu"); pinyinMap.put("思", "si");
        pinyinMap.put("梦", "meng"); pinyinMap.put("涵", "han"); pinyinMap.put("欣", "xin");
        pinyinMap.put("萱", "xuan"); pinyinMap.put("琪", "qi"); pinyinMap.put("薇", "wei");
        pinyinMap.put("婷", "ting"); pinyinMap.put("洁", "jie"); pinyinMap.put("晴", "qing");
        pinyinMap.put("蒙", "meng"); pinyinMap.put("桐", "tong");
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            String character = chinese.substring(i, i + 1);
            String pinyin = pinyinMap.get(character);
            if (pinyin != null) {
                result.append(pinyin);
            } else {
                // 如果没有映射，使用字符的hashCode生成
                result.append("char").append(Math.abs(character.hashCode() % 1000));
            }
        }
        return result.toString();
    }

    /**
     * 生成唯一的手机号
     */
    private String generateUniquePhone(Set<String> usedPhones) {
        String phone;
        do {
            String prefix = PHONE_PREFIXES[random.nextInt(PHONE_PREFIXES.length)];
            String suffix = String.format("%08d", random.nextInt(100000000));
            phone = prefix + suffix;
        } while (usedPhones.contains(phone));
        
        usedPhones.add(phone);
        return phone;
    }

    /**
     * 生成唯一的邮箱地址
     */
    private String generateUniqueEmail(String userAccount, Set<String> usedEmails) {
        String[] domains = {"example.com", "test.com", "demo.com", "sample.com", "mock.com"};
        String email;
        do {
            String domain = domains[random.nextInt(domains.length)];
            email = userAccount + "@" + domain;
        } while (usedEmails.contains(email));
        
        usedEmails.add(email);
        return email;
    }

    /**
     * 生成唯一的星球编号
     */
    private String generateUniquePlanetCode(Set<String> usedPlanetCodes, int index) {
        String planetCode;
        do {
            planetCode = "YX" + String.format("%06d", index + random.nextInt(1000));
        } while (usedPlanetCodes.contains(planetCode));
        
        usedPlanetCodes.add(planetCode);
        return planetCode;
    }

    /**
     * 生成随机技能标签JSON数组
     */
    private String generateRandomTags() {
        int tagCount = random.nextInt(5) + 1; // 1-5个标签
        Set<String> selectedTags = new HashSet<>();
        
        while (selectedTags.size() < tagCount) {
            String tag = SKILL_TAGS[random.nextInt(SKILL_TAGS.length)];
            selectedTags.add(tag);
        }
        
        StringBuilder jsonArray = new StringBuilder("[");
        String[] tagsArray = selectedTags.toArray(new String[0]);
        for (int i = 0; i < tagsArray.length; i++) {
            jsonArray.append('"').append(tagsArray[i]).append('"');
            if (i < tagsArray.length - 1) {
                jsonArray.append(", ");
            }
        }
        jsonArray.append("]");
        
        return jsonArray.toString();
    }
}