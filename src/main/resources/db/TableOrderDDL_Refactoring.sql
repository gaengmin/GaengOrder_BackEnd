-- 1. stores
DROP TABLE IF EXISTS `stores`;

CREATE TABLE `stores` (
                          `store_no` bigint NOT NULL AUTO_INCREMENT,
                          `business_no` varchar(20) NOT NULL,
                          `store_name` varchar(100) NOT NULL,
                          `store_tel` varchar(20) DEFAULT NULL,
                          `soft_delete` char(1) DEFAULT 'N',
                          `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
                          `update_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`store_no`),
                          UNIQUE KEY `business_no` (`business_no`)
);

-- 2. categories
DROP TABLE IF EXISTS `categories`;

CREATE TABLE `categories` (
                              `categories_no` bigint NOT NULL AUTO_INCREMENT,
                              `store_no` bigint NOT NULL,
                              `name` varchar(100) NOT NULL,
                              `soft_delete` char(1) DEFAULT 'N',
                              `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
                              `update_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `position` int NOT NULL DEFAULT '1',
                              PRIMARY KEY (`categories_no`),
                              KEY `store_no` (`store_no`),
                              CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`store_no`) REFERENCES `stores` (`store_no`)
);

-- 3. menu
DROP TABLE IF EXISTS `menu`;

CREATE TABLE `menu` (
                        `menu_no` bigint NOT NULL AUTO_INCREMENT,
                        `categories_no` bigint NOT NULL,
                        `menu_name` varchar(100) NOT NULL,
                        `menu_price` bigint NOT NULL,
                        `description` text,
                        `menu_status` char(1) DEFAULT 'N',
                        `soft_delete` char(1) DEFAULT 'N',
                        `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
                        `update_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `position` int NOT NULL DEFAULT '1',
                        PRIMARY KEY (`menu_no`),
                        KEY `categories_no` (`categories_no`),
                        CONSTRAINT `menu_ibfk_1` FOREIGN KEY (`categories_no`) REFERENCES `categories` (`categories_no`)
);

-- 4. tables
DROP TABLE IF EXISTS `tables`;

CREATE TABLE `tables` (
                          `table_no` bigint NOT NULL AUTO_INCREMENT,
                          `store_no` bigint NOT NULL,
                          `table_code` varchar(255) DEFAULT NULL,
                          `soft_delete` char(1) DEFAULT NULL,
                          `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
                          `update_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`table_no`),
                          UNIQUE KEY `table_code` (`table_code`),
                          KEY `store_no` (`store_no`),
                          CONSTRAINT `tables_ibfk_1` FOREIGN KEY (`store_no`) REFERENCES `stores` (`store_no`)
);

-- 5. users
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
                         `user_no` bigint NOT NULL AUTO_INCREMENT,
                         `store_no` bigint DEFAULT NULL,
                         `user_id` varchar(100) NOT NULL,
                         `pwd` varchar(100) NOT NULL,
                         `name` varchar(20) NOT NULL,
                         `phone_number` varchar(20) NOT NULL,
                         `role` enum('ADMIN','ORDERS','SUPERADMIN') DEFAULT NULL,
                         `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
                         `update_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `soft_delete` char(1) DEFAULT NULL,
                         PRIMARY KEY (`user_no`),
                         UNIQUE KEY `user_id` (`user_id`),
                         KEY `store_no` (`store_no`),
                         CONSTRAINT `users_ibfk_1` FOREIGN KEY (`store_no`) REFERENCES `stores` (`store_no`)
);

-- 6. orders
DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
                          `orders_no` bigint NOT NULL AUTO_INCREMENT,
                          `store_no` bigint NOT NULL,
                          `table_no` bigint NOT NULL,
                          `total_price` bigint DEFAULT '0',
                          `discount_price` bigint DEFAULT '0',
                          `final_price` bigint DEFAULT '0',
                          `order_status` varchar(20) NOT NULL DEFAULT 'READY',
                          `additional_order` int DEFAULT '1',
                          `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
                          `update_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`orders_no`),
                          KEY `fk_orders_table` (`table_no`),
                          KEY `fk_orders_store` (`store_no`),
                          CONSTRAINT `fk_orders_store` FOREIGN KEY (`store_no`) REFERENCES `stores` (`store_no`),
                          CONSTRAINT `fk_orders_table` FOREIGN KEY (`table_no`) REFERENCES `tables` (`table_no`)
);

-- 7. orders_items
DROP TABLE IF EXISTS `orders_items`;

CREATE TABLE `orders_items` (
                                `orders_items_no` bigint NOT NULL AUTO_INCREMENT,
                                `orders_no` bigint NOT NULL,
                                `menu_no` bigint NOT NULL,
                                `menu_name` varchar(100) NOT NULL,
                                `menu_price` bigint NOT NULL,
                                `quantity` int NOT NULL,
                                `is_free` char(1) DEFAULT 'N',
                                `is_canceled` char(1) DEFAULT 'N',
                                `cancel_reason` text,
                                `cancel_at` datetime DEFAULT NULL,
                                `added_order` int DEFAULT '0',
                                `added_at` datetime DEFAULT NULL,
                                `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
                                `update_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`orders_items_no`),
                                KEY `menu_no` (`menu_no`),
                                KEY `orders_items_ibfk_1` (`orders_no`),
                                CONSTRAINT `orders_items_ibfk_1` FOREIGN KEY (`orders_no`) REFERENCES `orders` (`orders_no`),
                                CONSTRAINT `orders_items_ibfk_2` FOREIGN KEY (`menu_no`) REFERENCES `menu` (`menu_no`)
);

-- 8. orders_status_log
DROP TABLE IF EXISTS `orders_status_log`;

CREATE TABLE `orders_status_log` (
                                     `orders_log_no` bigint NOT NULL AUTO_INCREMENT COMMENT '로그PK',
                                     `new_status` varchar(20) NOT NULL COMMENT '상태명',
                                     `previous_status` varchar(20) NOT NULL COMMENT '이전상태',
                                     `changed_at` datetime NOT NULL COMMENT '변경시간',
                                     `user_no` bigint NOT NULL COMMENT '직원PK',
                                     `order_no` bigint NOT NULL COMMENT '주문PK',
                                     PRIMARY KEY (`orders_log_no`),
                                     KEY `fk_orders_status_log_order` (`order_no`),
                                     KEY `fk_orders_status_log_user` (`user_no`),
                                     CONSTRAINT `fk_orders_status_log_order` FOREIGN KEY (`order_no`) REFERENCES `orders` (`orders_no`),
                                     CONSTRAINT `fk_orders_status_log_user` FOREIGN KEY (`user_no`) REFERENCES `users` (`user_no`)
);

-- 9. refresh_tokens
DROP TABLE IF EXISTS `refresh_tokens`;

CREATE TABLE `refresh_tokens` (
                                  `refresh_no` bigint NOT NULL AUTO_INCREMENT,
                                  `user_no` bigint NOT NULL,
                                  `refresh` varchar(255) NOT NULL,
                                  `expiration` datetime NOT NULL,
                                  `device_info` varchar(100) DEFAULT NULL,
                                  PRIMARY KEY (`refresh_no`),
                                  KEY `user_no` (`user_no`),
                                  CONSTRAINT `refresh_tokens_ibfk_1` FOREIGN KEY (`user_no`) REFERENCES `users` (`user_no`)
);
