package com.kimilguk.boot4.web.dto;

import java.time.LocalDateTime;

import com.kimilguk.boot4.domain.posts.Posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor // 생성자자 메소드를 자동을 생성한다
@Getter
@Setter // 롬복(Lombom)모듈에서 Get, Set 메소드를 자동으로 생성한다.
public class PostsDto {
	// 멤버변수는 Posts 와 일치한다.
	private long id;// 게시글 번호
	private String title;// 글 제목
	private String content;// 글 내용
	private String author;// 글 작성자 아이디
	private Long fileId;// 첨부파일 번호
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;
	// @컨트롤러 클래스에서 저장 시 사용, DB엔티티 값을 Dto객체에 담아서 조회(자동생성코드사용)
	public PostsDto(Posts entity) {
		this.id = entity.getId();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		this.author = entity.getAuthor();
		this.fileId = entity.getFileId();
		this.createDate = entity.getCreateDate();
		this.modifiedDate = entity.getModifiedDate();
	}
	//Source -> Generate Constructor using Fields창에서 super()부분 Omit생략체크 후 자동생성코드
	@Builder//조립 가능한 빌더형식으로 메소드를 @컨트롤러 클래스에서 사용하려고 만든다.
	public PostsDto(long id, String title, String content, String author, Long fileId) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.author = author;
		this.fileId = fileId;
	}
	//빌더형식으로 Posts엔티티의 DB저장에 사용
	public Posts toEntity() {
		return Posts.builder()
				//.id(id) //id는 자동증가값이므로 빌더에서 id값은 생략.
				.title(title)
				.content(content)
				.author(author)
				.fileId(fileId)
				.build();
	}
	// Source -> Generate toString()창에서 테스트 출력용 자동생성코드사용(아래)
	@Override
	public String toString() {
		return "PostsDto [id=" + id + ", title=" + title + ", content=" + content + ", author=" + author + ", fileId="
				+ fileId + ", createDate=" + createDate + ", modifiedDate=" + modifiedDate + "]";
	}
}
