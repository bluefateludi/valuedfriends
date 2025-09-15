package com.yupi.springbootinit.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 简化版数据生成脚本
 * 直接运行main方法生成十万条测试数据
 */
public class GenerateTestDataScript {
    
    public static void main(String[] args) {
        try {
            System.out.println("开始生成十万条用户测试数据...");
            generateTestData();
            System.out.println("数据生成完成！文件保存在: d:\\Program code\\java\\valuedfriends\\sql\\insert_100k_test_data.sql");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void generateTestData() throws IOException {
        String filePath = "d:\\Program code\\java\\valuedfriends\\sql\\insert_100k_test_data.sql";
        
        // 姓氏和名字数组
        String[] surnames = {"李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴", "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"};
        String[] givenNames = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋", "勇", "艳", "杰", "涛", "明", "超", "华", "文", "平", "刚", "小明", "小红", "小华", "小丽", "晓东", "晓明", "雨涵", "思涵", "梦涵"};
        
        // 技能标签
        String[] tags = {"Java", "Python", "JavaScript", "前端开发", "后端开发", "数据分析", "机器学习", "云计算", "大数据", "项目管理", "UI设计", "算法", "系统架构", "DevOps", "测试开发"};
        
        // 手机号前缀
        String[] phonePrefixes = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "150", "151", "152", "153", "155", "156", "157", "158", "159", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};
        
        Random random = new Random();
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // 写入文件头部
            writer.write("-- 用户表测试数据插入语句\n");
            writer.write("-- 生成100000条随机用户数据\n");
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
            
            // 生成100000条数据
            for (int i = 0; i < 100000; i++) {
                // 生成随机姓名
                String surname = surnames[random.nextInt(surnames.length)];
                String givenName = givenNames[random.nextInt(givenNames.length)];
                String username = surname + givenName;
                
                // 生成用户账号
                String userAccount = "user" + String.format("%06d", i + 1);
                
                // 头像URL
                String avatarUrl = "https://example.com/avatar/" + (i + 1) + ".jpg";
                
                // 随机性别
                int gender = random.nextInt(2);
                
                // 固定密码（MD5加密的123456）
                String userPassword = "b0dd3697a192885d7c055db46155b26a";
                
                // 生成手机号
                String phonePrefix = phonePrefixes[random.nextInt(phonePrefixes.length)];
                String phoneSuffix = String.format("%08d", random.nextInt(100000000));
                String phone = phonePrefix + phoneSuffix;
                
                // 生成邮箱
                String email = userAccount + "@example.com";
                
                // 用户状态和角色
                int userStatus = 0;
                int userRole = random.nextInt(100) < 5 ? 1 : 0; // 5%概率为管理员
                
                // 星球编号
                String planetCode = "YX" + String.format("%06d", i + 1);
                
                // 随机标签
                int tagCount = random.nextInt(3) + 1; // 1-3个标签
                Set<String> selectedTags = new HashSet<>();
                while (selectedTags.size() < tagCount) {
                    selectedTags.add(tags[random.nextInt(tags.length)]);
                }
                
                StringBuilder tagsJson = new StringBuilder("[");
                String[] tagsArray = selectedTags.toArray(new String[0]);
                for (int j = 0; j < tagsArray.length; j++) {
                    tagsJson.append('"').append(tagsArray[j]).append('"');
                    if (j < tagsArray.length - 1) {
                        tagsJson.append(", ");
                    }
                }
                tagsJson.append("]");
                
                // 写入数据行
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
                writer.write("        '" + tagsJson.toString() + "'\n");
                
                if (i < 99999) {
                    writer.write("    ),\n");
                } else {
                    writer.write("    );\n");
                }
                
                // 每1000条输出进度
                if ((i + 1) % 1000 == 0) {
                    System.out.println("已生成 " + (i + 1) + " 条数据...");
                }
            }
            
            // 写入说明
            writer.write("\n-- 数据说明：\n");
            writer.write("-- 1. username: 随机生成的中文姓名\n");
            writer.write("-- 2. userAccount: user + 6位数字编号\n");
            writer.write("-- 3. avatarUrl: 模拟头像链接地址\n");
            writer.write("-- 4. gender: 0-女性，1-男性，随机分布\n");
            writer.write("-- 5. userPassword: MD5加密的密码（原密码为123456）\n");
            writer.write("-- 6. phone: 符合中国手机号码格式\n");
            writer.write("-- 7. email: 基于账号生成的邮箱地址\n");
            writer.write("-- 8. userStatus: 0-正常状态\n");
            writer.write("-- 9. userRole: 0-普通用户，1-管理员（5%概率）\n");
            writer.write("-- 10. planetCode: YX + 6位数字编号\n");
            writer.write("-- 11. tags: 随机技能标签的JSON数组\n");
        }
    }
}