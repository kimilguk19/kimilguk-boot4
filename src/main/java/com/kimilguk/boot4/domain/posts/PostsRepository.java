package com.kimilguk.boot4.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;
//JpaRepository<엔티티클래스명, PK타입>을 상속하면 기본CRUD 메소드가 자동 생성된다.
//<T, ID>는 동일패키지안에 있는 Posts 클래스의 PK타입이 Long이므로 <Posts, Long>으로 지정한다.
//@Repository //이 애노테이션은 생략해도 정상작동 된다. 우리 작업에서는 사용하지 않는다.
public interface PostsRepository extends JpaRepository<Posts, Long> {
	//save(), findAll(),수정은 엔티티의 값만수정하면 DB값도 연동된다,
	//delete(), findById() 등 기본 CRUD 메소드가 자동으로 생성된다.
}
