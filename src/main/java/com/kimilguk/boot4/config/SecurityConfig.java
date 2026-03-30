package com.kimilguk.boot4.config;

import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.kimilguk.boot4.config.auth.Role;
import com.kimilguk.boot4.config.auth.dto.OAuthAttributes;
import com.kimilguk.boot4.config.auth.dto.SessionUser;
import com.kimilguk.boot4.domain.users.Users;
import com.kimilguk.boot4.domain.users.UsersRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@EnableMethodSecurity //메소드 단위로 권한 체크를 할 수 있도록 활성화
@EnableWebSecurity //http Url 접근권한을 Role 레벨 별로 사용 가능하도록 활성화
@Configuration
@RequiredArgsConstructor //final 매개변수가 있는 생성자 메소드가 자동 생성된다
public class SecurityConfig {
	private final UsersRepository usersRepository;//네이로 인증정보를 DB에 저장하기 위해서 객체생성
	private final HttpSession httpSession;//네아로 인증도 로그인 상태유지를 위해 세션에 저장하기 위해서 객체생성
	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
		//return new DefaultOAuth2UserService(); //응답 받은 사용자 정보를 기본 처리하는 @빈 추가
		//위 return 구문을 주석처리 후 아래 네아로 API 요청 시 자동 실행되는 코드를 추가(아래 코드 모두).
		return new OAuth2UserService<OAuth2UserRequest, OAuth2User>() {
		@Override//네아로 API 요청 시 자동 실행된다.
		public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService delegate = new DefaultOAuth2UserService();//OAuth2 객체생성
		OAuth2User oAuth2User = delegate.loadUser(userRequest);//네아로 사용자정보 객체생성
		String registrationId = userRequest.getClientRegistration().getRegistrationId();//현재 로그인 진행중인 서비스 구분 즉, naver, google, kakao 등 여러 외부 API를 구분하는 용도
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();//현재 로그인 진행중인 서비스의 응답 값의 키 이름을 가져온다.
		  OAuthAttributes attributes = OAuthAttributes.of(registrationId,userNameAttributeName,oAuth2User.getAttributes());
		  Users user = usersRepository.findByEmail(attributes.getEmail())
		   .map(entity->entity.update(attributes.getName(), attributes.getPicture()))
		   .orElse(attributes.toEntity());//신규등록 시 세션에 권한 Role.USER(일반사용자) 값이 강제로 입력됨. 
		  //중복 이메일이라면 update, 그렇지 않다면 insert
		  user = usersRepository.save(user);//update 또는 insert 쿼리 실행
		  //로그인 유지를 위한 세션 생성처리(아래)
		  httpSession.setAttribute("user", new SessionUser(user));//네아로때문에 SessionUser에 생성자를 추가함.
		  return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),//네아로 API 로그인 회원도 스프링시큐리티의 로그인 권한을 부여하도록 한다. 로그인 인증은 싱글톤 객체로 1번만 실행된다.
		  attributes.getAttributes(),
		  attributes.getNameAttributeKey());
		  }
		};
	} //네이버 OAuth2User정보를 Session에 저장 시켜서 기존 로그인과 연동되게 처리함.(위)

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
				.authorizeHttpRequests(auth -> auth//.anyRequest().permitAll()) // 모든 요청 허용
						.requestMatchers("/simple_users/**").hasRole(Role.ADMIN.name())
						.requestMatchers("/signup/**","/posts/read/**","/h2-console/**","/error/**","/kakaomap/**").permitAll()
						.requestMatchers("/api/**","/posts/**").hasAnyRole(Role.USER.name(),Role.ADMIN.name())
						.requestMatchers("/","/*.html").permitAll() // 특정 경로만 허용
						.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // static 폴더만 허용
						.anyRequest().authenticated()) // 기본요청은 모두인증 필요 해석은 뒤에서 부터
				.formLogin(form -> form.defaultSuccessUrl("/")) //스프링시큐리티에 내장된 로그인 폼을 사용
				.oauth2Login(oauth2 -> oauth2 //OAuth2 로그인 설정 추가
						.userInfoEndpoint(userInfo -> userInfo //네아로 로그인 성공 후 프로바이더 에서 사용자 정보 가져오기 설정 자동생성
								.userService(customOAuth2UserService()) //로그인 성공 시 응답 받은 정보를 저장하는 코딩을 추가
								)
						)
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