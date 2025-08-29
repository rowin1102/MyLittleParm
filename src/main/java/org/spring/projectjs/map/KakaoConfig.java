package org.spring.projectjs.map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class KakaoConfig {
    @Bean
    KakaoLocalDao kakaoLocalDao(@Value("${kakao.rest.key}") String restKey){
        return new KakaoLocalDao(restKey);
    }
}
