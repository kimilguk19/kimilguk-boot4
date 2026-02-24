package com.kimilguk.boot4.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kimilguk.boot4.web.dto.HelloDto;

@RestController
public class HelloController {
	@GetMapping("/hello")
	public String hello() {
		return "Hello";
	}

	@GetMapping("/hello/dto") // API로 웹 요청 시 데이터를 전송 받는 기능을 @RequestParam 애노테이션으로 자동 구현한다.
	public HelloDto helloDto(@RequestParam("name") String name, @RequestParam("amount") int amount) {
		return new HelloDto(name, amount);// 롬복의 자동 생성자 사용
	}
}
