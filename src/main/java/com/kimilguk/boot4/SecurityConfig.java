package com.kimilguk.boot4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 모든 요청 허용
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); //프레임 옵션 비활성화
		return http.build();
	}
}