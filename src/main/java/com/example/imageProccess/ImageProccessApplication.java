package com.example.imageProccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class ImageProccessApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageProccessApplication.class, args);
	}

}
