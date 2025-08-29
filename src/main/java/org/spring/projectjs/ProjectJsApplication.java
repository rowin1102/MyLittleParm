package org.spring.projectjs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.spring.projectjs.jdbc")
@MapperScan("org.spring.projectjs.chatting.mapper")
public class ProjectJsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectJsApplication.class, args);
    }

}
