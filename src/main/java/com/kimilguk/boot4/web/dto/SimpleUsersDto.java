package com.kimilguk.boot4.web.dto;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kimilguk.boot4.domain.simple_users.SimpleUsers;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor//생성자 메소드를 자동을 생성한다
@Getter @Setter//Get, Set 메소드를 자동으로 생성한다
public class SimpleUsersDto {
	private Long id;//회원 고유PK(Primary Key)
    private String username;//회원 고유ID(Unique Value)
    private String password;//회원 로그인 암호
    private String role;//회원 권한(ADMIN, USER)
    private Boolean enabled;//회원인데 로그인가능 여부
    private LocalDateTime modifiedDate;//정보 수정일시(머스테치 화면출력에 필요)
  
     //@컨트롤러 조회에서 사용
    public SimpleUsersDto(SimpleUsers entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.role = entity.getRole();
        this.enabled = entity.getEnabled();
        this.modifiedDate = entity.getModifiedDate();
    }
    @Builder//@컨트롤러에서 저장,수정 값 등록에 사용(아래)
    public SimpleUsersDto(String username, String password, String role, Boolean enabled) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }
    //@서비스 클래스에서 저장 시 사용(아래) 반환 값은 @Entity객체 
    public SimpleUsers toEntity() {
      //스프링 시큐리티를 사용하기 때문에 DB에 저장할 때는 비번을 암호화 하는 것이 필수이다.
      String encPassword = null;
      if(!password.isEmpty()) {//오픈소스라서 많이 사용되는 블로피시 암호화 클래스를 사용한다.
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
          encPassword = passwordEncoder.encode(password);
       }
      return SimpleUsers.builder()
        .username(username)
        .password(encPassword)
        .role(role)
        .enabled(enabled)
        .build();
    }
}
