package com.clinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class ClinicExpertsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicExpertsApplication.class, args);
	}

}
