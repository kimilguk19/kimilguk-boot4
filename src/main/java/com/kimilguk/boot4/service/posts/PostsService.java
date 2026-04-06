package com.kimilguk.boot4.service.posts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimilguk.boot4.domain.posts.Posts;
import com.kimilguk.boot4.domain.posts.PostsRepository;
import com.kimilguk.boot4.web.dto.PostsDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor//final  매개변수가 있는 생성자메소드가 자동 생성된다.
@Service//서비스 객체로 만든다
public class PostsService {
	private Logger logger = LoggerFactory.getLogger(getClass()); //로그 출력 객체생성
	private final PostsRepository postsRepository;//위 @애노테이션 때문에 객체로 사용가능하다.
	@Transactional//저장:Create
	public Long save(PostsDto requestDto) {
	    return postsRepository.save(requestDto.toEntity())
	    .getId();//DB에저장 후 추가된 Id값을 반환한다.
	}
	@Transactional//읽기:1개게시물 Read
	public PostsDto postsOne(Long id) {
	    Posts entity = postsRepository.findById(id).orElseThrow(()->new IllegalArgumentException(id + " 번째 게시글이 없습니다."));
	    return new PostsDto(entity);
	}
	@Transactional//읽기:전체게시물 Read
	public Page<Posts> postsList(Pageable pageable) {
	    Page<Posts> postsList = postsRepository.findAll(pageable);
	    return postsList;
	}
	@Transactional//읽기:전체게시물 Read+검색기능 추가
	public Page<Posts> postsList(String keyword,Pageable pageable) {
		return postsRepository.findByTitleContaining(keyword,pageable);
	}
	//수정:Update 는 엔티티의 값만 수정하면 레포지토리 함수없이 DB값이 자동으로 수정된다(Context 영속성)
	@Transactional
	public Long update(Long id, PostsDto requestDto) {
	    Posts posts = postsRepository.findById(id).orElseThrow(()->new IllegalArgumentException(id + " 번째 게시글이 없습니다."));//에러발생시 콘솔창에 에러 표시 후 메소드 종료됨. 자바8부터 람다=애로우(화살) 메소드 사용가능
	    posts.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getFileId()); //빌더형식이 아니기 때문에 위치를 잘 확인해야 한다.
	    return id;
	}

	@Transactional//삭제:Delete
	public void delete(Long id) {
	    Posts entity = postsRepository.findById(id).orElseThrow(()->new IllegalArgumentException(id + " 번째 게시글이 없습니다."));
	    postsRepository.delete(entity);
	}
}
