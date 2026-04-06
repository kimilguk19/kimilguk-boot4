package com.kimilguk.boot4.service.posts;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimilguk.boot4.domain.posts.File;
import com.kimilguk.boot4.domain.posts.FileRepository;
import com.kimilguk.boot4.domain.posts.ManyFile;
import com.kimilguk.boot4.domain.posts.ManyFileRepository;
import com.kimilguk.boot4.web.dto.FileDto;
import com.kimilguk.boot4.web.dto.ManyFileDto;

@Service//서비스 객체로 만든다
public class ManyFileService {
    private ManyFileRepository fileRepository;//PostsService 롬복의 @RequiredArgsConstructor사용과 비교해 보세요.
    public ManyFileService(ManyFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }
    @Transactional
    public void deleteFile(Long id) {//파일테이블의 고유키값인 id를 매개변수로 받는다.
        fileRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 파일이 없습니다."));
        fileRepository.deleteById(id);//삭제는 반환값이 없음=void
    }
    @Transactional
    public Long saveFile(ManyFileDto fileDto) {
        return fileRepository.save(fileDto.toEntity()).getId();//저장 후 id고유값을 반환
    }
    @Transactional
    public ManyFileDto getFile(Long id) {
    	ManyFile file = fileRepository.findById(id).get();//DB의 파일 테이블에서 가져온 객체를 get()메소드로 반환한다.
        return ManyFileDto.builder()
        .id(id)
        .origFilename(file.getOrigFilename())
        .filename(file.getFilename())
        .filePath(file.getFilePath())
        .posts(file.getPosts())//다중파일의 id가 저장된다.
        .build();
    }
    //파일리스트반환
    @Transactional
    public List<ManyFile> getManyFile(Long post_id) {
        return fileRepository.fileAllDesc(post_id);
    }
}