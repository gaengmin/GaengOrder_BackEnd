CREATE DATABASE tableorder;
SHOW databases;
USE tableorder;
SHOW TABLES;

-- 25-05-15 메뉴 및 카테고리의 position 컬럼 추가

-- 1. 매장정보
DROP TABLE IF EXISTS stores;
CREATE TABLE stores (
    store_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(20) NOT NULL UNIQUE,
    store_name VARCHAR(100),
    store_tel VARCHAR(20),
    soft_delete CHAR(1) DEFAULT 'N',
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 매장테이블정보
DROP TABLE IF EXISTS tables;
CREATE TABLE tables (
    table_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_no BIGINT NOT NULL,
    table_code VARCHAR(100) NOT NULL UNIQUE,
    soft_delete CHAR(1) DEFAULT 'N',
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_no) REFERENCES stores(store_no)
);

-- 3. 회원정보
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_no BIGINT,
    user_id VARCHAR(20) NOT NULL UNIQUE,
    pwd VARCHAR(100) NOT NULL,
    name VARCHAR(20) NOT NULL,
    role VARCHAR(10) NOT NULL,
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    soft_delete CHAR(1) DEFAULT 'N',
    FOREIGN KEY (store_no) REFERENCES stores(store_no)
);

-- 4. 카테고리
DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    categories_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_no BIGINT NOT NULL,
    name VARCHAR(30) NOT NULL,
	position INT NOT NULL DEFAULT 1,
    soft_delete CHAR(1) DEFAULT 'N',
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_no) REFERENCES stores(store_no)
);

-- 5. 메뉴
DROP TABLE IF EXISTS menu;
CREATE TABLE menu (
    menu_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    categories_no BIGINT NOT NULL,
    menu_name VARCHAR(30) NOT NULL,
    menu_price BIGINT NOT NULL,
    description TEXT,
    position INT NOT NULL DEFAULT 1,
    soft_delete CHAR(1) DEFAULT 'N',
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categories_no) REFERENCES categories(categories_no)
);

-- 6. 주문상태(주문)
DROP TABLE IF EXISTS orders_status;
CREATE TABLE orders_status (
    orders_status_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_no BIGINT NOT NULL,
    order_status VARCHAR(20) NOT NULL,
    additional_order INT DEFAULT 1,
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    soft_delete CHAR(1) DEFAULT 'N',
    FOREIGN KEY (table_no) REFERENCES tables(table_no)
);

-- 7. 주문메뉴(주문상세)
DROP TABLE IF EXISTS orders_items;
CREATE TABLE orders_items (
    orders_items_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    orders_status_no BIGINT NOT NULL,
    menu_name VARCHAR(30) NOT NULL,
    menu_price BIGINT NOT NULL,
    quantity INT NOT NULL,
    create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    soft_delete CHAR(1) DEFAULT 'N',
    menu_no BIGINT NOT NULL,
    FOREIGN KEY (orders_status_no) REFERENCES orders_status(orders_status_no),
    FOREIGN KEY (menu_no) REFERENCES menu(menu_no)
);

-- 8. 리프래시 토큰
DROP TABLE IF EXISTS refresh_tokens;
CREATE TABLE refresh_tokens (
    refresh_no   BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 리프레시 토큰 PK
    user_no      BIGINT NOT NULL,                     -- 유저 PK (외래키)
    refresh      VARCHAR(255) NOT NULL,               -- 리프레시 토큰 값
    expiration   DATETIME NOT NULL,                   -- 만료 일시
    device_info  VARCHAR(100),                        -- (선택) 기기 정보
    FOREIGN KEY (user_no) REFERENCES users(user_no)
);

