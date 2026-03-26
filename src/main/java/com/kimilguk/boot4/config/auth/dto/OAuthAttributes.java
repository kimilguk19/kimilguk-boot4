package com.kimilguk.boot4.config.auth.dto;

import java.util.Map;

import com.kimilguk.boot4.config.auth.Role;
import com.kimilguk.boot4.domain.users.Users;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
    //Map은 키(Key):값(Value)로 구성되는 자료 형이다.
    private Map<String, Object> attributes; //네아로 반환값 저장
    private String nameAttributeKey; //네아로 반환값 중 id와 같음
    private String name; //위 Map 객체를 분해해서 이름을 저장 
    private String email; //위 Map 객체를 분해해서 이메일을 저장
    private String picture ;//위 Map 객체를 분해해서 프로필사진을 저장

    @Builder//이 클래스에서 사용, 생성자 메소드
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes ;//네아로에서 반환받는 json 데이터가 저장될 변수
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }
    //Users 엔티티(DB)에 저장
    public Users toEntity() {
        return Users.builder()
                        .name(name)
                        .email(email)
                        .picture(picture)
                       .role(Role.USER) //네이버 API 로그인한 회원은 무조건 ROLE_USER권한으로 등록한다.
                      .build();
    }

    //여러 외부 로그인 인증을 위한 분기용 of()메소드 생성
	public static OAuthAttributes of(String registrationId,String userNameAttributeName,Map<String, Object> attributes) {
	    if("naver".equals(registrationId)) {
	    //아래 줄은 스프링부트OAuth2에서 생성된다.(참고, 구글에서 "id"대신 "sub"를 자동으로 가져온다)
	    userNameAttributeName = "id";//구글 API에서는 필요 없으나, 네이버 에서는 수동으로 입력해야 한다.
	    return ofNaver(userNameAttributeName,attributes);
	    }
	    return null;
	}
	private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
	    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
	        return OAuthAttributes.builder()
	                .name((String) response.get("name"))
	                .email((String) response.get("email"))
	                .picture((String) response.get("profile_image"))
	                .attributes(response)
	                .nameAttributeKey(userNameAttributeName)
	                .build();
	}
}