package com.kimilguk.boot4.web.dto;

import com.kimilguk.boot4.domain.posts.File;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor//매개변수가 없는 생성자 메소드를 자동 생성한다
@Getter @Setter//Get, Set 메소드를 자동으로 생성한다.
public class FileDto {
	private Long id;//첨부파일고유ID
    private String origFilename;//한글첨부파일명
    private String filename;//실제저장된 비한글 파일명
    private String filePath;//실제저장된 파일경로
    @Builder//조립가능한 형식으로=build()메소드 사용가능하게 만든다.
    public FileDto(Long id, String origFilename, String filename, String filePath) {
        this.id = id;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
    }
    public File toEntity() {
        return File.builder()
        .id(id)
        .origFilename(origFilename)
        .filename(filename)
        .filePath(filePath)
        .build();
    }
}
