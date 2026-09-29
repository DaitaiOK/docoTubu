package com.example.docotubu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * パスワードのハッシュ化方式の設定
 */
@Configuration
public class PasswordConfig {

    /**
     * BCrypt（ソルト付きの一方向ハッシュ）でパスワードをハッシュ化・照合する
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
