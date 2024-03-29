package com.example.stay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(value = {"com.example.stay.*.*.mapper", "com.example.stay.*.mapper"})
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class PlndStayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlndStayApplication.class, args);
	}

}
