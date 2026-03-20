package com.kimilguk.boot4.config;

import javax.sql.DataSource;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.kimilguk.boot4.config.auth.Role;

@EnableMethodSecurity //메소드 단위로 권한 체크를 할 수 있도록 활성화
@EnableWebSecurity //http Url 접근권한을 Role 레벨 별로 사용 가능하도록 활성화
@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
				.authorizeHttpRequests(auth -> auth//.anyRequest().permitAll()) // 모든 요청 허용
						.requestMatchers("/posts/read/**","/h2-console/**","/error/**").permitAll()
						.requestMatchers("/api/**","/posts/**").hasAnyRole(Role.USER.name(),Role.ADMIN.name())
						.requestMatchers("/","/*.html").permitAll() // 특정 경로만 허용
						.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // static 폴더만 허용
						.anyRequest().authenticated()) // 기본요청은 모두인증 필요 해석은 뒤에서 부터
				.formLogin(form -> form.defaultSuccessUrl("/")) //스프링시큐리티에 내장된 로그인 폼을 사용
				.logout(logout -> logout
	                    .logoutUrl("/logout") // Logout URL
	                    .logoutSuccessUrl("/") // Redirect after logout
	                    .invalidateHttpSession(true) // Invalidate session
	                    .deleteCookies("JSESSIONID")) // Delete cookies
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); //프레임 옵션 비활성화
		return http.build();
	}
	// 데이터베이스에서 사용자 정보를 가져오는 UserDetailsManager 빈을 정의
	@Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        // JdbcUserDetailsManager를 사용하여 데이터베이스에서 사용자 정보를 가져옴
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery("SELECT username, password, enabled FROM simple_users WHERE username = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT username, CONCAT('ROLE_', role) FROM simple_users WHERE username = ?");
        return userDetailsManager;
    }
	/* 메모리용 인증과 권한 코딩 (아래)
	@Bean
	public UserDetailsService userDetailsService(BCryptPasswordEncoder passwordEncoder) {
		UserDetails guest = User.builder()
                .username("guest")
                .password(passwordEncoder.encode("1234"))
                .roles("GUEST")
                .build();
		UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .roles("ADMIN")
                .build();
        // InMemoryUserDetailsManager는 테스트용으로 사용하기 위해 UserDetails정보를 메모리에 저장한다.
        return new InMemoryUserDetailsManager(guest, user, admin);
    }
	*/	
	@Bean //BCryptPasswordEndoder 메소드로 비밀번호 암호화는 필수이다.
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}