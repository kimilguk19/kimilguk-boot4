package com.kimilguk.boot4.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter//엔티티 출력이 가능하게 구현된다
@RequiredArgsConstructor//final 매개변수가 있는 생성자메소드가 자동 생성된다
public enum Role {
	GUEST("ROLE_GUEST","손님"),
    USER("ROLE_USER","일반사용자"),
    ADMIN("ROLE_ADMIN","관리자");
    private final String key;//ROLE_GUEST, ROLE_USER, ROLE_ADMIN 값이다.
    private final String title; //지금 사용하지 않고, 네이버 Api 용으로 사용예정
}
