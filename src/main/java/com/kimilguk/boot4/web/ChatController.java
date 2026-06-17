package com.kimilguk.boot4.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kimilguk.boot4.service.RAGService;

@RestController
public class ChatController {
	private final ChatClient chatClient;// ChatClient.Builder는 자동으로 autowired(주입) 됨
	/* RAG 기능때문에 ChatClient.Builder를 주입받는 생성자 주석처리
	public ChatController(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}
	*/
	private final RAGService ragService; // RAGService 주입
	// 생성자 주입 방식으로 RAGService와 ChatClient를 주입받음
	public ChatController(RAGService ragService,ChatClient.Builder builder) {
        this.ragService = ragService; // RAG 기능때문에 추가
        this.chatClient = builder.build(); // 기존 수업 소스
    }
	// RAG 기능을 위한 API 엔드포인트 추가
	@PostMapping("/api/gemini/rag_chat")
    public String ragChat(@RequestBody String message) {
        return ragService.answer(message, 3);// 검색 시 유사도Top3에 따라 조정
    }
	@PostMapping("/api/gemini/chat") //보안에 유리한 @PostMapping, @RequestBody 로 변경.
	public String generate(@RequestBody String message) {
		String response = "";
		System.out.println("Received message: " + message); // 디버깅용 로그
		try {
			response = chatClient.prompt().user(message).call().content(); // 응답 문자열 반환
		} catch (Exception e) { // e.getMessage()최 상위 간단한 원인
			e.printStackTrace();
			Throwable cause = e.getCause();
			if (cause != null) {
				response = cause.getMessage(); // 상세원인 메시지 출력
			}
		}
		return response;
	}
}
