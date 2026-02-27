package com.kimilguk.boot4.domain.posts;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest // 스프링부트에 Junit5=주피터가 포함되어서 구현된다.
class PostsRepositoryTest {
	/*
	 * private: 다른 클래스에서 가로채 사용하지 못하도록(보안) 
	 * static final: 앱이 처음 실행되는 처음 1번에 모두 메모리에 할당되기 때문에, 앱(인스턴스)에서 하나의 값만 저장해서
	 * 사용 하도록(고정)되고, final 로 선언된(값이 할당된) 이후 변수의 값을 바꿀 수 없다.
	 * final: 개별 클래스가 실행되는 런타임 시 메모리에 할당됨=현재 클래스에서만 사용(무 상속) 런타임 시 메모리에 할당된
	 * 일반변수(static, final이 안 붙은)는 선언된 이후 변수 값을 바꿀 수 있다.
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired // 의존성주입(외부클래스를 스프링 빈으로 등록 후 객체로 생성)
	PostsRepository postsRepository;

	@AfterEach
	void tearDown() throws Exception {
		postsRepository.deleteAll();// test()메소드가 끝날 때마다 Posts테이블 저장소의 모든 데이터를 삭제한다.
	}

	@Test
	void test() {
		// fail("Not yet implemented");
		postsRepository.save(
				Posts.builder()
				.title("게시글제목2")
				.content("게시글내용2")
				.author("user")
				.build()
		);
		// DB posts 테이블에 저장된 값 출력(아래)
		List<Posts> postsList = postsRepository.findAll();
		Posts posts = postsList.get(0);
		logger.debug("등록된 레코드수:" + postsRepository.count());
		logger.info("디버그:" + posts.toString());
		// 위 debug 메소드는 출력이 되지 않는다. 로깅 레벨을 기본값이 info 이다.
		//로깅레벨을 수정하려면, application.properties 파일에서 아래 내용 추가한다.
		//logging.level.root=debug (참고, 로깅레벨 출력순서: debug>info>warn>error)
	}

}
