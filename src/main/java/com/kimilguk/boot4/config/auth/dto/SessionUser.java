package com.kimilguk.boot4.config.auth.dto;

import java.io.Serializable;

import com.kimilguk.boot4.domain.users.Users;

import lombok.Getter;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String role;
    private String email;//네아로 API때문에 추가
	private String picture;//네아로 API때문에 추가
    //지금은 아이디 뿐이지만, 나중에 회원DB로 로그인시 필드 변수가 추가된다.
    //시리얼라이즈는 데이터직렬화 처리 시 의미 있는 데이터로 구분 하는 기능.
    public SessionUser(String name,String role) {
        this.name = name; 
        this.role = role;
    }
    //네아로 API때문에 추가
  	public SessionUser(Users user) {
  		this.name = user.getName();
  		this.email = user.getEmail();
  		this.picture = user.getPicture();
  		this.role = user.getRoleKey();
  	}
}