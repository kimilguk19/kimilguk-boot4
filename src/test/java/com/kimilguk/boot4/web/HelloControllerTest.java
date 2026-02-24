package com.kimilguk.boot4.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
@AutoConfigureMockMvc//API(/hello)경로로 접근하기 위한 샘플 MVC가 필요하다.
@SpringBootTest//초기 코드 상단에 Junit5=주피터가 포함되어서 구현되는 @애노테이션을 추가.
class HelloControllerTest {

	@BeforeAll//JUnit실행 전전처리
	static void setUpBeforeClass() throws Exception {
		System.out.println("@BeforeAll");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("@AfterAll");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		//fail("Not yet implemented");
		//assert로 시작하는 에러 검출용 메소드를 사용
	    //assertEquals(2, 2);//기본메소드, 좀더 향상된 기능의 assertThat을 사용
	    //결과출력방식: assertThat(비교대상 값).비교매소드(비교기준값)
	    assertThat(10).isEqualTo(10);
	}
	@Autowired//스프링 빈(스프링에서 실행 가능한 클래스)로 등록
	private MockMvc mockMvc;

   @Test
   void hello() throws Exception {
      mockMvc.perform(get("/hello"))
      .andExpect(status().isOk()) //expect는 비교대상 값이다.
      .andExpect(content().string("Hello"));
   }
}
