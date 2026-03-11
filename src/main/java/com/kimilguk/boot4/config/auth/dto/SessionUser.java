package com.kimilguk.boot4.config.auth.dto;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String role;
    //지금은 아이디 뿐이지만, 나중에 회원DB로 로그인시 필드 변수가 추가된다.
    //시리얼라이즈는 데이터직렬화 처리 시 의미 있는 데이터로 구분 하는 기능.
    public SessionUser(String name,String role) {
        this.name = name; 
        this.role = role;
    }
}