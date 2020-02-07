package com.demo.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.demo.community.mapper")
public class ComunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComunityApplication.class, args);
    }

}
