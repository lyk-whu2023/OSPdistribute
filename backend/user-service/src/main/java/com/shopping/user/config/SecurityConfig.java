package com.shopping.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Spring Security 配置类（Reactive 模式）
 * 配置认证和授权策略
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * 密码编码器
     * 使用 BCrypt 强哈希算法加密用户密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 过滤链配置（Reactive 模式）
     * 沙盒测试环境：禁用 CSRF、允许所有请求（方便测试）
     */
    @Bean
    @Profile("dev") // 仅在 dev 环境下生效
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable()) // 禁用 CSRF
            .authorizeExchange(auth -> auth
                .anyExchange().permitAll() // 允许所有请求
            )
            .httpBasic(basic -> basic.disable()) // 禁用 HTTP Basic
            .formLogin(form -> form.disable()); // 禁用表单登录

        return http.build();
    }
}