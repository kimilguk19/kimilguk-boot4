package com.kimilguk.boot4.domain.posts;
import com.kimilguk.boot4.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor//매개변수가 없는 기본생성자를 자동으로 추가한다. public Posts(){} 와 같은 효과이다. JPA에서 엔티티 클래스를 사용할 때는 기본생성자가 반드시 필요하다. JPA 구현체가 리플렉션을 사용하여 엔티티 인스턴스를 생성할 때, 매개변수가 없는 생성자를 호출하기 때문이다. 따라서 @NoArgsConstructor를 사용하여 기본생성자를 자동으로 생성된다.
@Getter//엔티티 출력이 가능하게 구현된다 @NoArgsConstructor//엔티티 생성자를 자동으로 추가한다
@Entity//엔티티와 매핑되는 저장소를 만든다
public class Posts extends BaseTimeEntity {
	@Id//주키 Primary Key 로 만든다
	@GeneratedValue(strategy=GenerationType.IDENTITY)//자동증가값으로 구현한다 
	private long id;
	@Column(length=500, nullable=false)//기본길이 255글자
	private String title;//글 제목
	@Column(columnDefinition="TEXT", nullable=false)
	    private String content;//글 내용
	    private String author;//글 작성자 아이디
	    private Long fileId;//첨부파일 번호
	    @Builder//조립가능한 형식으로 메소드를 사용 가능하게 만든다.
	public Posts(String title, String content, String author, Long fileId) {
	   //super();//Source -> Generate Constructor using Fields 창 에서 super() 부분 Omit 생략체크 후 진행하면 된다.
	   this.title = title;
	   this.content = content;
	   this.author = author;
	   this.fileId = fileId;
	}
	public void update(String title, String content, Long fileId) {
	   this.title = title;
	   this.content = content;
	   this.fileId = fileId;
	}
	@Override
	public String toString() {
		return "Posts [id=" + id + ", title=" + title + ", content=" + content + ", author=" + author + ", fileId="
				+ fileId + "]";
	} //테스트용 출력 오버라이드 메소드 추가(아래) 이클립스 Source -> Generate toString()… 사용
	
}
