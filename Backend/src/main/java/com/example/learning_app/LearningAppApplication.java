package com.example.learning_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LearningAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningAppApplication.class, args);
	}

}
