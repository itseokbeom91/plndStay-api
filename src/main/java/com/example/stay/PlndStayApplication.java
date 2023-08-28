package com.example.stay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.example.stay.*.*.mapper")
@EnableScheduling
@SpringBootApplication
public class PlndStayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlndStayApplication.class, args);
	}

}
