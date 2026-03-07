package com.kimilguk.boot4.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository<엔티티클래스명, PK타입>을 상속하면 기본CRUD 메소드가 자동 생성된다.
public interface FileRepository extends JpaRepository<File, Long> {
  //save(), findAll(),수정은 엔티티의 값만수정하면 DB값도 연동된다, delete()
}
//클래스에서 인터페이스를 상속받을 때는 implements 를 사용하고, 인터페이스에서 상속받을 때는 extends 사용한다.
