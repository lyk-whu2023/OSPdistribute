package com.shopping.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
* JWT 配置类
* 用于集中管理 JWT 相关配置参数
* 注意：实际配置值在 application.yml 中定义
*/
@Configuration
public class JwtConfig {

   /**
    * JWT 签名密钥
    * 用于生成和验证 JWT 令牌的签名
    * 建议使用足够长的随机字符串（至少 32 位）
    */
   @Value("${jwt.secret}")
   private String secret;

   /**
    * JWT 令牌过期时间（毫秒）
    * 默认 24 小时（86400000ms）
    */
   @Value("${jwt.expiration}")
   private Long expiration;

   /**
    * JWT 令牌前缀
    * 用于在 Authorization header 中识别令牌
    * 例如："Bearer <token>"
    */
   @Value("${jwt.prefix:Bearer }")
   private String prefix;

   /**
    * JWT 令牌 header 名称
    * 默认从 "Authorization" header 中获取令牌
    */
   @Value("${jwt.header:Authorization}")
   private String header;

   // Getter 方法

   public String getSecret() {
       return secret;
   }

   public Long getExpiration() {
       return expiration;
   }

   public String getPrefix() {
       return prefix;
   }

   public String getHeader() {
       return header;
   }
}