package com.kimilguk.boot4.config.auth;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME) //이 인터페잇의 생명주기-여기선 실행시까지 남아 있는다.
@Target(PARAMETER) // 이 인터페이스는 파라미터로만 사용한다.
public @interface LoginUser {

}
