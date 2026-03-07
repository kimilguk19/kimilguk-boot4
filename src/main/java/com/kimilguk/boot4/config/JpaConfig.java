package com.kimilguk.boot4.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing//JPA공통DB필드(변수)사용
//대표적으로 생성일자, 수정일자와 같은 도메인마다 공통으로 존재하는 중복 코드를 자동으로 등록하는 기능을 사용한다.
@Configuration//이 클래스에서 설정한 내용이 자동으로 빈으로 등록된다.
public class JpaConfig {
}
