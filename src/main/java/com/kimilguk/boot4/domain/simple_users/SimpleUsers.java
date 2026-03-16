package com.kimilguk.boot4.domain.simple_users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kimilguk.boot4.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter//엔티티 출력이 가능하게 구현된다
@NoArgsConstructor//엔티티 생성자를 자동으로 추가한다
@Entity//엔티티와 매핑되는 저장소를 만든다
@Table(
    name="SimpleUsers",//실제 물리 테이블은 simple_users가 된다.
    uniqueConstraints= {//유일제약조건 번역하면 uniqueConstraints
       @UniqueConstraint(
             columnNames= {"username"}//1개 이상일수 있기 때문에 배열로
        )
    }
)
public class SimpleUsers extends BaseTimeEntity {
	@Id//주키 Primary Key 로 만든다.자동증가값으로 구현한다(아래) 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Column(nullable=false)
	private String username;//회원로그인 아이디로 사용(유일 값으로 설정)
	@Column(nullable=false)
	private String password;//회원로그인 암호
	@Column(nullable=false)
	private String role;//회원권한
	@Column(nullable=false)
	private Boolean enabled;//회원사용여부(false시 인증되더라도 로그인 불가)
	@Builder//Dto 클래스에서 사용, 입력 기능으로 다른 클래스에서 build() 메소드 형식으로 사용가능
	public SimpleUsers(String username, String password, String role, Boolean enabled) {
	    this.username = username;
	    this.password = password;
	    this.role = role;
	    this.enabled = enabled;
	}
	//회원 수정 시 쿼리로 DB핸들링 하지 않아도 아래 메소드로 바로 업데이트 된다.(더티 체킹 Dirty Checking)
	public void update(String username, String password, String role, Boolean enabled) {
	    this.username = username;
	    //스프링 시큐리티를 사용하기 때문에 DB에 저장할 때는 비번을 반드시 암호화 해야 한다.
	    String encPassword = null;
	    if(!password.isEmpty()) {//오픈소스라서 많이 사용되는 블로피시 암호화 클래스를 사용한다.
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        encPassword = passwordEncoder.encode(password);
	        this.password = encPassword;
	    }
	    this.role = role;
	    this.enabled = enabled;
	}
}
