package com.kimilguk.boot4.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Sort;
import com.kimilguk.boot4.domain.posts.Posts;
import com.kimilguk.boot4.service.posts.PostsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor//final 매개변수가 있는 생성자메소드가 자동 생성된다
@Controller//일반컨트롤러는 반환 값으로 출력할 페이지를 지정한다
public class IndexController {
	//로그 출력 객체생성
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final PostsService postsService;//생성자로 주입

    @GetMapping("/posts")//전체게시물 Read
    public String postList(@PageableDefault(size=5,sort="id",direction=Sort.Direction.DESC) Pageable pageable, Model model) {
    Page<Posts> postsList = postsService.postsList(pageable);
    model.addAttribute("postsList", postsList);//게시글목록 5개
    model.addAttribute("currPage", postsList.getPageable().getPageNumber());//현재페이지번호
    model.addAttribute("pageIndex", postsList.getTotalPages());//전체페이지개수
    model.addAttribute("prevCheck", postsList.hasPrevious());//이전페이지 있는지 체크
    model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());//이전페이지번호 사용
    model.addAttribute("nextCheck", postsList.hasNext());//다음페이지 있는지 체크
    model.addAttribute("next", pageable.next().getPageNumber());//다음페이지번호 사용
    return "posts/post-list";//출력할 페이지명 posts폴더/post-list.mustache파일(html디자인템플릿)
    } // mustache템플릿 html 뷰 파일은 다음시간에 만든다.
}
