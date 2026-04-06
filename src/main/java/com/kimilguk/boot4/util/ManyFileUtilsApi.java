package com.kimilguk.boot4.util;

import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kimilguk.boot4.domain.posts.Posts;
import com.kimilguk.boot4.domain.posts.PostsRepository;
import com.kimilguk.boot4.service.posts.FileService;
import com.kimilguk.boot4.service.posts.ManyFileService;
import com.kimilguk.boot4.web.dto.FileDto;
import com.kimilguk.boot4.web.dto.ManyFileDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor//final 매개변수가 있는 생성자 메소드가 자동 생성된다.
@RestController//반환값으로 json텍스트 내용부분만(body) 전송된다.
public class ManyFileUtilsApi {
	private final ManyFileService fileService;//DB CRUD 메소드 포함
	private final PostsRepository postsRepository;//DB CRUD 메소드 포함
	
    @PostMapping("/api/many_file_upload")
    public Long uploadFile(@RequestParam("many_file") MultipartFile uploadFile,@RequestParam("post_id") Long post_id) {
        Long fileId = null;
        String origFilename = uploadFile.getOriginalFilename();//전송파일명을 가져온다.
        UUID uid = UUID.randomUUID();//유니크한 ID값 자동생성해서 실제저장할 파일명으로 사용(이유는 한글파일명으로 저장할 수 없기 때문에)
        String filename = uid.toString()+"."+StringUtils.getFilenameExtension(origFilename);//실제 파일저장명과 확장자를 가져온다.
        String uploadPath = "/tmp";//C하드디스크에 tmp 폴더 생성 필수. 단, 클라우드 에서는 자동 생성된다.
        String filePath = Paths.get(uploadPath, filename).toString();
        //입출력 스트림 객체를 사용하여 전송파일을 저장한다.(아래)
        try {
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));//주, File 클래스는 java.io 패키지의 클래스이다.
                stream.write(uploadFile.getBytes());//바이트 기반으로 실제 디스크에 파일이 저장된다.
                stream.close();//stream 객체를 메모리에서 제거한다.
                Posts posts = postsRepository.findById(post_id).orElseThrow(() -> new
                        IllegalArgumentException("헤당 게시글이 없습니다."));
                //엔티티에 접근하려면 서비스가 아니고, 레포지토리에서 작업 해야한다. 보통 데이터의 흐름은 html머스테치-컨트롤러-서비스-Jpa레포지토리-엔티티-DB 순서이다.
                ManyFileDto fileDto = new ManyFileDto();//DB에 파일정보를 저장할 객체를 선언한다.
                fileDto.setOrigFilename(origFilename);
                fileDto.setFilename(filename);
                fileDto.setFilePath(filePath);
                fileDto.setPosts(posts);//DB many_file 테이블의 posts_id 필드에 저장된다.
                fileId = fileService.saveFile(fileDto);//DB에 파일정보를 저장된다.
            } catch (IOException e) {
                // Stream 에서 에러발생 시 중지없이 프로그램을 진행 시킨다.
                e.printStackTrace();
            }
            return fileId;
     }
    @DeleteMapping("/api/many_file_delete/{fileId}")
    public ResponseEntity<Map<String,Object>> file_delete(@PathVariable("fileId") Long fileId) {//삭제 후 반환 값으로 json 형태로 데이터를 반환할 때 ResponseEntity 객체에 담아서 반환한다.
        Map<String,Object> jsonResult = new HashMap<>();//json형태<키(Key):값(Value)>의 객체 생성
        ResponseEntity<Map<String,Object>> result = null;//json 반환값 초기화
        ManyFileDto fileDto = fileService.getFile(fileId);
        Path filePath = Paths.get(fileDto.getFilePath());//파일경로를 가져온다.
        File target = new File(filePath.toString());//해당경로의 파일객체를 가져온다.
        if(target.exists()) {
            target.delete();//파일객체가 존재한다면, 실제파일을 삭제한다.
            fileService.deleteFile(fileId);//DB에서 파일정보를 삭제한다.
            jsonResult.put("success", "OK");//json 형태에 OK값을 저장한다.
            result = new ResponseEntity<Map<String,Object>>(jsonResult, HttpStatus.OK);
        }
        return result;
    } //ResponseEntity객체에 jsonResult, HttpStatus상태정보 값을 json형태로 mustache 파일에 응답.(위)
    @GetMapping("api/many_file_download/{fileId}")
    public ResponseEntity<Resource> filedownload(@PathVariable("fileId") Long fileId) throws IOException {
        //Resource 는 바이트기반의 형태로 데이터를 반환할 때 ResponseEntity 객체에 담아서 반환한다.
    	ManyFileDto fileDto = fileService.getFile(fileId);//DB에서 파일정보를 가져온다.
        Path filePath = Paths.get(fileDto.getFilePath());//파일경로를 가져온다.
        Resource resource = new InputStreamResource(Files.newInputStream(filePath));//Resource는 바이트기반이기 때문에 스트림객체로 가져온다.
        String encOrigFilename = URLEncoder.encode(fileDto.getOrigFilename());//ie 브라우저에서 한글파일명이 깨질때 방지하는 코드(크로스브라우징 체크) 
        return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\""+encOrigFilename+"\"")
        .body(resource);//\" 큰따옴표안에 큰따옴표를 사용할 때 역슬래쉬가 사용된다.
    }
}
