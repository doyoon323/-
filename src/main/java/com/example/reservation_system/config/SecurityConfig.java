package com.example.reservation_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 스프링에게 "암호화 도구(Bean)"를 등록한다고 알려줍니다.
    // 이제 UserService가 이 도구를 가져다 쓸 수 있게 됩니다.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 시큐리티 설정을 통해 "회원가입" 길을 열어줍니다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 테스트를 위해 CSRF 보안을 잠시 끕니다.
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 회원가입 경로는 누구나 통과! (일단 전부 통과하게 만들어둠)

                //.anyRequest().authenticated() // 나머지는 다 막음
            );
        
        return http.build();
    }
}