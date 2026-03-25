package com.kimilguk.boot4.domain.users;

import com.kimilguk.boot4.config.auth.Role;
import com.kimilguk.boot4.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter //엔티티 출력이 가능하게 구현된다
@NoArgsConstructor //엔티티 생성자를 자동으로 추가한다
@Entity //엔티티와 매핑되는 DB테이블 저장소를 만든다
public class Users extends BaseTimeEntity {
	@Id //주키 Primary Key 로 만든다
    @GeneratedValue(strategy=GenerationType.IDENTITY) //자동증가값으로 구현한다
    private Long id;
    @Column(nullable=false)
    private String name; //네이버에서 반환받는 name 변수값
    @Column(nullable=false)
    private String email; //네이버에서 반환받는 email 변수값
    @Column
    private String picture; //네이버에서 반환받는 profile_image 변수값
    @Enumerated(EnumType.STRING)//role에 들어갈 자료의 형태는 String이 아니고 나열형이다.
    @Column(nullable=false)
    private Role role; //스프링시큐리티 로직을 거쳐야 하기 때문에 필요
    @Builder //Dto클래스에서 사용, 입력 기능으로 다른 클래스에서 build() 메소드 형식으로 사용가능
    public Users(String name, String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    //엔티티에서 업데이트쿼리 대신 메소드로 처리해도 DB에 저장된다.
    public Users update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return Users.this;
    }

    //Role 나열형 이라서 일반 getRole() 메소드로는 부족하다.
    public String getRoleKey() {
        return this.role.getKey();//ROLE_ADMIN, ROLE_USER, ...반환
    }
}
