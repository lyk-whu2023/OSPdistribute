#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""测试配置文件读取"""

from pathlib import Path

config = {
    'host': 'localhost',
    'port': 3306,
    'username': 'root',
    'password': ''
}

config_file = Path('db_config.properties')
if config_file.exists():
    with open(config_file, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#') and '=' in line:
                key, value = line.split('=', 1)
                key = key.strip()
                value = value.strip()
                print(f'Key: [{key}] = Value: [{value}]')
                
                if key == 'db.username':
                    config['username'] = value
                elif key == 'db.password':
                    config['password'] = value

print(f"\n最终配置：")
print(f"用户名：[{config['username']}]")
print(f"密码：[{config['password']}]")
