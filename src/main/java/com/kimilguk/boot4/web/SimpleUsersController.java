package com.kimilguk.boot4.web;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.data.domain.Sort;
import com.kimilguk.boot4.config.auth.LoginUser;
import com.kimilguk.boot4.config.auth.dto.SessionUser;
import com.kimilguk.boot4.domain.simple_users.SimpleUsers;
import com.kimilguk.boot4.service.simple_users.SimpleUsersService;
import com.kimilguk.boot4.util.ScriptUtils;
import com.kimilguk.boot4.web.dto.SimpleUsersDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor//final 매개변수가 있는 생성자 메소드가 자동 생성된다(객체초기화 역할)
@Controller//일반컨트롤러는 반환값으로 출력할 페이지를 지정한다
public class SimpleUsersController {
    private final SimpleUsersService simpleUsersService; //서비스 객체생성

    @GetMapping("/simple_users/save")//회원생성 디자인보기
    public String simpleUsersSave(Model model,@LoginUser SessionUser user) {
        if(user != null) {
        //회원등록 가능한 상태인지 확인하는 용도 index.mustache에 사용되고, 여기선 사용되지 않는다.
        model.addAttribute("sessionUserName", user.getName());
        }
        return "simple_users/save";//템플릿 폴더기준 save.mustache 생략
    }

    @PostMapping("/simple_users/save")//회원생성 API실행
    public String simpleUsersSavePost(HttpServletResponse response,SimpleUsersDto requestDto) throws IOException {
        SimpleUsersDto usersDto = null;
        try { //아래 구문에서 test회원 등록 시 에러를 발생 시켜보고 try~catch 문을 사용하는 방법을 확인한다.
            usersDto = simpleUsersService.findByName(requestDto.getUsername());
        }catch(Exception e){
        }
        if(usersDto == null) { //조건추가
            simpleUsersService.save(requestDto);
            ScriptUtils.alertAndMovePage(response, "저장 되었습니다.", "/simple_users/list");
        }else {
        	System.out.println("중복 아이디 존재");
            ScriptUtils.alertAndBackPage(response, "중복 아이디가 존재합니다. 아이디를 다시 입력해 주세요.");
        }
        return null;//"redirect:/simple_users/list";
    }//저장 후 절대경로로 페이지이동
   // …중략 이전 장  이어서…
    @GetMapping("/simple_users/list")//회원목록 디자인보기
    public String simpleUsersList(@PageableDefault(size=5,sort="id",direction=Sort.Direction.DESC) Pageable pageable, Model model,@LoginUser SessionUser user) {
        if(user != null) {
            //회원등록 가능한 상태인지 확인하는 용도
            model.addAttribute("sessionUserName", user.getName());
        }
        Page<SimpleUsers> usersList = simpleUsersService.usersList(pageable);
        model.addAttribute("usersList", usersList);//회원목록 5개
        model.addAttribute("currPage", usersList.getPageable().getPageNumber());//현재페이지번호
        model.addAttribute("pageIndex", usersList.getTotalPages());//전체페이지개수
        model.addAttribute("prevCheck", usersList.hasPrevious());//이전페이지 있는지 체크
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());//이전페이지번호 사용
        model.addAttribute("nextCheck", usersList.hasNext());//다음페이지 있는지 체크
        model.addAttribute("next", pageable.next().getPageNumber());//다음페이지번호 사용
        return "simple_users/list";//출력할 페이지명 index.mustache파일의 게시판 목록에서 프로그램 참조
    } //static/list.html파일의 디자인 참조
    // …중략 이어서…
 // …중략 이전 장  이어서…
    @GetMapping("/simple_users/update/{username}") //회원상세 디자인보기
    public String simpleUsersUpdate(@PathVariable("username") String username,Model model,@LoginUser SessionUser user) {
        model.addAttribute("simple_user", simpleUsersService.findByName(username));
        return "simple_users/update";
    }

    @PostMapping("/simple_users/update")//회원수정 API실행
    public String simpleUsersUpdatePost(HttpServletResponse response,SimpleUsersDto requestDto) throws IOException {
        simpleUsersService.update(requestDto.getId(), requestDto);
        ScriptUtils.alertAndMovePage(response, "수정 되었습니다.", "/simple_users/update/"+requestDto.getUsername());
        return null;//"redirect:/simple_users/update/"+requestDto.getUsername();
    }

    @PostMapping("/simple_users/delete")//회원삭제 API실행
    public String simpleUsersDelete(HttpServletResponse response,SimpleUsersDto requestDto) throws IOException {
        simpleUsersService.delete(requestDto.getId());
        ScriptUtils.alertAndMovePage(response, "삭제 되었습니다.", "/simple_users/list");
        return null;//"redirect:/simple_users/list";//삭제 후 절대경로로 페이지 이동
    }

 }