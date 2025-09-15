-- 新增用户表测试数据插入语句
-- 生成30条具有多样化标签组合的用户数据
-- 专门用于支持推荐功能的测试需求
-- 标签覆盖技术栈、兴趣爱好、职业方向、学习阶段等多个维度

use valuesfriends;

INSERT INTO
    user (
        username,
        userAccount,
        avatarUrl,
        gender,
        userPassword,
        phone,
        email,
        userStatus,
        userRole,
        planetCode,
        tags
    )
VALUES (
        '陈思雨',
        'chensiyutech051',
        'https://example.com/avatar/51.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '13845612378',
        'chensiyutech@example.com',
        0,
        0,
        'YX051',
        '["React", "TypeScript", "前端架构", "性能优化", "音乐", "钢琴"]'
    ),
    (
        '李明轩',
        'limingxuan052',
        'https://example.com/avatar/52.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '15923456789',
        'limingxuan@example.com',
        0,
        0,
        'YX052',
        '["Go", "微服务", "Docker", "Kubernetes", "登山", "摄影", "旅行"]'
    ),
    (
        '王雅琪',
        'wangyaqi053',
        'https://example.com/avatar/53.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '18734567890',
        'wangyaqi@example.com',
        0,
        0,
        'YX053',
        '["Python", "机器学习", "数据挖掘", "TensorFlow", "瑜伽", "健身", "素食主义"]'
    ),
    (
        '张浩然',
        'zhanghaoran054',
        'https://example.com/avatar/54.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '13567890123',
        'zhanghaoran@example.com',
        0,
        0,
        'YX054',
        '["Java", "Spring Boot", "Redis", "MySQL", "篮球", "游戏", "电竞"]'
    ),
    (
        '刘诗涵',
        'liushihan055',
        'https://example.com/avatar/55.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '15678901234',
        'liushihan@example.com',
        0,
        1,
        'YX055',
        '["UI/UX设计", "Figma", "Sketch", "用户研究", "绘画", "插画", "咖啡"]'
    ),
    (
        '赵子轩',
        'zhaozixuan056',
        'https://example.com/avatar/56.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '18789012345',
        'zhaozixuan@example.com',
        0,
        0,
        'YX056',
        '["C++", "算法竞赛", "ACM", "数据结构", "象棋", "数学", "物理"]'
    ),
    (
        '孙美琳',
        'sunmeilin057',
        'https://example.com/avatar/57.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '13890123456',
        'sunmeilin@example.com',
        0,
        0,
        'YX057',
        '["Flutter", "Dart", "移动开发", "跨平台", "舞蹈", "韩语", "追剧"]'
    ),
    (
        '周俊杰',
        'zhoujunjie058',
        'https://example.com/avatar/58.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '15901234567',
        'zhoujunjie@example.com',
        0,
        0,
        'YX058',
        '["DevOps", "Jenkins", "GitLab CI", "监控", "足球", "啤酒", "烧烤"]'
    ),
    (
        '吴雨桐',
        'wuyutong059',
        'https://example.com/avatar/59.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '18012345678',
        'wuyutong@example.com',
        0,
        0,
        'YX059',
        '["Vue.js", "Nuxt.js", "前端工程化", "Webpack", "烘焙", "美食", "摄影"]'
    ),
    (
        '郑凯文',
        'zhengkaiwen060',
        'https://example.com/avatar/60.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '13123456789',
        'zhengkaiwen@example.com',
        0,
        0,
        'YX060',
        '["Rust", "系统编程", "区块链", "智能合约", "吉他", "摇滚", "独立音乐"]'
    ),
    (
        '黄思琪',
        'huangsiqi061',
        'https://example.com/avatar/61.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '15234567890',
        'huangsiqi@example.com',
        0,
        0,
        'YX061',
        '["产品经理", "用户体验", "数据分析", "A/B测试", "阅读", "心理学", "冥想"]'
    ),
    (
        '林志豪',
        'linzhihao062',
        'https://example.com/avatar/62.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '18345678901',
        'linzhihao@example.com',
        0,
        0,
        'YX062',
        '["Node.js", "Express", "MongoDB", "GraphQL", "滑板", "街舞", "嘻哈"]'
    ),
    (
        '徐梦瑶',
        'xumengyao063',
        'https://example.com/avatar/63.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '13456789012',
        'xumengyao@example.com',
        0,
        0,
        'YX063',
        '["测试工程师", "自动化测试", "Selenium", "性能测试", "瑜伽", "茶艺", "园艺"]'
    ),
    (
        '马建国',
        'majianguo064',
        'https://example.com/avatar/64.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '15567890123',
        'majianguo@example.com',
        0,
        0,
        'YX064',
        '["大数据", "Spark", "Hadoop", "Kafka", "钓鱼", "象棋", "太极"]'
    ),
    (
        '朱雨欣',
        'zhuyuxin065',
        'https://example.com/avatar/65.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '18678901234',
        'zhuyuxin@example.com',
        0,
        0,
        'YX065',
        '["iOS开发", "Swift", "SwiftUI", "ARKit", "跑步", "马拉松", "健康生活"]'
    ),
    (
        '高文博',
        'gaowenbo066',
        'https://example.com/avatar/66.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '13789012345',
        'gaowenbo@example.com',
        0,
        0,
        'YX066',
        '["网络安全", "渗透测试", "CTF", "逆向工程", "黑客文化", "科幻小说"]'
    ),
    (
        '何雅婷',
        'heyating067',
        'https://example.com/avatar/67.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '15890123456',
        'heyating@example.com',
        0,
        0,
        'YX067',
        '["数据科学", "R语言", "统计学", "可视化", "古典音乐", "小提琴", "艺术"]'
    ),
    (
        '罗志强',
        'luozhiqiang068',
        'https://example.com/avatar/68.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '18901234567',
        'luozhiqiang@example.com',
        0,
        0,
        'YX068',
        '["Unity", "游戏开发", "C#", "VR/AR", "游戏设计", "动漫", "二次元"]'
    ),
    (
        '宋雨晴',
        'songyuqing069',
        'https://example.com/avatar/69.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '13012345678',
        'songyuqing@example.com',
        0,
        1,
        'YX069',
        '["技术写作", "文档工程", "Markdown", "技术翻译", "文学", "诗歌", "写作"]'
    ),
    (
        '韩志明',
        'hanzhiming070',
        'https://example.com/avatar/70.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '15123456789',
        'hanzhiming@example.com',
        0,
        0,
        'YX070',
        '["嵌入式", "STM32", "物联网", "传感器", "无线电", "电子制作", "创客"]'
    ),
    (
        '邓小雅',
        'dengxiaoya071',
        'https://example.com/avatar/71.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '18234567890',
        'dengxiaoya@example.com',
        0,
        0,
        'YX071',
        '["Kotlin", "Android开发", "Jetpack", "Material Design", "旅行", "摄影", "美食探店"]'
    ),
    (
        '冯浩宇',
        'fenghaoyu072',
        'https://example.com/avatar/72.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '13345678901',
        'fenghaoyu@example.com',
        0,
        0,
        'YX072',
        '["云计算", "AWS", "Azure", "Terraform", "极限运动", "攀岩", "冒险"]'
    ),
    (
        '曹雨薇',
        'caoyuwei073',
        'https://example.com/avatar/73.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '15456789012',
        'caoyuwei@example.com',
        0,
        0,
        'YX073',
        '["PHP", "Laravel", "WordPress", "电商开发", "宠物", "猫咪", "动物保护"]'
    ),
    (
        '彭志华',
        'pengzhihua074',
        'https://example.com/avatar/74.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '18567890123',
        'pengzhihua@example.com',
        0,
        0,
        'YX074',
        '["Scala", "函数式编程", "Akka", "分布式系统", "围棋", "策略游戏", "思维训练"]'
    ),
    (
        '曾雨涵',
        'zengyuhan075',
        'https://example.com/avatar/75.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '13678901234',
        'zengyuhan@example.com',
        0,
        0,
        'YX075',
        '["业务分析师", "需求工程", "流程优化", "项目管理", "手工", "编织", "DIY"]'
    ),
    (
        '萧志远',
        'xiaozhiyuan076',
        'https://example.com/avatar/76.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '15789012345',
        'xiaozhiyuan@example.com',
        0,
        0,
        'YX076',
        '["Ruby", "Rails", "敏捷开发", "TDD", "咖啡", "精品咖啡", "咖啡烘焙"]'
    ),
    (
        '薛雨晨',
        'xueyuchen077',
        'https://example.com/avatar/77.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '18890123456',
        'xueyuchen@example.com',
        0,
        0,
        'YX077',
        '["前端新人", "HTML", "CSS", "JavaScript基础", "学习中", "编程入门", "技术成长"]'
    ),
    (
        '范志鹏',
        'fanzhipeng078',
        'https://example.com/avatar/78.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '13901234567',
        'fanzhipeng@example.com',
        0,
        0,
        'YX078',
        '["技术管理", "团队领导", "架构设计", "技术选型", "管理学", "领导力", "商业思维"]'
    ),
    (
        '谭雨琪',
        'tanyuqi079',
        'https://example.com/avatar/79.jpg',
        0,
        'b0dd3697a192885d7c055db46155b26a',
        '15012345678',
        'tanyuqi@example.com',
        0,
        0,
        'YX079',
        '["全栈开发", "MERN", "GraphQL", "微前端", "电影", "影评", "文艺青年"]'
    ),
    (
        '邹志伟',
        'zouzhiwei080',
        'https://example.com/avatar/80.jpg',
        1,
        'b0dd3697a192885d7c055db46155b26a',
        '18123456789',
        'zouzhiwei@example.com',
        0,
        0,
        'YX080',
        '["运维工程师", "Linux", "Shell脚本", "监控告警", "开源贡献", "技术社区", "分享精神"]'
    );

-- 新增数据说明：
-- 1. 共30个新用户，planetCode从YX051到YX080
-- 2. 标签组合具有高度差异性，涵盖：
--    - 技术栈：前端、后端、移动端、大数据、AI、安全等
--    - 职业阶段：新人、资深、管理层
--    - 兴趣爱好：音乐、运动、艺术、旅行、美食等
--    - 学习方向：算法、架构、产品、管理等
-- 3. 每个用户3-7个标签，确保推荐算法有足够的匹配维度
-- 4. 性别分布均匀，少数用户设置为管理员角色
-- 5. 联系方式和邮箱地址保持唯一性
-- 6. 密码统一使用MD5加密的123456