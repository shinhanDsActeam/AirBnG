package com.airbng.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 기존에는 스프링 부트 메인 클레스에 작성
 * 하지만 메인에 작성시 스프링 컨테이너가 필요한 테스트를 진행할때
 * @EnableJapAuditing가 붙어있는 JPA관련 빈을 불러와야 하는데 mock 테스트를 진행시 해당 빈이 없어 오류 발생
 * 따라서 별도의 클래스로 JPA관련 config클래스로 관리
 */
@EnableJpaAuditing
@Configuration
public class JpaAuditingConfig {
}
