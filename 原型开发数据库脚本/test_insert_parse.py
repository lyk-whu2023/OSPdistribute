#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""测试特定 SQL 语句的解析"""

sql = """INSERT INTO users (username, password, phone, email, nickname, role, status) 
VALUES ('admin', '$2a$10$XoLvF5C2dz9.7JHK8sN.TOxl9yYp4CqQKQ8MmqPHAKQYYQZQxZP8W', '13800138000', 'admin@shopping.com', '系统管理员', 'admin', 1);"""

def _split_sql_statements(sql_content):
    """分割 SQL 语句"""
    statements = []
    current_statement = ''
    in_string = False
    string_char = None
    
    i = 0
    while i < len(sql_content):
        char = sql_content[i]
        
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

result = _split_sql_statements(sql)
print(f"解析出 {len(result)} 条语句")
print(f"语句内容：{result[0] if result else '无'}")
