#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
传统分布式购物平台 - 数据库初始化脚本
功能：执行 SQL 脚本创建数据库和表结构
创建时间：2026-03-17
"""

import os
import sys
import mysql.connector
from mysql.connector import Error
from pathlib import Path
from datetime import datetime


class DatabaseInitializer:
    """数据库初始化类"""
    
    def __init__(self, host='localhost', port=3306, username='root', password='', database=''):
        """
        初始化数据库连接配置
        
        Args:
            host: MySQL 服务器地址
            port: MySQL 服务器端口
            username: 用户名
            password: 密码
            database: 数据库名（创建数据库时可为空）
        """
        self.host = host
        self.port = port
        self.username = username
        self.password = password
        self.database = database
        self.connection = None
    
    def connect(self, database=''):
        """
        连接到 MySQL 服务器
        
        Args:
            database: 要连接的数据库名，为空则不指定数据库
            
        Returns:
            bool: 连接是否成功
        """
        try:
            self.connection = mysql.connector.connect(
                host=self.host,
                port=self.port,
                user=self.username,
                password=self.password,
                database=database if database else None,
                buffered=True,
                autocommit=True
            )
            if self.connection.is_connected():
                print(f"✓ 成功连接到 MySQL 服务器")
                return True
        except Error as e:
            print(f"✗ 连接失败：{e}")
            return False
        return False
    
    def disconnect(self):
        """断开数据库连接"""
        try:
            if self.connection and self.connection.is_connected():
                self.connection.close()
                print("✓ 数据库连接已关闭")
        except Exception:
            pass
    
    def execute_sql_file(self, file_path, show_info=True):
        """
        执行 SQL 文件
        
        Args:
            file_path: SQL 文件路径
            show_info: 是否显示执行信息
            
        Returns:
            bool: 执行是否成功
        """
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                sql_content = f.read()
            
            if show_info:
                print(f"  执行：{os.path.basename(file_path)}")
            
            cursor = self.connection.cursor()
            
            statements = self._split_sql_statements(sql_content)
            
            success_count = 0
            for statement in statements:
                statement = statement.strip()
                if statement and not statement.startswith('--'):
                    try:
                        cursor.execute(statement)
                        
                        if statement.upper().startswith('USE '):
                            db_name = statement.split()[1].strip('`;')
                            self.database = db_name
                            try:
                                self.connection.database = db_name
                            except Exception:
                                pass
                        
                        success_count += 1
                    except Error as e:
                        if show_info:
                            print(f"    警告：{e}")
            
            self.connection.commit()
            cursor.close()
            
            if show_info:
                print(f"  ✓ 执行成功，共 {success_count} 条语句")
            
            return True
            
        except Error as e:
            print(f"  ✗ 执行失败：{e}")
            return False
        except Exception as e:
            print(f"  ✗ 读取文件失败：{e}")
            return False
    
    def _split_sql_statements(self, sql_content):
        """
        分割 SQL 语句（处理多行语句和分号）
        
        Args:
            sql_content: SQL 文件内容
            
        Returns:
            list: SQL 语句列表
        """
        statements = []
        current_statement = ''
        in_string = False
        string_char = None
        
        i = 0
        while i < len(sql_content):
            char = sql_content[i]
            
            if char == '-' and i + 1 < len(sql_content) and sql_content[i + 1] == '-' and not in_string:
                while i < len(sql_content) and sql_content[i] != '\n':
                    i += 1
                continue
            
            if not in_string and char in ('"', "'"):
                in_string = True
                string_char = char
                current_statement += char
            elif in_string and char == string_char:
                in_string = False
                string_char = None
                current_statement += char
            elif char == ';' and not in_string:
                current_statement += char
                stmt = current_statement.strip()
                if stmt and stmt != ';':
                    statements.append(stmt)
                current_statement = ''
            else:
                current_statement += char
            
            i += 1
        
        if current_statement.strip():
            stmt = current_statement.strip()
            if stmt and stmt != ';':
                statements.append(stmt)
        
        return statements
    
    def create_database(self, db_name, charset='utf8mb4', collation='utf8mb4_unicode_ci'):
        """
        创建数据库
        
        Args:
            db_name: 数据库名称
            charset: 字符集
            collation: 排序规则
            
        Returns:
            bool: 创建是否成功
        """
        try:
            cursor = self.connection.cursor()
            
            cursor.execute(f"DROP DATABASE IF EXISTS {db_name}")
            print(f"  ✓ 已删除旧数据库（如果存在）: {db_name}")
            
            cursor.execute(
                f"CREATE DATABASE {db_name} DEFAULT CHARACTER SET {charset} COLLATE {collation}"
            )
            print(f"  ✓ 数据库创建成功：{db_name}")
            
            cursor.close()
            return True
            
        except Error as e:
            print(f"  ✗ 创建数据库失败：{e}")
            return False
    
    def database_exists(self, db_name):
        """
        检查数据库是否存在
        
        Args:
            db_name: 数据库名称
            
        Returns:
            bool: 数据库是否存在
        """
        try:
            cursor = self.connection.cursor()
            cursor.execute(
                "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = %s",
                (db_name,)
            )
            result = cursor.fetchone()
            cursor.close()
            return result is not None
        except Error as e:
            print(f"  ✗ 检查数据库失败：{e}")
            return False


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
        'password': ''
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
                            match = re.search(r'jdbc:mysql://([^:]+):(\d+)/', value)
                            if match:
                                config['host'] = match.group(1)
                                config['port'] = int(match.group(2))
                        elif key == 'db.username':
                            config['username'] = value
                        elif key == 'db.password':
                            config['password'] = value
            print(f"✓ 已加载配置文件：{config_file.name}")
        except Exception as e:
            print(f"  警告：读取配置文件失败：{e}")
    else:
        print(f"  提示：未找到配置文件，使用默认配置")
    
    return config


def main():
    """主函数"""
    print("=" * 60)
    print("传统分布式购物平台 - 数据库初始化")
    print("=" * 60)
    print()
    
    script_dir = Path(__file__).parent
    db_name = "shopping_platform_db"
    
    config = load_config()
    
    initializer = DatabaseInitializer(
        host=config['host'],
        port=config['port'],
        username=config['username'],
        password=config['password']
    )
    
    if not initializer.connect():
        print("\n✗ 无法连接到 MySQL 服务器，请检查配置")
        sys.exit(1)
    
    print()
    print("-" * 60)
    print("步骤 1: 创建数据库")
    print("-" * 60)
    
    if not initializer.create_database(db_name):
        initializer.disconnect()
        sys.exit(1)
    
    try:
        cursor = initializer.connection.cursor()
        cursor.execute(f"USE {db_name}")
        cursor.close()
        print(f"  ✓ 已切换到数据库：{db_name}")
    except Exception as e:
        print(f"  ✗ 切换数据库失败：{e}")
        initializer.disconnect()
        sys.exit(1)
    
    print()
    print("-" * 60)
    print("步骤 2: 执行数据库初始化脚本")
    print("-" * 60)
    
    sql_files = [
        '00_create_database.sql',
        '01_user_auth.sql',
        '02_product_inventory.sql',
        '03_order_payment.sql',
        '04_logistics_comment.sql',
        '05_content_stats.sql',
        '06_store.sql'
    ]
    
    success_count = 0
    for sql_file in sql_files:
        file_path = script_dir / sql_file
        if file_path.exists():
            if initializer.execute_sql_file(file_path):
                success_count += 1
        else:
            print(f"  ✗ 文件不存在：{file_path}")
    
    print()
    print("-" * 60)
    print("初始化完成")
    print("-" * 60)
    print(f"✓ 成功执行 {success_count}/{len(sql_files)} 个脚本")
    print()
    
    choice = input("是否要插入测试数据？(y/n): ").strip().lower()
    if choice == 'y':
        print()
        print("-" * 60)
        print("步骤 3: 插入测试数据")
        print("-" * 60)
        
        test_data_file = script_dir / '99_test_data.sql'
        if test_data_file.exists():
            if initializer.execute_sql_file(test_data_file):
                print("✓ 测试数据插入成功")
            else:
                print("✗ 测试数据插入失败")
        else:
            print(f"✗ 测试数据文件不存在：{test_data_file}")
    
    initializer.disconnect()
    
    print()
    print("=" * 60)
    print("数据库初始化完成！")
    print("=" * 60)
    print()
    print("数据库名称:", db_name)
    print("主机:", config['host'])
    print("端口:", config['port'])
    print()
    print("提示：运行 db_verify.py 验证数据库")


if __name__ == "__main__":
    main()
