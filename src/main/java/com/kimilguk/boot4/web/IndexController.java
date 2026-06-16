package com.kimilguk.boot4.web;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Sort;

import com.kimilguk.boot4.config.auth.LoginUser;
import com.kimilguk.boot4.config.auth.dto.SessionUser;
import com.kimilguk.boot4.domain.posts.ManyFile;
import com.kimilguk.boot4.domain.posts.Posts;
import com.kimilguk.boot4.service.posts.FileService;
import com.kimilguk.boot4.service.posts.ManyFileService;
import com.kimilguk.boot4.service.posts.PostsService;
import com.kimilguk.boot4.service.simple_users.SimpleUsersService;
import com.kimilguk.boot4.util.ScriptUtils;
import com.kimilguk.boot4.web.dto.FileDto;
import com.kimilguk.boot4.web.dto.PostsDto;
import com.kimilguk.boot4.web.dto.SimpleUsersDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor//final 매개변수가 있을 때 생성자메소드가 자동 생성된다
@Controller//일반컨트롤러는 반환 값으로 출력할 페이지를 지정한다
public class IndexController {
	//로그 출력 객체생성
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final PostsService postsService; //생성자로 주입
    private final FileService fileService;//생성자로 주입 이 필요
    private final SimpleUsersService simpleUsersService; //서비스 객체생성
    private final ManyFileService manyFileService;//생성자로 주입 이 필요
    
    @GetMapping("/ai_rag")//ai챗봇 디자인보기
    public String aiRagGet() {
        return "ai_rag";//ai_rag.mustache 생략
    }
    @GetMapping("/ai")//ai챗봇 디자인보기
    public String aiGet() {
        return "ai";//ai.mustache 생략
    }
    @PostMapping("/mypage/delete")//회원삭제 API실행
    public String mypageDelete(HttpServletResponse response,SimpleUsersDto requestDto) throws IOException {
        simpleUsersService.delete(requestDto.getId());
        ScriptUtils.alertAndMovePage(response, "회원탈퇴 되었습니다.", "/logout");
        return null;//"redirect:/simple_users/list";//삭제 후 절대경로로 페이지 이동
    }
    @GetMapping("/mypage/update") //보안 때문에 Path변수는 삭제한다. 회원상세 디자인보기
    public String mypageUpdate(Model model,@LoginUser SessionUser user) {
    	//user세션에서 로그인한 사용자 이름으로 DB에서 사용자 정보를 가져와서 모델에 담는다.
        model.addAttribute("simple_user", simpleUsersService.findByName(user.getName()));
        return "mypage/update";
    }
    @PostMapping("/mypage/update")//회원수정 API실행
    public String simpleUsersUpdatePost(HttpServletResponse response,SimpleUsersDto requestDto) throws IOException {
        simpleUsersService.update(requestDto.getId(), requestDto);
        ScriptUtils.alertAndMovePage(response, "수정 되었습니다.", "/mypage/update");
        return null;//"redirect:/simple_users/update/"+requestDto.getUsername();
    }
    @GetMapping("/signup")//일반회원생성 디자인보기
    public String signupGet() {
        return "signup";//signup.mustache 생략
    }
    @PostMapping("/signup")//회원생성 API실행
    public String signupPost(HttpServletResponse response,SimpleUsersDto requestDto) throws IOException {
        SimpleUsersDto usersDto = null;//중복회원 체크용 객체생성
        try { //아래 try~catch 문을 사용하여 findByName에 에러가 발생 시 멈추지 않고 다음 줄로 진행된다.
            usersDto = simpleUsersService.findByName(requestDto.getUsername());
        }catch(Exception e){
        }
        if(usersDto == null) { //조건추가
        	requestDto.setRole("USER");//해킹 위험 때문에 강제로 일반사용자로 고정함.
            simpleUsersService.save(requestDto);
            ScriptUtils.alertAndMovePage(response, "회원가입 되었습니다. 로그인 후 이용해 주세요.", "/");
        }else {
        	System.out.println("중복 아이디 존재");
            ScriptUtils.alertAndBackPage(response, "중복 아이디가 존재합니다. 아이디를 다시 입력해 주세요.");
        }
        return null;//"redirect:/simple_users/list";
    }//저장 후 절대경로로 페이지이동

    @GetMapping("/kakaomap")
    public String kakaoMap(@RequestParam(value="keyword", defaultValue="천안시")String keyword, Model model) {
        //공공데이터포털에서 전기차 충전소 데이터를 받아서 model객체에 담는 코딩예정(다음시간에 현재는 null)
        RestTemplate restTemplate = new RestTemplate();// RestTemplate 임포트 후 객체를 생성한다.
        // API URL 및 파라미터 분리
        String baseUrl = "https://bigdata.kepco.co.kr/openapi/v1/EVchargeManage.do";
        String addr = keyword; // 검색어를 addr 파라미터로 사용
        String apiKey = "u5fd16awu91me8PmKu0fwtk6sdCNVMje19iL6yrS";
        String returnType = "json";
        String apiUrl = String.format("%s?addr=%s&apiKey=%s&returnType=%s", baseUrl, addr, apiKey, returnType); 
        try {
            // 외부 API 호출 및 JSON 데이터 가져오기
            String response = restTemplate.getForObject(apiUrl, String.class);
            System.out.println("JSON Response: " + response); // JSON 결과를 콘솔에 출력
            model.addAttribute("response", response); // JSON 데이터를 모델에 추가
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("response", "Error fetching data");
        }
        model.addAttribute("keyword", keyword); // 검색어를 모델에 담아서 머스태치에 보내준다.
        return "kakaomap";//resource루트의 templates폴더에 kakaomap.mustache 파일과 연결
    }
    @GetMapping("/posts/update/{id}") //패스경로에 id값이 들어갔다. 아래 @PathVariable 사용해서 메소드의 매개변수에서 사용
    public String postsUpdate(HttpServletResponse response,@PathVariable("id") Long id, Model model,@LoginUser SessionUser user) throws IOException {
    	if(user != null) {
            model.addAttribute("sesstionUserName", user.getName());
            model.addAttribute("sessionRoleName", "ROLE_ADMIN".equals(user.getRole())?"admin":null);
        }//자바의 3항 연산자: if 조건문을 축약한 구문으로 형식은 (조건문)? 조건이 참일 때 값 : 거짓일 때 값 이다.
        PostsDto dto = postsService.postsOne(id);//1개의 레코드만 가져온다.
        if(!user.getName().equals(dto.getAuthor()) && !"ROLE_ADMIN".equals(user.getRole())) {
            ScriptUtils.alertAndBackPage(response, "본인 글만 수정 가능합니다.!");
            return null;//현재 메소드를 빠져 나간다==종료한다.
        }
        model.addAttribute("post",dto);//모델객체에 담아서 mustache로 보낸다.
        if(dto.getFileId() != null) {
        	//단일 첨부파일 처리는 이후 수업에서 작업(아래)
            FileDto fileDto = fileService.getFile(dto.getFileId());
            model.addAttribute("OrigFilename", fileDto.getOrigFilename()); 
        }
        //멀티파일 조회처리
        List<ManyFile> manyFileList = manyFileService.getManyFile(id);
        if(manyFileList.size()>0) {//배열객체의 레코드 개수를 구할 때 size() 메소드를 사용한다.
            model.addAttribute("manyFileList", manyFileList);
        }
        return "posts/posts-update";
    }
    
    @GetMapping("/posts/read/{id}") //패스경로에 id값이 들어갔다. 아래 @PathVariable 사용해서 메소드의 매개변수에서 사용
    public String postsRead(@PathVariable("id") Long id, Model model,@LoginUser SessionUser user) {
    	if(user != null) {
            model.addAttribute("sesstionUserName", user.getName());
            model.addAttribute("sessionRoleName", "ROLE_ADMIN".equals(user.getRole())?"admin":null);
        }//자바의 3항 연산자: if 조건문을 축약한 구문으로 형식은 (조건문)? 조건이 참일 때 값 : 거짓일 때 값 이다.
	    PostsDto dto = postsService.postsOne(id); //1개의 레코드만 가져온다.
	    model.addAttribute("post",dto); //모델객체에 담아서 mustache로 보낸다.
	    if(dto.getFileId() != null) {
	    	//단일 첨부파일 처리는 이후 수업에서 작업(아래)
	        FileDto fileDto = fileService.getFile(dto.getFileId());
	        model.addAttribute("OrigFilename", fileDto.getOrigFilename()); 
	     }
	    //멀티파일 조회처리
	    List<ManyFile> manyFileList = manyFileService.getManyFile(id);
	    if(manyFileList.size()>0) {
	        model.addAttribute("manyFileList", manyFileList);
	    }
	     return "posts/posts-read";
   }
    
    @GetMapping("/posts/save")//Url주소와 posts-save.mustache를 매핑 시킨다.
    public String postsSave(Model model,@LoginUser SessionUser user) {
    	 if(user != null) {
             model.addAttribute("sessionUserName", user.getName());
         }
        return "posts/posts-save";
    }
    //@RequestParam 으로 검색기능 추가
    @GetMapping("/")//전체게시물 Read 접근 Api Url을 도메인 루트로 변경한다
    public String postList(@RequestParam(value="keyword", defaultValue="")String keyword, @PageableDefault(size=5,sort="id",direction=Sort.Direction.DESC) Pageable pageable, Model model,@LoginUser SessionUser user) {
    	if(user != null) {
            model.addAttribute("sessionUserName", user.getName());
            model.addAttribute("sessionRoleName", "ROLE_ADMIN".equals(user.getRole())?"admin":null);
            System.out.print("세션값이 있을 때 user.getName(): " + user.getName());
            //회원DB에 등록된 사용자인지 확인
            try {
               SimpleUsersDto usersDto = simpleUsersService.findByName(user.getName());
               //회원DB에 등록된 사용자만 memberTrue에 객체 값을 보낸다.
               model.addAttribute("memberTrue", usersDto);
            }catch (Exception e) {
               model.addAttribute("memberTrue", null);
            }
        }//자바의 3항 연산자: if 조건문을 축약한 구문으로 형식은 (조건문)? 조건이 참일 때 값 : 거짓일 때 값 이다.
    	Page<Posts> postsList = postsService.postsList(keyword,pageable);//검색기능 추가
	    model.addAttribute("postsList", postsList);//게시글목록 5개 이상 시 페이징 처리
	    model.addAttribute("currPage", postsList.getPageable().getPageNumber());//현재페이지번호
	    model.addAttribute("pageIndex", postsList.getTotalPages());//전체페이지개수
	    model.addAttribute("prevCheck", postsList.hasPrevious());//이전페이지 있는지 체크
	    model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());//이전페이지번호 사용
	    model.addAttribute("nextCheck", postsList.hasNext());//다음페이지 있는지 체크
	    model.addAttribute("next", pageable.next().getPageNumber());//다음페이지번호 사용
	    return "index";//출력할 페이지명 posts폴더/post-list.mustache파일(html디자인템플릿)
    } // mustache템플릿 html 뷰 파일은 다음시간에 만든다.
}
