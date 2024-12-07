/*
 Navicat Premium Data Transfer

 Source Server         : jiushi
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : 192.168.1.20:3306
 Source Schema         : jiushi_live_living

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 07/12/2024 10:14:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_anchor_shop_info
-- ----------------------------
DROP TABLE IF EXISTS `t_anchor_shop_info`;
CREATE TABLE `t_anchor_shop_info`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `anchor_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '主播id',
  `sku_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品sku id',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '有效（0无效，1有效）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '带货主播权限配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_anchor_shop_info
-- ----------------------------
INSERT INTO `t_anchor_shop_info` VALUES (3, 1823353984983810050, 666, 1, '2024-08-21 02:08:40', '2024-11-21 16:16:54');
INSERT INTO `t_anchor_shop_info` VALUES (4, 1823353984983810050, 777, 1, '2024-08-21 02:08:40', '2024-11-25 20:37:30');

-- ----------------------------
-- Table structure for t_category_info
-- ----------------------------
DROP TABLE IF EXISTS `t_category_info`;
CREATE TABLE `t_category_info`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `level` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '类目级别',
  `parent_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '父类目id',
  `category_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类目名称',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态（0无效，1有效）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '类目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_category_info
-- ----------------------------
INSERT INTO `t_category_info` VALUES (4, 1, 0, '家居用品', 1, '2024-08-21 02:09:11', '2024-08-21 02:09:11');
INSERT INTO `t_category_info` VALUES (5, 2, 4, '生活用品', 1, '2024-08-21 02:09:40', '2024-08-21 02:09:40');
INSERT INTO `t_category_info` VALUES (6, 3, 5, '纸巾用品', 1, '2024-08-21 02:10:07', '2024-08-21 02:10:07');

-- ----------------------------
-- Table structure for t_gift_config
-- ----------------------------
DROP TABLE IF EXISTS `t_gift_config`;
CREATE TABLE `t_gift_config`  (
  `gift_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '礼物id',
  `price` int UNSIGNED NULL DEFAULT NULL COMMENT '虚拟货币价格',
  `gift_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '礼物名称',
  `status` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '状态(0无效,1有效)',
  `cover_img_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '礼物封面地址',
  `svga_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'svga资源地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`gift_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '礼物配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_gift_config
-- ----------------------------
INSERT INTO `t_gift_config` VALUES (22, 90, '飞天火箭', 1, 'http://192.168.1.20:9009/jiushi-live/NO.10686%20%20%E7%81%AB%E7%AE%AD.png', 'http://192.168.1.20:9009/jiushi-live/NO.10686%20%20%E7%81%AB%E7%AE%AD.png', '2023-08-01 06:50:25', '2024-12-06 15:56:02');
INSERT INTO `t_gift_config` VALUES (23, 20, '进场特效', 1, 'http://192.168.1.20:9009/jiushi-live/%E7%94%B7-%E8%BF%9B%E5%9C%BA.png', 'http://192.168.1.20:9009/jiushi-live/%E7%94%B7-%E8%BF%9B%E5%9C%BA.png', '2023-08-01 06:50:25', '2024-12-06 15:56:06');
INSERT INTO `t_gift_config` VALUES (24, 10, '天使的翅膀', 1, 'http://192.168.1.20:9009/jiushi-live/NO.105731%20%20%E8%9D%B4%E8%9D%B619-19.png', 'http://192.168.1.20:9009/jiushi-live/NO.105731%20%20%E8%9D%B4%E8%9D%B619-19.png', '2023-08-01 06:50:25', '2024-12-06 15:56:09');

-- ----------------------------
-- Table structure for t_gift_record
-- ----------------------------
DROP TABLE IF EXISTS `t_gift_record`;
CREATE TABLE `t_gift_record`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '发送人',
  `object_id` bigint NULL DEFAULT NULL COMMENT '收礼人',
  `gift_id` int NULL DEFAULT NULL COMMENT '礼物id',
  `price` int NULL DEFAULT NULL COMMENT '送礼金额',
  `price_unit` tinyint NULL DEFAULT NULL COMMENT '送礼金额的单位',
  `source` tinyint NULL DEFAULT NULL COMMENT '礼物来源',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `json` json NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '送礼记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_gift_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_living_room
-- ----------------------------
DROP TABLE IF EXISTS `t_living_room`;
CREATE TABLE `t_living_room`  (
  `id` bigint UNSIGNED NOT NULL,
  `anchor_id` bigint NULL DEFAULT NULL COMMENT '主播id',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '直播间类型（1普通直播间，2pk直播间）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0无效1有效）',
  `room_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'EMPTY_STR' COMMENT '直播间名称',
  `covert_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '直播间封面',
  `watch_num` int NULL DEFAULT 0 COMMENT '观看数量',
  `good_num` int NULL DEFAULT 0 COMMENT '点赞数量',
  `start_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开播时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_living_room
-- ----------------------------
INSERT INTO `t_living_room` VALUES (2, 1823353984983810050, 2, 1, 'pk直播间', 'https://inews.gtimg.com/om_bt/Os3eJ8u3SgB3Kd-zrRRhgfR5hUvdwcVPKUTNO6O7sZfUwAA/641', 0, 0, '2024-10-12 11:14:11', '2024-10-12 11:14:53');
INSERT INTO `t_living_room` VALUES (7, 1823353984983810050, 1, 1, '主播-1823353984983810050的直播间', 'https://img2.baidu.com/it/u=3566795249,1013590605&fm=253&fmt=auto?w=800&h=800', 0, 0, '2024-11-06 10:24:43', '2024-11-20 16:04:56');
INSERT INTO `t_living_room` VALUES (1853986840344817666, 1845364359214202881, 1, 1, '主播-1845364359214202881的直播间', 'https://pic4.zhimg.com/v2-e14eb19452f4dd06c930b2685dab78c7_1440w.jpg', 0, 0, '2024-11-06 10:24:43', NULL);

-- ----------------------------
-- Table structure for t_living_room_record
-- ----------------------------
DROP TABLE IF EXISTS `t_living_room_record`;
CREATE TABLE `t_living_room_record`  (
  `id` bigint UNSIGNED NOT NULL,
  `anchor_id` bigint NULL DEFAULT NULL COMMENT '主播id',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '直播间类型（0默认类型）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0无效1有效）',
  `room_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'EMPTY_STR' COMMENT '直播间名称',
  `covert_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '直播间封面',
  `watch_num` int NULL DEFAULT 0 COMMENT '观看数量',
  `good_num` int NULL DEFAULT 0 COMMENT '点赞数量',
  `start_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开播时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '关播时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_living_room_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_red_packet_config
-- ----------------------------
DROP TABLE IF EXISTS `t_red_packet_config`;
CREATE TABLE `t_red_packet_config`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `anchor_id` bigint NOT NULL DEFAULT 0 COMMENT '主播id',
  `start_time` datetime NULL DEFAULT NULL COMMENT '红包雨活动开始时间',
  `total_get` int NOT NULL DEFAULT 0 COMMENT '一共领取数量',
  `total_get_price` int NOT NULL DEFAULT 0 COMMENT '一共领取金额',
  `max_get_price` int NOT NULL DEFAULT 0 COMMENT '最大领取金额',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '(1 待准备，2已准备，3已发送)',
  `total_price` int NOT NULL DEFAULT 0 COMMENT '红包雨总金额数',
  `total_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '红包雨总红包数',
  `config_code` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一code',
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '直播间红包雨配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_red_packet_config
-- ----------------------------
INSERT INTO `t_red_packet_config` VALUES (3, 1823353984983810050, NULL, 0, 0, 0, 3, 1000, 100, 'tfh666', '默认红包雨配置', '2024-08-13 06:38:11', '2024-11-22 11:37:16');

-- ----------------------------
-- Table structure for t_sku_info
-- ----------------------------
DROP TABLE IF EXISTS `t_sku_info`;
CREATE TABLE `t_sku_info`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sku_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'sku id',
  `sku_price` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'sku价格',
  `sku_code` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sku编码',
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
  `icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '缩略图',
  `original_icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '原图',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品描述',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态(0下架，1上架)',
  `category_id` int NOT NULL COMMENT '类目id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品sku信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_sku_info
-- ----------------------------
INSERT INTO `t_sku_info` VALUES (3, 666, 50, 'fasdfafge42522', '苹果16 Pro', 'http://192.168.1.20:9009/jiushi-live/iphone_16pro__erw9alves2qa_xlarge.png', 'http://192.168.1.20:9009/jiushi-live/iphone_16pro__erw9alves2qa_xlarge.png', '高品质', 1, 6, '2024-08-21 02:06:18', '2024-12-06 15:58:16');
INSERT INTO `t_sku_info` VALUES (44, 777, 50, 'fasdfafge42352', 'Mac Pro', 'http://192.168.1.20:9009/jiushi-live/product_tile_mbp_14_16__bkl8zusnkpw2_large.png', 'http://192.168.1.20:9009/jiushi-live/product_tile_mbp_14_16__bkl8zusnkpw2_large.png', '高品质', 1, 6, '2024-08-21 02:06:18', '2024-12-06 15:57:09');

-- ----------------------------
-- Table structure for t_sku_order_info
-- ----------------------------
DROP TABLE IF EXISTS `t_sku_order_info`;
CREATE TABLE `t_sku_order_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `sku_id_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户id',
  `room_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '直播id',
  `status` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态',
  `extra` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_sku_order_info
-- ----------------------------
INSERT INTO `t_sku_order_info` VALUES (1865023014161092609, '666,777', 1823353984983810050, 7, 2, NULL, '2024-12-06 21:18:32', '2024-12-06 21:22:11');
INSERT INTO `t_sku_order_info` VALUES (1865216559056707586, '666', 1823353984983810050, 7, 2, NULL, '2024-12-07 10:07:37', '2024-12-07 10:07:41');

-- ----------------------------
-- Table structure for t_sku_stock_info
-- ----------------------------
DROP TABLE IF EXISTS `t_sku_stock_info`;
CREATE TABLE `t_sku_stock_info`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sku_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'sku id',
  `stock_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT 'sku库存',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态（0无效，1有效）',
  `version` int UNSIGNED NULL DEFAULT NULL COMMENT '乐观锁',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'sku库存表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_sku_stock_info
-- ----------------------------
INSERT INTO `t_sku_stock_info` VALUES (2, 666, 91, 1, 0, '2024-08-21 02:10:47', '2024-12-07 10:07:51');
INSERT INTO `t_sku_stock_info` VALUES (3, 777, 30, 1, NULL, '2024-12-06 15:59:55', '2024-12-06 21:18:44');

SET FOREIGN_KEY_CHECKS = 1;
