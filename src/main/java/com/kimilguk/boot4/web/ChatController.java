package com.kimilguk.boot4.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
	private final ChatClient chatClient;// ChatClient.Builder는 자동으로 autowired(주입) 됨
	public ChatController(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}
	@GetMapping("/api/gemini/chat") // 다음 슬라이드에서 테스트 후 @PostMapping 으로 변경 할 예정.
	public String generate(@RequestParam(value = "message", defaultValue = "안녕, 너는 누구니?") String message) {
		String response = "";
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
