package com.kimilguk.boot4.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimilguk.boot4.domain.posts.Posts;
import com.kimilguk.boot4.domain.posts.PostsRepository;
import com.kimilguk.boot4.web.dto.PostsDto;
//API(Url)경로로 접근하기 위한 목업 MVC가 필요하다.
@AutoConfigureMockMvc
//스프링부트에 Junit5=주피터가 포함되어서 구현된다.
@SpringBootTest
class PostsApiControllerTest {
	//의존성주입(외부클래스를 스프링 빈으로 등록 가능하게 명시 및 객체로생성)
	@Autowired 
	private MockMvc mockMvc;
	@Autowired
	private PostsRepository postsRepository;

	@Test
	void save() throws Exception {
	    PostsDto requestDto = PostsDto.builder()
	    .title("게시물제목")
	    .content("게시물내용")
	    .author("작성자")
	    .build();
	    mockMvc.perform(
	    post("/api/posts/save")//보안(데이터숨김)접근
	    .contentType(MediaType.APPLICATION_JSON)//전송 값 한글 처리
	    .content(new ObjectMapper().writeValueAsString(requestDto))//전송값 내용
	    )
	    .andExpect(status().isOk());//기준값Ok(200)과 반환값 비교 성공시 객체생성.
	List<Posts> postList = postsRepository.findAll();
	System.out.println("디버그" + postList.toString()); //Posts엔티티 클래스의 toString()이 실행된다.
	System.out.println("디버그" + postList.get(0).getCreateDate());//등록일시가 null 이 나오는 이유는 Application.java에서 Auditing을 활성화 시키면 해결된다.@EnableJpaAuditing//JPA공통DB필드(변수)사용
	}
	//IndexController 의 postList()메소드 실행테스트(아래)
    @Test
    public void postList() throws Exception {
        mockMvc.perform(
        get("/posts") //비 보안(데이터노출)접근
        )
        .andExpect(status().isOk())
        .andDo(print()); //반환성공 후 body 내용을 출력한다.
       //위 처럼 메소드를 체인형식으로 연결해서 코딩을 줄이는 방식을 메소드 체인닝 이라고 한다.
    }
}
