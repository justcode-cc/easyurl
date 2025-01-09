
#数据库初始化
-- 部门表
CREATE TABLE `sys_dept` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `parent_id` bigint NOT NULL,
                            `ancestors` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                            `dept_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                            `order_num` int NOT NULL,
                            `status` tinyint NOT NULL,
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `deleted` tinyint NOT NULL DEFAULT '0',
                            `version` int NOT NULL DEFAULT '0',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sys_dept` (`id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `status`, `create_time`, `update_time`, `deleted`, `version`) VALUES (1, 0, '0', '总公司', 1, 1, '2024-07-16 23:01:36', '2024-07-16 23:06:38', 0, 0);

-- 菜单表
CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `menu_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `parent_id` bigint DEFAULT NULL,
                            `order_num` int DEFAULT NULL,
                            `path` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `component` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `query` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `is_frame` tinyint(1) DEFAULT '0',
                            `is_cache` tinyint(1) DEFAULT '1',
                            `menu_type` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `visible` tinyint(1) DEFAULT '1',
                            `status` tinyint(1) DEFAULT NULL,
                            `icon` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `deleted` tinyint DEFAULT '0',
                            `version` tinyint DEFAULT '0',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (1, '系统管理', 0, 1, 'system', NULL, NULL, 0, 1, 'M', 1, 1, 'system', '2024-07-17 16:33:50', '2024-07-17 16:33:50', 0, 0);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', NULL, 0, 1, 'C', 1, 1, 'tree-table', '2024-07-22 14:32:10', '2024-07-22 14:32:10', 0, 0);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', NULL, 0, 1, 'C', 1, 1, 'tree', '2024-07-23 16:07:15', '2024-07-23 16:08:48', 0, 0);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (105, '角色管理', 1, 2, 'role', 'system/role/index', NULL, 0, 1, 'C', 1, 1, 'peoples', '2024-07-24 22:27:25', '2024-07-24 22:27:25', 0, 0);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (106, '用户管理', 1, 1, 'user', 'system/user/index', NULL, 0, 1, 'C', 1, 1, 'user', '2024-07-27 11:36:29', '2024-07-27 11:36:29', 0, 0);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (107, '缓存管理', 1, 50, 'SysCache', 'system/cache/index', NULL, 0, 1, 'C', 1, 1, 'time', '2024-08-26 09:27:13', '2024-08-26 09:27:13', 0, 0);


-- 角色表 AUTO_INCREMENT设置为10001，1-10001为预留位置
CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `main_role` tinyint DEFAULT '0',
                            `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                            `parent_id` bigint NOT NULL,
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `deleted` tinyint NOT NULL DEFAULT '0',
                            `version` int NOT NULL DEFAULT '0',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sys_role` (`id`, `main_role`, `role_name`, `parent_id`, `create_time`, `update_time`, `deleted`, `version`) VALUES (1, 1, '管理员', 1, '2024-07-16 23:09:03', '2024-11-07 23:01:29', 0, 0);
INSERT INTO `sys_role` (`id`, `main_role`, `role_name`, `parent_id`, `create_time`, `update_time`, `deleted`, `version`) VALUES (10001, 1, '预留角色', 1, '2024-07-24 23:37:26', '2024-11-07 23:01:17', 0, 0);


-- 角色菜单表
CREATE TABLE `sys_role_menu` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `role_id` bigint NOT NULL,
                                 `menu_id` bigint NOT NULL,
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `deleted` tinyint NOT NULL DEFAULT '0',
                                 `version` int NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 用户表
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `salt` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `status` tinyint DEFAULT '1',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `deleted` tinyint DEFAULT '0',
                            `version` tinyint DEFAULT '0',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- admin -- th749gds73phg4gg
INSERT INTO `sys_user` (`id`, `username`, `account`, `phone`, `password`, `salt`, `status`, `create_time`, `update_time`, `deleted`, `version`) VALUES (1, '管理员', 'admin', '16688888888', '60ee00e87016c7f42414b8fda4bbd11c', 'tgb976', 1, '2024-07-16 23:10:08', '2024-07-30 14:47:57', 0, 0);

-- 岗位表
CREATE TABLE `sys_user_job` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `user_id` bigint NOT NULL,
                                `role_id` bigint NOT NULL,
                                `dept_id` bigint NOT NULL,
                                `dept_admin` int NOT NULL DEFAULT '0',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `deleted` tinyint NOT NULL DEFAULT '0',
                                `version` int NOT NULL DEFAULT '0',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sys_user_job` (`id`, `user_id`, `role_id`, `dept_id`, `dept_admin`, `create_time`, `update_time`, `deleted`, `version`) VALUES (1, 1, 1, 1, 1, '2024-07-16 23:10:22', '2024-07-16 23:10:22', 0, 0);

-- 缓存表
CREATE TABLE `sys_cache` (
                             `id` bigint NOT NULL,
                             `cache_key` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `cache_value` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `cache_ttl` int DEFAULT '-1',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             `deleted` tinyint DEFAULT '0',
                             `version` int DEFAULT '0',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `open_account` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `app_secret` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `status` int NOT NULL DEFAULT '1',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `deleted` int NOT NULL DEFAULT '0',
                                `version` int NOT NULL DEFAULT '1',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (108, '开放账户', 1, 60, 'openAccount', 'system/openAccount/index', NULL, 0, 1, 'C', 1, 1, 'row', '2024-12-19 22:41:55', '2024-12-19 22:41:55', 0, 0);


CREATE TABLE `url_mapping` (
                               `id` bigint unsigned NOT NULL,
                               `origin_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `short_url` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `status` tinyint DEFAULT '1',
                               `expire_time` datetime DEFAULT NULL,
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               `create_user_id` bigint DEFAULT '-1',
                               PRIMARY KEY (`id`),
                               KEY `idx_shortUrl` (`short_url`),
                               KEY `idx_expireTime` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `url_visit` (
                             `id` bigint NOT NULL,
                             `url_id` bigint DEFAULT NULL,
                             `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `browser_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `platform` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `engine` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `engine_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `referrer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `mobile` tinyint DEFAULT NULL,
                             `valid` tinyint DEFAULT NULL,
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             KEY `idx_createTime` (`create_time`),
                             KEY `idx_urlId` (`url_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (200, '短链管理', 0, 0, 'url', NULL, NULL, 0, 1, 'M', 1, 1, 'link', '2024-08-14 23:07:50', '2024-08-14 23:07:50', 0, 0);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (201, '短链列表', 200, 1, 'urlList', 'url/list', NULL, 0, 1, 'C', 1, 1, 'list', '2024-08-14 23:09:30', '2024-08-14 23:09:30', 0, 0);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `icon`, `create_time`, `update_time`, `deleted`, `version`) VALUES (202, '访问记录', 200, 2, 'visitList', 'url/visitList', NULL, 0, 1, 'C', 1, 1, 'monitor', '2024-08-14 23:10:40', '2024-08-14 23:10:40', 0, 0);
