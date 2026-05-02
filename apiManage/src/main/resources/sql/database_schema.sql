-- ============================================
-- 驴友 APP 平台数据库设计
-- 数据库名称：lvyoubaba
-- 创建日期：2026-03-31
-- 说明：基于技术文档需求的完整数据库设计（MySQL 5.7 兼容版本）
-- ===========================================

-- ============================================
-- 1. 用户表 (user)
-- 承载个人中心模块，存储用户基础信息
-- ============================================
CREATE TABLE user (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
  nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称（显示名称）',
  avatar_url VARCHAR(500) DEFAULT NULL COMMENT '头像 URL',
  gender TINYINT(1) DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  wechat VARCHAR(50) DEFAULT NULL COMMENT '微信号',
  signature VARCHAR(200) DEFAULT NULL COMMENT '个性签名',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  KEY idx_phone (phone),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 景点表 (scenic_spot)
-- 官方景点推荐模块，存储景点信息
-- ============================================
CREATE TABLE scenic_spot (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '景点 ID',
  name VARCHAR(100) NOT NULL COMMENT '景点名称',
  cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图片 URL',
  images TEXT COMMENT '图片集 URL（多张逗号分隔或 JSON）',
  description TEXT COMMENT '景点介绍',
  location VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
  latitude DECIMAL(10,6) DEFAULT NULL COMMENT '纬度（MySQL 5.7 兼容，精确到米级）',
  longitude DECIMAL(11,6) DEFAULT NULL COMMENT '经度（MySQL 5.7 兼容，精确到米级）',
  city VARCHAR(50) DEFAULT NULL COMMENT '所属城市',
  province VARCHAR(50) DEFAULT NULL COMMENT '所属省份',
  ticket_price DECIMAL(10,2) DEFAULT NULL COMMENT '门票价格',
  opening_hours VARCHAR(100) DEFAULT NULL COMMENT '开放时间',
  best_season VARCHAR(100) DEFAULT NULL COMMENT '最佳游玩季节',
  play_duration INT(11) DEFAULT NULL COMMENT '建议游玩时长（分钟）',
  tags VARCHAR(500) DEFAULT NULL COMMENT '标签（逗号分隔如：登山、观景、露营）',
  view_count INT(11) NOT NULL DEFAULT 0 COMMENT '浏览次数',
  like_count INT(11) NOT NULL DEFAULT 0 COMMENT '点赞次数',
  is_hot TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否热门 0-否 1-是',
  is_recommend TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否推荐 0-否 1-是',
  status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_city (city),
  KEY idx_province (province),
  KEY idx_is_hot (is_hot),
  KEY idx_is_recommend (is_recommend),
  KEY idx_view_count (view_count),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- ============================================
-- 3. 约伴表 (team)
-- 约伴组队核心模块，存储约伴信息
-- ============================================
CREATE TABLE team (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '约伴 ID',
  user_id BIGINT(20) NOT NULL COMMENT '发起人用户 ID',
  title VARCHAR(100) NOT NULL COMMENT '约伴标题',
  content TEXT COMMENT '约伴详情描述',
  scenic_spot_id BIGINT(20) DEFAULT NULL COMMENT '关联景点 ID',
  destination VARCHAR(200) DEFAULT NULL COMMENT '目的地',
  start_time DATETIME DEFAULT NULL COMMENT '出发时间',
  end_time DATETIME DEFAULT NULL COMMENT '结束时间',
  duration INT(11) DEFAULT NULL COMMENT '预计天数',
  max_members INT(11) DEFAULT NULL COMMENT '最大人数',
  current_members INT(11) NOT NULL DEFAULT 1 COMMENT '当前人数',
  budget DECIMAL(10,2) DEFAULT NULL COMMENT '预算（人均）',
  activity_type VARCHAR(50) DEFAULT NULL COMMENT '活动类型（如：登山、徒步、露营）',
  difficulty_level TINYINT(1) DEFAULT 1 COMMENT '难度等级 1-简单 2-中等 3-困难',
  requirements VARCHAR(500) DEFAULT NULL COMMENT '参与要求',
  contact_info VARCHAR(200) DEFAULT NULL COMMENT '联系方式',
  cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
  view_count INT(11) NOT NULL DEFAULT 0 COMMENT '浏览次数',
  signup_count INT(11) NOT NULL DEFAULT 0 COMMENT '报名人数',
  is_full TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已满员 0-否 1-是',
  status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-取消 1-招募中 2-进行中 3-已完成',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_scenic_spot_id (scenic_spot_id),
  KEY idx_destination (destination),
  KEY idx_start_time (start_time),
  KEY idx_activity_type (activity_type),
  KEY idx_status (status),
  KEY idx_create_time (create_time),
  CONSTRAINT fk_team_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_team_scenic_spot FOREIGN KEY (scenic_spot_id) REFERENCES scenic_spot (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='约伴表';

-- ============================================
-- 4. 动态表 (activity)
-- 轻量动态模块，存储用户分享的动态
-- ============================================
CREATE TABLE activity (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '动态 ID',
  user_id BIGINT(20) NOT NULL COMMENT '发布者用户 ID',
  content VARCHAR(2000) DEFAULT NULL COMMENT '动态内容（最多 2000 字）',
  images TEXT COMMENT '图片集 URL（JSON 格式，最多 9 张）',
  video_url VARCHAR(500) DEFAULT NULL COMMENT '视频 URL',
  scenic_spot_id BIGINT(20) DEFAULT NULL COMMENT '关联景点 ID',
  team_id BIGINT(20) DEFAULT NULL COMMENT '关联约伴 ID',
  location VARCHAR(200) DEFAULT NULL COMMENT '发布地点',
  latitude DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  longitude DECIMAL(11,6) DEFAULT NULL COMMENT '经度',
  tags VARCHAR(500) DEFAULT NULL COMMENT '预设标签（逗号分隔）',
  like_count INT(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  comment_count INT(11) NOT NULL DEFAULT 0 COMMENT '评论数',
  share_count INT(11) NOT NULL DEFAULT 0 COMMENT '分享数',
  view_count INT(11) NOT NULL DEFAULT 0 COMMENT '浏览数',
  is_top TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
  is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_scenic_spot_id (scenic_spot_id),
  KEY idx_team_id (team_id),
  KEY idx_create_time (create_time),
  KEY idx_like_count (like_count),
  KEY idx_is_top (is_top),
  CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_activity_scenic_spot FOREIGN KEY (scenic_spot_id) REFERENCES scenic_spot (id) ON DELETE SET NULL,
  CONSTRAINT fk_activity_team FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态表';

-- ============================================
-- 5. 约伴报名表 (team_signup)
-- 约伴报名关联表，存储用户报名约伴的记录
-- ============================================
CREATE TABLE team_signup (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '报名 ID',
  team_id BIGINT(20) NOT NULL COMMENT '约伴 ID',
  user_id BIGINT(20) NOT NULL COMMENT '报名用户 ID',
  message VARCHAR(500) DEFAULT NULL COMMENT '留言/备注',
  status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-拒绝 1-待确认 2-已加入 3-已取消',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_team_user (team_id, user_id),
  KEY idx_user_id (user_id),
  KEY idx_status (status),
  KEY idx_create_time (create_time),
  CONSTRAINT fk_signup_team FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE,
  CONSTRAINT fk_signup_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='约伴报名表';

-- ============================================
-- 6. 动态点赞表 (activity_like)
-- 动态点赞关联表，记录用户对动态的点赞
-- ============================================
CREATE TABLE activity_like (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '点赞 ID',
  activity_id BIGINT(20) NOT NULL COMMENT '动态 ID',
  user_id BIGINT(20) NOT NULL COMMENT '点赞用户 ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_activity_user (activity_id, user_id),
  KEY idx_user_id (user_id),
  KEY idx_create_time (create_time),
  CONSTRAINT fk_like_activity FOREIGN KEY (activity_id) REFERENCES activity (id) ON DELETE CASCADE,
  CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态点赞表';

-- ============================================
-- 7. 私信留言表 (message)
-- 用户间私信沟通功能
-- ============================================
CREATE TABLE message (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '消息 ID',
  sender_id BIGINT(20) NOT NULL COMMENT '发送者用户 ID',
  receiver_id BIGINT(20) NOT NULL COMMENT '接收者用户 ID',
  content TEXT NOT NULL COMMENT '消息内容',
  image_url VARCHAR(500) DEFAULT NULL COMMENT '图片 URL（可选）',
  team_id BIGINT(20) DEFAULT NULL COMMENT '关联约伴 ID（可选）',
  is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_sender_id (sender_id),
  KEY idx_receiver_id (receiver_id),
  KEY idx_team_id (team_id),
  KEY idx_is_read (is_read),
  KEY idx_create_time (create_time),
  CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES user (id) ON DELETE CASCADE,
  CONSTRAINT fk_message_team FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私信留言表';

-- ============================================
-- 8. 动态评论表 (activity_comment)
-- 动态评论功能（补充）
-- ============================================
CREATE TABLE activity_comment (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论 ID',
  activity_id BIGINT(20) NOT NULL COMMENT '动态 ID',
  user_id BIGINT(20) NOT NULL COMMENT '评论用户 ID',
  parent_id BIGINT(20) DEFAULT NULL COMMENT '父评论 ID（用于回复）',
  content VARCHAR(500) NOT NULL COMMENT '评论内容',
  like_count INT(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  is_delete TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_activity_id (activity_id),
  KEY idx_user_id (user_id),
  KEY idx_parent_id (parent_id),
  KEY idx_create_time (create_time),
  CONSTRAINT fk_comment_activity FOREIGN KEY (activity_id) REFERENCES activity (id) ON DELETE CASCADE,
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态评论表';

-- ============================================
-- 初始化数据 - 示例数据
-- ============================================

-- 插入示例用户
INSERT INTO user (username, nickname, avatar_url, gender, phone, email, signature, status) VALUES
('zhangsan', '张三', 'https://picsum.photos/seed/user1/100/100', 1, '13800138001', 'zhangsan@qq.com', '热爱户外运动的背包客', 1),
('lisi', '李四', 'https://picsum.photos/seed/user2/100/100', 1, '13800138002', 'lisi@gmail.com', '用脚步丈量世界', 1),
('wangwu', '王五', 'https://picsum.photos/seed/user3/100/100', 2, '13800138003', 'wangwu@163.com', '旅行是我的生活方式', 1),
('zhaoliu', '赵六', 'https://picsum.photos/seed/user4/100/100', 1, '13800138004', 'zhaoliu@hotmail.com', '寻找诗和远方', 0),
('xiaomei', '小美', 'https://picsum.photos/seed/user5/100/100', 2, '13900139001', 'xiaomei@qq.com', '喜欢拍照的旅行者', 1),
('laowang', '老王', 'https://picsum.photos/seed/user6/100/100', 1, '13900139002', 'laowang@gmail.com', '走遍中国', 1),
('xiaohong', '小红', 'https://picsum.photos/seed/user7/100/100', 2, '13900139003', 'xiaohong@163.com', '在路上', 1),
('david', 'David', 'https://picsum.photos/seed/user8/100/100', 1, '13700137001', 'david@outlook.com', 'Explore the world', 1),
('lucy', 'Lucy', 'https://picsum.photos/seed/user9/100/100', 2, '13700137002', 'lucy@gmail.com', 'Love traveling', 0),
('mike', 'Mike', 'https://picsum.photos/seed/user10/100/100', 1, '13700137003', 'mike@qq.com', 'Adventure awaits', 1);

-- 插入示例景点
INSERT INTO scenic_spot (name, cover_image, images, description, location, city, province, latitude, longitude, ticket_price, opening_hours, best_season, play_duration, tags, view_count, like_count, is_hot, is_recommend, status) VALUES
('黄山风景区', 'https://picsum.photos/seed/scenic1/400/300', '["https://picsum.photos/seed/scenic1/400/300"]', '世界文化与自然双重遗产，天下第一奇山，以奇松、怪石、云海、温泉四绝著称', '安徽省黄山市', '黄山市', '安徽省', 30.132067, 118.126161, 190.00, '06:00-17:30', '四季皆宜', 480, '登山,观景,日出', 28560, 3250, 1, 1, 1),
('稻城亚丁', 'https://picsum.photos/seed/scenic2/400/300', '["https://picsum.photos/seed/scenic2/400/300"]', '被誉为香格里拉之魂，蓝色星球上的最后一片净土', '四川省甘孜藏族自治州稻城县', '甘孜州', '四川省', 28.459824, 100.335753, 266.00, '07:00-18:00', '9-10月', 600, '徒步,雪山,高原', 19856, 2890, 1, 1, 1),
('四姑娘山', 'https://picsum.photos/seed/scenic3/400/300', '["https://picsum.photos/seed/scenic3/400/300"]', '东方阿尔卑斯山，蜀山之后，国家级风景名胜区', '四川省阿坝藏族羌族自治州小金县', '阿坝州', '四川省', 30.999045, 102.842972, 80.00, '08:00-17:00', '6-10月', 360, '登山,徒步,露营', 8623, 980, 0, 1, 1),
('丽江古城', 'https://picsum.photos/seed/scenic4/400/300', '["https://picsum.photos/seed/scenic4/400/300"]', '世界文化遗产，中国历史文化名城，茶马古道上的重镇', '云南省丽江市', '丽江市', '云南省', 26.872108, 100.229978, 50.00, '全天开放', '四季皆宜', 240, '古城,文化,摄影', 35230, 4120, 1, 1, 1),
('西湖', 'https://picsum.photos/seed/scenic5/400/300', '["https://picsum.photos/seed/scenic5/400/300"]', '世界文化遗产，欲把西湖比西子，淡妆浓抹总相宜', '浙江省杭州市', '杭州市', '浙江省', 30.245803, 120.145465, 0.00, '全天开放', '四季皆宜', 180, '湖泊,园林,文化', 58960, 6850, 1, 1, 1),
('张家界国家森林公园', 'https://picsum.photos/seed/scenic6/400/300', '["https://picsum.photos/seed/scenic6/400/300"]', '中国第一个国家森林公园，世界自然遗产，阿凡达取景地', '湖南省张家界市', '张家界市', '湖南省', 29.325601, 110.480514, 225.00, '07:00-18:00', '4-10月', 300, '森林,奇峰,玻璃栈道', 21250, 2680, 1, 1, 1),
('九寨沟', 'https://picsum.photos/seed/scenic7/400/300', '["https://picsum.photos/seed/scenic7/400/300"]', '九寨归来不看水，世界自然遗产，国家级自然保护区', '四川省阿坝州九寨沟县', '阿坝州', '四川省', 33.260028, 103.918629, 169.00, '08:00-17:00', '9-10月', 420, '湖泊,瀑布,彩林', 32100, 3980, 1, 1, 1),
('鼓浪屿', 'https://picsum.photos/seed/scenic8/400/300', '["https://picsum.photos/seed/scenic8/400/300"]', '世界文化遗产，海上花园，钢琴之岛', '福建省厦门市', '厦门市', '福建省', 24.446449, 118.082665, 35.00, '全天开放', '四季皆宜', 300, '海岛,建筑,音乐', 25680, 3120, 1, 1, 1),
('八达岭长城', 'https://picsum.photos/seed/scenic9/400/300', '["https://picsum.photos/seed/scenic9/400/300"]', '不到长城非好汉，世界文化遗产，明长城中保存最好的一段', '北京市延庆区', '北京市', '北京市', 40.359580, 116.020111, 40.00, '06:30-16:30', '春秋季', 180, '长城,历史,登山', 45200, 5280, 1, 1, 1),
('故宫博物院', 'https://picsum.photos/seed/scenic10/400/300', '["https://picsum.photos/seed/scenic10/400/300"]', '紫禁城，世界上现存规模最大保存最完整的木质古建筑群', '北京市东城区', '北京市', '北京市', 39.916345, 116.397155, 60.00, '08:30-17:00', '四季皆宜', 240, '宫殿,历史,文物', 52300, 6120, 1, 1, 1);

-- 插入示例约伴
INSERT INTO team (user_id, title, content, scenic_spot_id, destination, start_time, end_time, duration, max_members, current_members, budget, activity_type, difficulty_level, contact_info, cover_image, view_count, signup_count, is_full, status) VALUES
(1, '川西小环线5日游', '一起去川西看雪山草原，体验藏族风情，费用AA制，欢迎热爱旅行的朋友加入！', 2, '四川省甘孜州', '2026-06-15 08:00:00', '2026-06-19 18:00:00', 5, 5, 2, 2500.00, '登山', 1, '138****8888', 'https://picsum.photos/seed/team1/400/300', 1256, 2, 0, 1),
(2, '云南大理丽江游', '云南深度游，走遍大理、丽江、香格里拉，体验少数民族文化。', 4, '丽江', '2026-07-01 09:00:00', '2026-07-07 20:00:00', 7, 8, 6, 3500.00, '观光', 1, '139****9999', 'https://picsum.photos/seed/team2/400/300', 2380, 6, 1, 2),
(3, '西藏拉萨朝圣之旅', '前往世界屋脊，感受布达拉宫的神圣，体验藏族文化。', NULL, '拉萨', '2026-08-10 06:00:00', '2026-08-19 20:00:00', 10, 6, 3, 5000.00, '朝圣', 2, '137****7777', 'https://picsum.photos/seed/team3/400/300', 3560, 3, 0, 1),
(4, '张家界玻璃栈道探险', '挑战玻璃栈道，感受惊险刺激，观赏张家界独特的砂岩峰林地貌。', 6, '张家界', '2026-05-20 07:00:00', '2026-05-22 18:00:00', 3, 10, 8, 1200.00, '探险', 2, '136****6666', 'https://picsum.photos/seed/team4/400/300', 1890, 8, 0, 3),
(5, '杭州西湖周边徒步', '西湖周边徒步，欣赏西湖美景，感受江南水乡的韵味。', 5, '杭州', '2026-06-05 08:00:00', '2026-06-06 18:00:00', 2, 12, 4, 800.00, '徒步', 1, '135****5555', 'https://picsum.photos/seed/team5/400/300', 980, 4, 0, 1),
(6, '新疆喀纳斯金秋摄影之旅', '金秋时节，去喀纳斯拍摄最美的秋景，专业摄影师带队指导。', NULL, '喀纳斯', '2026-09-20 06:00:00', '2026-09-27 20:00:00', 8, 8, 5, 6800.00, '摄影', 2, '134****4444', 'https://picsum.photos/seed/team6/400/300', 4230, 5, 0, 1);

-- 插入示例动态
INSERT INTO activity (user_id, content, images, scenic_spot_id, location, tags, like_count, comment_count, view_count) VALUES
(1, '今天去了黄山，风景真的太美了！云海缭绕，宛如仙境。推荐大家都来看看！', '["https://picsum.photos/seed/act1/400/300"]', 1, '黄山风景区', '登山,云海,日出', 128, 32, 2560),
(2, '稻城亚丁徒步之旅完成，虽然很累但值得！一路上的美景让人流连忘返。', '["https://picsum.photos/seed/act2/400/300","https://picsum.photos/seed/act3/400/300"]', 2, '稻城亚丁景区', '徒步,雪山,高原', 256, 45, 3890),
(3, '丽江古城夜景太美了，晚上的酒吧一条街特别热闹！在这里可以感受到不一样的纳西文化。', '["https://picsum.photos/seed/act4/400/300"]', 4, '丽江古城', '古城,夜景,文化', 89, 18, 1560),
(4, '西藏之行圆满结束，布达拉宫太震撼了！站在布达拉宫前，感受到了信仰的力量。', '["https://picsum.photos/seed/act5/400/300"]', NULL, '布达拉宫', '西藏,朝圣,文化', 312, 67, 5230),
(5, '今天去了西湖，断桥残雪真的很有意境，推荐大家来感受一下江南水乡的韵味！', '["https://picsum.photos/seed/act6/400/300"]', 5, '西湖', '西湖,江南,园林', 178, 42, 2890),
(6, '张家界的玻璃栈道太刺激了，站在上面俯瞰整个森林公园，太壮观了！', '["https://picsum.photos/seed/act7/400/300"]', 6, '张家界国家森林公园', '玻璃栈道,森林公园,探险', 234, 56, 4120);

-- ============================================
-- MySQL 5.7 兼容性说明
-- ============================================
-- ✅ 已修正项目：
-- 1. 经纬度精度调整为 DECIMAL(10,6) 和 DECIMAL(11,6)，完全兼容 MySQL 5.7
-- 2. 所有表使用 InnoDB 引擎，支持外键约束
-- 3. utf8mb4 字符集（MySQL 5.5.3+ 支持）
-- 4. 外键约束添加了 ON DELETE 行为定义
--
-- ⚠️ 注意事项：
-- 1. 确保 MySQL 启用了外键支持：SET FOREIGN_KEY_CHECKS=1;
-- 2. 如果导入失败，可以先删除旧表：DROP TABLE IF EXISTS 表名;
-- 3. 时区设置可能影响 DATETIME，建议设置：SET time_zone='+08:00';
--
-- ============================================
-- 扩展性说明
-- ============================================
-- 1. 预留了多个扩展字段，便于后续功能迭代
-- 2. 使用 utf8mb4 字符集，支持 emoji 等特殊字符
-- 3. 所有表都包含 create_time 和 update_time 便于数据追踪
-- 4. 状态字段统一使用 TINYINT，便于扩展状态值
-- 5. 金额字段使用 DECIMAL 类型，避免精度丢失