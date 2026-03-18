#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
传统分布式购物平台 - 数据库验证脚本
功能：验证数据库、表结构和数据是否正确创建
创建时间：2026-03-17
"""

import os
import sys
import mysql.connector
from mysql.connector import Error
from pathlib import Path
from datetime import datetime
from collections import defaultdict


class DatabaseVerifier:
    """数据库验证类"""
    
    def __init__(self, host='localhost', port=3306, username='root', password='', database='shopping_platform_db'):
        """
        初始化数据库连接配置
        
        Args:
            host: MySQL 服务器地址
            port: MySQL 服务器端口
            username: 用户名
            password: 密码
            database: 数据库名
        """
        self.host = host
        self.port = port
        self.username = username
        self.password = password
        self.database = database
        self.connection = None
    
    def connect(self):
        """
        连接到 MySQL 服务器
        
        Returns:
            bool: 连接是否成功
        """
        try:
            self.connection = mysql.connector.connect(
                host=self.host,
                port=self.port,
                user=self.username,
                password=self.password,
                database=self.database
            )
            if self.connection.is_connected():
                print(f"✓ 成功连接到 MySQL 服务器")
                print(f"  数据库：{self.database}")
                return True
        except Error as e:
            print(f"✗ 连接失败：{e}")
            return False
        return False
    
    def disconnect(self):
        """断开数据库连接"""
        if self.connection and self.connection.is_connected():
            self.connection.close()
            print("\n✓ 数据库连接已关闭")
    
    def execute_query(self, query, params=None):
        """
        执行查询语句
        
        Args:
            query: SQL 查询语句
            params: 查询参数
            
        Returns:
            list: 查询结果
        """
        try:
            cursor = self.connection.cursor(dictionary=True)
            cursor.execute(query, params or ())
            results = cursor.fetchall()
            cursor.close()
            return results
        except Error as e:
            print(f"  ✗ 查询失败：{e}")
            return []
    
    def check_tables_exist(self):
        """
        检查所有表是否存在
        
        Returns:
            dict: 验证结果
        """
        print("\n" + "=" * 60)
        print("1. 检查所有表是否存在")
        print("=" * 60)
        
        expected_tables = {
            '用户与认证': ['users', 'addresses', 'sms_templates', 'sms_records'],
            '商品与库存': ['categories', 'products', 'product_images', 'product_skus', 
                         'product_attributes', 'product_attribute_values', 'product_sku_attributes',
                         'seckill_products', 'seckill_activities'],
            '订单与支付': ['carts', 'cart_items', 'orders', 'order_items', 'seckill_orders', 'payments'],
            '物流与评论': ['shipments', 'comments', 'favorites'],
            '内容与统计': ['blogs', 'blog_categories', 'blog_tags', 'blog_comments', 'stats_records'],
            '店铺': ['stores', 'store_follows', 'store_ratings']
        }
        
        query = """
            SELECT TABLE_NAME 
            FROM information_schema.TABLES 
            WHERE TABLE_SCHEMA = %s
        """
        
        results = self.execute_query(query, (self.database,))
        existing_tables = {row['TABLE_NAME'] for row in results}
        
        all_exist = True
        for module, tables in expected_tables.items():
            print(f"\n{module}:")
            for table in tables:
                if table in existing_tables:
                    print(f"  ✓ {table}")
                else:
                    print(f"  ✗ {table} (缺失)")
                    all_exist = False
        
        return {
            'success': all_exist,
            'total_expected': sum(len(tables) for tables in expected_tables.values()),
            'total_found': len(existing_tables)
        }
    
    def check_table_count(self):
        """
        检查表总数
        
        Returns:
            dict: 验证结果
        """
        print("\n" + "=" * 60)
        print("2. 检查表总数")
        print("=" * 60)
        
        query = """
            SELECT COUNT(*) as total
            FROM information_schema.TABLES 
            WHERE TABLE_SCHEMA = %s
        """
        
        results = self.execute_query(query, (self.database,))
        if results:
            total = results[0]['total']
            print(f"\n表总数：{total}")
            
            if total >= 30:
                print(f"✓ 表数量正常（期望 30 个，实际 {total} 个）")
                return {'success': True, 'count': total}
            else:
                print(f"✗ 表数量不足（期望 30 个，实际 {total} 个）")
                return {'success': False, 'count': total}
        
        return {'success': False, 'count': 0}
    
    def check_module_distribution(self):
        """
        检查各模块表数量分布
        
        Returns:
            dict: 验证结果
        """
        print("\n" + "=" * 60)
        print("3. 检查各模块表数量分布")
        print("=" * 60)
        
        query = """
            SELECT 
                CASE 
                    WHEN TABLE_NAME IN ('users', 'addresses', 'sms_templates', 'sms_records') THEN '用户与认证'
                    WHEN TABLE_NAME IN ('categories', 'products', 'product_images', 'product_skus', 
                                       'product_attributes', 'product_attribute_values', 'product_sku_attributes',
                                       'seckill_products', 'seckill_activities') THEN '商品与库存'
                    WHEN TABLE_NAME IN ('carts', 'cart_items', 'orders', 'order_items', 'seckill_orders', 'payments') THEN '订单与支付'
                    WHEN TABLE_NAME IN ('shipments', 'comments', 'favorites') THEN '物流与评论'
                    WHEN TABLE_NAME IN ('blogs', 'blog_categories', 'blog_tags', 'blog_comments', 'stats_records') THEN '内容与统计'
                    WHEN TABLE_NAME IN ('stores', 'store_follows', 'store_ratings') THEN '店铺'
                    ELSE '其他'
                END AS module,
                COUNT(*) AS table_count
            FROM information_schema.TABLES 
            WHERE TABLE_SCHEMA = %s
            GROUP BY module
        """
        
        results = self.execute_query(query, (self.database,))
        
        expected_counts = {
            '用户与认证': 4,
            '商品与库存': 9,
            '订单与支付': 6,
            '物流与评论': 3,
            '内容与统计': 5,
            '店铺': 3
        }
        
        all_correct = True
        for module, expected in expected_counts.items():
            actual = next((r['table_count'] for r in results if r['module'] == module), 0)
            if actual == expected:
                print(f"  ✓ {module}: {actual} 个表")
            else:
                print(f"  ✗ {module}: {actual} 个表 (期望 {expected} 个)")
                all_correct = False
        
        return {'success': all_correct}
    
    def check_default_data(self):
        """
        检查默认数据是否存在
        
        Returns:
            dict: 验证结果
        """
        print("\n" + "=" * 60)
        print("4. 检查默认数据")
        print("=" * 60)
        
        checks = []
        
        query_admin = "SELECT COUNT(*) as count FROM users WHERE role = 'admin'"
        result = self.execute_query(query_admin)
        admin_count = result[0]['count'] if result else 0
        admin_ok = admin_count > 0
        print(f"\n管理员账户：{'✓' if admin_ok else '✗'} (数量：{admin_count})")
        checks.append(admin_ok)
        
        query_categories = "SELECT COUNT(*) as count FROM categories"
        result = self.execute_query(query_categories)
        category_count = result[0]['count'] if result else 0
        categories_ok = category_count >= 2
        print(f"商品分类：{'✓' if categories_ok else '✗'} (数量：{category_count})")
        checks.append(categories_ok)
        
        query_stores = "SELECT COUNT(*) as count FROM stores"
        result = self.execute_query(query_stores)
        store_count = result[0]['count'] if result else 0
        stores_ok = store_count >= 1
        print(f"店铺数据：{'✓' if stores_ok else '✗'} (数量：{store_count})")
        checks.append(stores_ok)
        
        query_sms_templates = "SELECT COUNT(*) as count FROM sms_templates"
        result = self.execute_query(query_sms_templates)
        sms_count = result[0]['count'] if result else 0
        sms_ok = sms_count >= 1
        print(f"短信模板：{'✓' if sms_ok else '✗'} (数量：{sms_count})")
        checks.append(sms_ok)
        
        query_blog_categories = "SELECT COUNT(*) as count FROM blog_categories"
        result = self.execute_query(query_blog_categories)
        blog_cat_count = result[0]['count'] if result else 0
        blog_cat_ok = blog_cat_count >= 1
        print(f"博客分类：{'✓' if blog_cat_ok else '✗'} (数量：{blog_cat_count})")
        checks.append(blog_cat_ok)
        
        query_blog_tags = "SELECT COUNT(*) as count FROM blog_tags"
        result = self.execute_query(query_blog_tags)
        blog_tag_count = result[0]['count'] if result else 0
        blog_tag_ok = blog_tag_count >= 1
        print(f"博客标签：{'✓' if blog_tag_ok else '✗'} (数量：{blog_tag_count})")
        checks.append(blog_tag_ok)
        
        return {'success': all(checks)}
    
    def check_test_data(self):
        """
        检查测试数据（可选）
        
        Returns:
            dict: 验证结果
        """
        print("\n" + "=" * 60)
        print("5. 检查测试数据（可选）")
        print("=" * 60)
        
        checks = []
        
        query_users = "SELECT COUNT(*) as count FROM users WHERE role = 'user'"
        result = self.execute_query(query_users)
        user_count = result[0]['count'] if result else 0
        users_ok = user_count >= 2
        print(f"\n测试用户：{'✓' if users_ok else '✗'} (数量：{user_count}, 期望>=2)")
        checks.append(users_ok)
        
        query_products = "SELECT COUNT(*) as count FROM products"
        result = self.execute_query(query_products)
        product_count = result[0]['count'] if result else 0
        products_ok = product_count >= 5
        print(f"商品数据：{'✓' if products_ok else '✗'} (数量：{product_count}, 期望>=5)")
        checks.append(products_ok)
        
        query_orders = "SELECT COUNT(*) as count FROM orders"
        result = self.execute_query(query_orders)
        order_count = result[0]['count'] if result else 0
        orders_ok = order_count >= 1
        print(f"订单数据：{'✓' if orders_ok else '✗'} (数量：{order_count}, 期望>=1)")
        checks.append(orders_ok)
        
        query_comments = "SELECT COUNT(*) as count FROM comments"
        result = self.execute_query(query_comments)
        comment_count = result[0]['count'] if result else 0
        comments_ok = comment_count >= 1
        print(f"评论数据：{'✓' if comments_ok else '✗'} (数量：{comment_count}, 期望>=1)")
        checks.append(comments_ok)
        
        query_blogs = "SELECT COUNT(*) as count FROM blogs"
        result = self.execute_query(query_blogs)
        blog_count = result[0]['count'] if result else 0
        blogs_ok = blog_count >= 1
        print(f"博客文章：{'✓' if blogs_ok else '✗'} (数量：{blog_count}, 期望>=1)")
        checks.append(blogs_ok)
        
        return {'success': all(checks), 'has_test_data': all(checks)}
    
    def print_summary(self, results):
        """
        打印验证摘要
        
        Args:
            results: 各项验证结果
        """
        print("\n" + "=" * 60)
        print("验证摘要")
        print("=" * 60)
        
        all_success = all(r.get('success', False) for r in results.values())
        
        print(f"\n数据库名称：{self.database}")
        print(f"主机：{self.host}:{self.port}")
        print(f"验证时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print()
        
        for check_name, result in results.items():
            status = "✓" if result.get('success', False) else "✗"
            print(f"{status} {check_name}")
        
        print()
        if all_success:
            print("=" * 60)
            print("✓ 数据库验证通过！")
            print("=" * 60)
        else:
            print("=" * 60)
            print("✗ 数据库验证未通过，请检查上述标记为 ✗ 的项目")
            print("=" * 60)


def load_config():
    """
    从配置文件加载数据库配置
    
    Returns:
        dict: 配置字典
    """
    config = {
        'host': 'localhost',
        'port': 3306,
        'username': 'root',
        'password': '',
        'database': 'shopping_platform_db'
    }
    
    config_file = Path(__file__).parent / 'db_config.properties'
    if config_file.exists():
        try:
            with open(config_file, 'r', encoding='utf-8') as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith('#') and '=' in line:
                        key, value = line.split('=', 1)
                        key = key.strip()
                        value = value.strip()
                        
                        if key == 'db.url':
                            import re
                            match = re.search(r'jdbc:mysql://([^:]+):(\d+)/([^?]+)', value)
                            if match:
                                config['host'] = match.group(1)
                                config['port'] = int(match.group(2))
                                config['database'] = match.group(3)
                        elif key == 'db.username':
                            config['username'] = value
                        elif key == 'db.password':
                            config['password'] = value
        except Exception as e:
            print(f"  警告：读取配置文件失败：{e}")
    
    return config


def main():
    """主函数"""
    print("=" * 60)
    print("传统分布式购物平台 - 数据库验证")
    print("=" * 60)
    
    config = load_config()
    
    verifier = DatabaseVerifier(
        host=config['host'],
        port=config['port'],
        username=config['username'],
        password=config['password'],
        database=config['database']
    )
    
    if not verifier.connect():
        print("\n✗ 无法连接到 MySQL 服务器，请检查配置")
        sys.exit(1)
    
    results = {}
    
    results['表存在性'] = verifier.check_tables_exist()
    results['表总数'] = verifier.check_table_count()
    results['模块分布'] = verifier.check_module_distribution()
    results['默认数据'] = verifier.check_default_data()
    results['测试数据'] = verifier.check_test_data()
    
    verifier.print_summary(results)
    
    verifier.disconnect()
    
    sys.exit(0 if all(r.get('success', False) for r in results.values()) else 1)


if __name__ == "__main__":
    main()
