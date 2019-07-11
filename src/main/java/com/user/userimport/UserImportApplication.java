package com.user.userimport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.user.userimport.dao")
public class UserImportApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserImportApplication.class, args);
    }

}
