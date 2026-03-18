#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
微服务数据库初始化脚本
功能：按照顺序初始化所有微服务数据库
"""

import os
import sys
import mysql.connector
from mysql.connector import Error
from colorama import init, Fore, Style

# 初始化 colorama
init()

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',  # 请根据实际情况修改 db_config.properties 中的密码
    'charset': 'utf8mb4'
}

# 微服务数据库列表
SERVICES = [
    'user_db',
    'product_db',
    'order_db',
    'payment_db',
    'shipment_db',
    'comment_db',
    'blog_db',
    'sms_db',
    'store_db'
]

def print_success(message):
    print(f"{Fore.GREEN}✓ {message}{Style.RESET_ALL}")

def print_error(message):
    print(f"{Fore.RED}✗ {message}{Style.RESET_ALL}")

def print_info(message):
    print(f"{Fore.CYAN}ℹ {message}{Style.RESET_ALL}")

def execute_sql_file(connection, sql_file):
    """执行 SQL 文件"""
    try:
        with open(sql_file, 'r', encoding='utf-8') as f:
            sql_content = f.read()
        
        # 分割 SQL 语句（按分号分割）
        statements = sql_content.split(';')
        
        # 使用 buffered cursor 避免 Unread result found 错误
        cursor = connection.cursor(buffered=True)
        for statement in statements:
            statement = statement.strip()
            if statement:
                cursor.execute(statement)
        
        connection.commit()
        cursor.close()
        return True
    except Error as e:
        print_error(f"执行 SQL 文件失败 {sql_file}: {e}")
        return False

def init_database(db_name):
    """初始化单个数据库"""
    print_info(f"正在初始化数据库：{db_name}")
    
    try:
        # 连接到 MySQL 服务器
        connection = mysql.connector.connect(**DB_CONFIG)
        
        if connection.is_connected():
            cursor = connection.cursor()
            
            # 获取数据库目录
            db_dir = db_name.replace('_db', '-db')
            
            # 1. 创建数据库
            sql_files = [
                os.path.join(db_dir, '01_create_database.sql'),
                os.path.join(db_dir, '02_tables.sql'),
                os.path.join(db_dir, '03_init_data.sql')
            ]
            
            # 执行所有 SQL 文件
            for sql_file in sql_files:
                if os.path.exists(sql_file):
                    print_info(f"  执行：{sql_file}")
                    if not execute_sql_file(connection, sql_file):
                        return False
                else:
                    print_error(f"  文件不存在：{sql_file}")
            
            print_success(f"数据库 {db_name} 初始化完成")
            return True
        else:
            print_error(f"无法连接到 MySQL 服务器")
            return False
            
    except Error as e:
        print_error(f"初始化数据库 {db_name} 失败：{e}")
        return False
    finally:
        if connection.is_connected():
            connection.close()

def main():
    """主函数"""
    print("=" * 60)
    print(f"{Fore.GREEN}微服务数据库初始化脚本{Style.RESET_ALL}")
    print("=" * 60)
    print()
    
    # 检查 MySQL 连接
    print_info("检查 MySQL 连接...")
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        if connection.is_connected():
            print_success("MySQL 连接成功")
            connection.close()
        else:
            print_error("MySQL 连接失败")
            sys.exit(1)
    except Error as e:
        print_error(f"MySQL 连接失败：{e}")
        print_info("请确保 MySQL 服务已启动，并修改 db_config.properties 中的配置")
        sys.exit(1)
    
    print()
    
    # 初始化所有数据库
    success_count = 0
    for service in SERVICES:
        if init_database(service):
            success_count += 1
        print()
    
    # 输出统计信息
    print("=" * 60)
    print(f"初始化完成：{success_count}/{len(SERVICES)} 个数据库")
    if success_count == len(SERVICES):
        print_success("所有数据库初始化成功！")
    else:
        print_error(f"有 {len(SERVICES) - success_count} 个数据库初始化失败")
    print("=" * 60)

if __name__ == '__main__':
    main()
