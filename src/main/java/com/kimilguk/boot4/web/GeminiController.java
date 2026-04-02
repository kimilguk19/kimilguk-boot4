package com.kimilguk.boot4.web;

import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    private final WebClient webClient = WebClient.create("https://generativelanguage.googleapis.com");
    @Value("${API_KEY}")
    private String apiKey;
    //Mono<String>은 Spring WebFlux에서 제공하는 타입으로, 문자열 데이터를 비동기적으로 처리할 때 사용한다.
    @PostMapping("/chat")
    public Mono<String> chat(@RequestBody String input) {
    	//System.out.println("API 키: " + apiKey); // API 키가 제대로 로드되었는지 확인
    	Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                    "parts", List.of(Map.of("text", input))
                ))
            );
    	Mono<String> result = null;
		result = webClient.post()
				.uri("/v1beta/models/gemini-2.5-flash:generateContent") // API 키를 URL에 포함시키는 경우
				.header("x-goog-api-key", apiKey) // API 키를 헤더에 포함시키는 경우)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.retrieve()
				.bodyToMono(String.class);
    	return result;
    }
}

