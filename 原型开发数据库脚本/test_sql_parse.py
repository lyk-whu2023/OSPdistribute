#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""测试 SQL 解析"""

from pathlib import Path

def _split_sql_statements(sql_content):
    """分割 SQL 语句"""
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

with open('01_user_auth.sql', 'r', encoding='utf-8') as f:
    content = f.read()

statements = _split_sql_statements(content)

print(f"共解析出 {len(statements)} 条语句\n")

for idx, stmt in enumerate(statements, 1):
    lines = stmt.split('\n')
    first_line = lines[0][:60] if lines else ''
    print(f"{idx}. {first_line}...")
    if 'INSERT' in stmt.upper():
        print(f"   [INSERT 语句]")
        print(f"   完整内容：{stmt}")
        print()
