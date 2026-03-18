#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
微服务数据库验证脚本
功能：验证所有微服务数据库是否正确初始化
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
    'password': '123456',  # 请根据实际情况修改
    'charset': 'utf8mb4'
}

# 微服务数据库及预期表
SERVICES = {
    'user_db': ['users', 'addresses'],
    'product_db': ['categories', 'products', 'product_images', 'seckill_activities', 
                   'seckill_products', 'product_skus', 'product_attributes', 
                   'product_attribute_values', 'product_sku_attributes'],
    'order_db': ['carts', 'cart_items', 'orders', 'order_items', 'seckill_orders'],
    'payment_db': ['payments'],
    'shipment_db': ['shipments'],
    'comment_db': ['comments', 'favorites'],
    'blog_db': ['blog_categories', 'blog_tags', 'blogs', 'blog_comments'],
    'sms_db': ['sms_templates', 'sms_records'],
    'store_db': ['stores', 'store_follows', 'store_ratings']
}

def print_success(message):
    print(f"{Fore.GREEN}✓ {message}{Style.RESET_ALL}")

def print_error(message):
    print(f"{Fore.RED}✗ {message}{Style.RESET_ALL}")

def print_info(message):
    print(f"{Fore.CYAN}ℹ {message}{Style.RESET_ALL}")

def verify_database(db_name, expected_tables):
    """验证单个数据库"""
    print_info(f"正在验证数据库：{db_name}")
    
    try:
        # 连接到数据库
        config = DB_CONFIG.copy()
        config['database'] = db_name
        connection = mysql.connector.connect(**config)
        
        if connection.is_connected():
            cursor = connection.cursor()
            
            # 获取所有表
            cursor.execute("SHOW TABLES")
            tables = [row[0] for row in cursor.fetchall()]
            
            # 验证表是否存在
            missing_tables = []
            for table in expected_tables:
                if table in tables:
                    print_success(f"  表 {table} 存在")
                else:
                    print_error(f"  表 {table} 不存在")
                    missing_tables.append(table)
            
            cursor.close()
            connection.close()
            
            if missing_tables:
                print_error(f"数据库 {db_name} 缺少表：{', '.join(missing_tables)}")
                return False
            else:
                print_success(f"数据库 {db_name} 验证通过")
                return True
        else:
            print_error(f"无法连接到数据库 {db_name}")
            return False
            
    except Error as e:
        print_error(f"验证数据库 {db_name} 失败：{e}")
        return False

def main():
    """主函数"""
    print("=" * 60)
    print(f"{Fore.GREEN}微服务数据库验证脚本{Style.RESET_ALL}")
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
        sys.exit(1)
    
    print()
    
    # 验证所有数据库
    success_count = 0
    for db_name, tables in SERVICES.items():
        if verify_database(db_name, tables):
            success_count += 1
        print()
    
    # 输出统计信息
    print("=" * 60)
    print(f"验证完成：{success_count}/{len(SERVICES)} 个数据库")
    if success_count == len(SERVICES):
        print_success("✓ 所有数据库验证通过！")
    else:
        print_error(f"✗ 有 {len(SERVICES) - success_count} 个数据库验证失败")
    print("=" * 60)

if __name__ == '__main__':
    main()
