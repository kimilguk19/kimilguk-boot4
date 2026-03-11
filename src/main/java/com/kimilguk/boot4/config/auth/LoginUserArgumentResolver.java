package com.kimilguk.boot4.config.auth;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.kimilguk.boot4.config.auth.dto.SessionUser;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor//final 매개변수가 있는 생성자메소드가 자동 생성된다.
@Component//자동으로 스프링 빈으로 등록시키는 애노테이션
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
	private final HttpSession httpSession;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		//return false;
		//컨트롤러 클래스의 파라미터 타입에 포함되는지 비교(아래)
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
        return isUserClass;//즉, 컨트롤러의 메소드 호출 시 파라미터에 세션값이 포함되면 true로 반환해서 resolveArgument 메소드가 실행된다. 
	}

	@Override
	public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
		//return null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//인증객체생성
	    String userName = authentication.getName();//admin, user, guest 중 1개가 들어간다.
	    Role userAuthor = null;
	    if(httpSession.getAttribute("user")==null && !"anonymousUser".equals(userName)) {//주, 초기 코딩 인증 값이 없을 때 anonymousUser 값을 갖는다. 하단 코딩에서 httpSession 을 set으로 넣는다.
	        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();//권한생성
	        if(roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
	            userAuthor = Role.ADMIN;
	        } else if(roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
	            userAuthor = Role.USER;
	        } else {
	            userAuthor = Role.GUEST;
	        }
		    // 세션 값 user 생성 핵심(아래)userAuthor.getKey()= ROLE_GUEST,ROLE_USER,ROLE_ADMIN
		    httpSession.setAttribute("user", new SessionUser(userName,userAuthor.getKey()));
	    }
	    //System.out.print(userAuthor+"여기LoginUserArgumentResolver resolveArgument() userName: " + userName);
	return httpSession.getAttribute("user"); //세션값을 가져와서 return한다
	}
}
