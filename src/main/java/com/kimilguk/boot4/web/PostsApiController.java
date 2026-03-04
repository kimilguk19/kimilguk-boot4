package com.kimilguk.boot4.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kimilguk.boot4.service.posts.PostsService;
import com.kimilguk.boot4.web.dto.PostsDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor//final 매개변수가 있는 생성자메소드가 자동 생성된다
@RestController//반환값으로 json텍스트 내용부분만(body) 전송된다
public class PostsApiController {
	//로그 출력 객체생성
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final PostsService postsService;//생성자로 주입
    //포스트매핑은 페이지 폼에서 데이터 전송하면서 접근가능(보안)
    @PostMapping("/api/posts/save")//저장:Create
    public Long save(@RequestBody PostsDto requestDto) {
        return postsService.save(requestDto);
    }
    //Get매핑은 페이지 URL 주소(쿼리스트링)에서 데이터 전송 하면서 접근가능(비 보안)
    @GetMapping("/api/posts/{id}")//읽기:1개게시물 Read
    public PostsDto postOne(@PathVariable Long id) {
        return postsService.postsOne(id);
    }

    //전체게시물 읽기는 @RestController가 아닌 일반 @Controller에서 디자인 뷰 파일명을 반환하는 다른 클래스에서 처리할 예정이다.
    //Put매핑은 페이지 폼에서 데이터 전송하면서 접근가능(보안)
    @PutMapping("/api/posts/{id}")//수정:Update
    public Long update(@PathVariable Long id, @RequestBody PostsDto requestDto) {
        return postsService.update(id, requestDto);
    }
    @DeleteMapping("/api/posts/{id}")
    public Long delete(@PathVariable Long id) {
        postsService.delete(id);
    return id;
    }
}
