package com.kimilguk.boot4.domain.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository<엔티티클래스명, PK타입>을 상속하면 기본CRUD 메소드가 자동생성된다.
public interface UsersRepository extends JpaRepository<Users, Long> {
  //findBy~변수명은 자동으로 select 쿼리를 생성해 준다.
  Optional<Users> findByEmail(String email);//신규등록-업데이트 구분용
  //Optinal<객체>는 객체가 null 일 때 에러가 발생되는 것을 방지하는 자바8버전 부터 만들어진 클래스 이다. 주로 반환 값이 null 일 수 있을 때 사용한다.
}
