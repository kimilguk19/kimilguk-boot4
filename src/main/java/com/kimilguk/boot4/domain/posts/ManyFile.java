package com.kimilguk.boot4.domain.posts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter//엔티티 출력이 가능하게 get메소드가 자동생성된다
@NoArgsConstructor//엔티티 생성자를 자동으로 추가한다
@Entity//엔티티와 매핑되는 DB저장소를 만든다
public class ManyFile {
	@Id//주키 Primary Key 로 만든다
    @GeneratedValue(strategy = GenerationType.IDENTITY)//자동증가값으로 구현한다
    private Long id;//첨부파일고유ID
    @Column(nullable = false)//이 변수필드는 공백일 수 없다.
    private String origFilename;//한글첨부파일명
    @Column(nullable = false)//이 변수필드는 공백일 수 없다.
    private String filename;//실제저장된 비한글 파일명
    @Column(nullable = false)//이 변수필드는 공백일 수 없다.
    private String filePath;//실제저장된 파일경로
    @ManyToOne//1:N 관계 추가
	private Posts posts;//1:N 관계 @엔티티(테이블)명 지정
    @Builder//조립가능한 형식으로=build()메소드 사용가능하게 만든다.
    public ManyFile(Long id, String origFilename, String filename, String filePath, Posts posts) {
        this.id = id;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
        this.posts = posts;
    }
}
