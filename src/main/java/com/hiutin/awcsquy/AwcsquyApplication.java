package com.hiutin.awcsquy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing 
public class AwcsquyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwcsquyApplication.class, args);
	}

}
