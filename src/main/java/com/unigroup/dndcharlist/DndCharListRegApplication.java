package com.unigroup.dndcharlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DndCharListRegApplication {

	public static void main(String[] args) {
		BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
		String pwd = bcryptPasswordEncoder.encode("password");
		System.out.println(pwd);
		SpringApplication.run(DndCharListRegApplication.class, args);
	}

}
