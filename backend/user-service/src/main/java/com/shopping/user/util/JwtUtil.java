package com.shopping.user.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类
 * 功能：生成 JWT 令牌、解析令牌中的用户信息、验证令牌有效性、手动失效令牌（黑名单）
 * 基于 JJWT 框架实现，使用 Redis 存储令牌黑名单（支持分布式环境和自动过期）
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")//从配置文件获取
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    // Redis 模板，用于存储令牌黑名单
    @javax.annotation.Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 将令牌加入黑名单（使令牌失效）
     * @param token 需要失效的令牌
     */
    public void invalidateToken(String token) {
        // 存储到 Redis，key 为 "blacklist:token"，值为 "1"，过期时间为令牌的剩余有效期
        if (token != null && !token.isEmpty()) {
            redisTemplate.opsForValue().set("blacklist:" + token, "1", expiration, TimeUnit.MILLISECONDS);
        }
    }

    //获取 JWT 签名密钥（转换为 HS512 算法要求的 Key 对象）
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT 令牌
     * @param userId 用户 ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT 令牌字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // 设置主题：存储用户 ID（核心标识）
                .claim("username", username) // 自定义 Claim：添加用户名
                .claim("role", role) // 自定义 Claim：添加用户角色
                .issuedAt(now) // 设置令牌签发时间
                .expiration(expiryDate) // 设置令牌过期时间
                .signWith(getSigningKey()) // 设置签名密钥（使用 HS512 算法）
                .compact(); // 压缩生成最终的令牌字符串
    }

    /**
     * 从令牌中获取用户 ID
     * @param token JWT 令牌
     * @return 用户 ID
     */
    public Long getUserIdFromToken(String token) {
        // 解析令牌并获取 Claims（JWT 的负载部分，存储所有自定义/内置信息）
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // 设置验签密钥（必须和生成时一致）
                .build() // 构建解析器
                .parseSignedClaims(token) // 解析令牌（验证签名、有效期等）
                .getPayload(); // 获取负载部分（Claims）

        // 从 subject 中获取用户 ID 并转换为 Long 类型
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 验证令牌有效性
     * @param token JWT 令牌
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            // 第一步：检查令牌是否在 Redis 黑名单中
            if (redisTemplate.hasKey("blacklist:" + token)) {
                return false;
            }

            // 第二步：解析令牌（自动验证签名、有效期、格式等）
            Jwts.parser()
                    .verifyWith(getSigningKey()) // 设置验签密钥
                    .build()
                    .parseSignedClaims(token); // 解析失败会抛出 JwtException

            // 所有验证通过，返回 true
            return true;
        } catch (JwtException e) {
            // 捕获所有 JWT 相关异常（如签名错误、令牌过期、令牌篡改、格式错误等）
            return false;
        }
    }
}