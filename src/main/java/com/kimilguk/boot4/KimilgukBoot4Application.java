package com.kimilguk.boot4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing//JPA공통DB필드(변수)
@SpringBootApplication
public class KimilgukBoot4Application {

	public static void main(String[] args) {
		SpringApplication.run(KimilgukBoot4Application.class, args);
	}

}
