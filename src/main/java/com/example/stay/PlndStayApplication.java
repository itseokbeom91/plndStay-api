package com.example.stay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.stay.*.*.mapper")
@SpringBootApplication
public class PlndStayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlndStayApplication.class, args);
	}

}
