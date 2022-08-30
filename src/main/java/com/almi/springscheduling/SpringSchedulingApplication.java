package com.almi.springscheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class SpringSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSchedulingApplication.class, args);
	}

}
