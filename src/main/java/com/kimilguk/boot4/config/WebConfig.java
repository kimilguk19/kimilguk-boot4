package com.kimilguk.boot4.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kimilguk.boot4.config.auth.LoginUserArgumentResolver;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor//final 매개변수가 있는 생성자메소드가 자동 생성된다.
@Configuration//이 클래스에서 설정한 내용이 자동으로 빈으로 등록된다.
public class WebConfig implements WebMvcConfigurer {
	private final LoginUserArgumentResolver loginUserArgumentResolver;//생성자로 주입
	//보통 리졸버는 보안으로 분리된 다른 클래스 데이터에 접근할 때 사용한다.
    //여기서는 모든 앱 페이지에서 세션 값에 접근해서 가져다 사용할 때에 이용한다.
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUserArgumentResolver);//loginUserArgumentResolver를 만든다.
		//WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}
}
