package com.shopping.user.service;

import com.shopping.user.entity.OperationLog;
import com.shopping.user.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志服务
 */
@Service
@RequiredArgsConstructor
public class OperationLogService {
    
    private final OperationLogMapper operationLogMapper;
    
    /**
     * 记录操作日志
     */
    public void log(OperationLog log) {
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
    
    /**
     * 记录登录日志
     */
    public void logLogin(Long userId, String username, String ip, String result) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation("登录");
        log.setModule("认证模块");
        log.setMethod("POST /api/auth/login");
        log.setParams("");
        log.setResult(result);
        log.setIp(ip);
        log.setDuration(0L);
        log(log);
    }
    
    /**
     * 记录注册日志
     */
    public void logRegister(Long userId, String username, String ip, String result) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation("注册");
        log.setModule("认证模块");
        log.setMethod("POST /api/auth/register");
        log.setParams("");
        log.setResult(result);
        log.setIp(ip);
        log.setDuration(0L);
        log(log);
    }
    
    /**
     * 记录用户信息更新日志
     */
    public void logUpdateUser(Long userId, String username, String ip, String params, String result) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation("更新用户信息");
        log.setModule("用户模块");
        log.setMethod("PUT /api/users/{id}");
        log.setParams(params);
        log.setResult(result);
        log.setIp(ip);
        log.setDuration(0L);
        log(log);
    }
    
    /**
     * 记录地址操作日志
     */
    public void logAddressOperation(Long userId, String username, String ip, String operation, 
                                   String method, String params, String result) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setModule("地址模块");
        log.setMethod(method);
        log.setParams(params);
        log.setResult(result);
        log.setIp(ip);
        log.setDuration(0L);
        log(log);
    }
}
