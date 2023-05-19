package com.zaidMEx.security;

import com.zaidMEx.security.auth.AuthenticationService;
import com.zaidMEx.security.auth.RegisterRequest;
import com.zaidMEx.security.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static com.zaidMEx.security.user.Role.ADMIN;
import static com.zaidMEx.security.user.Role.AGENT;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			var admin = RegisterRequest.builder()
			.firstname("zaid")
            .lastname("Gabri")
            .email("zaid@gmail.com")
            .confirm_email("zaid@gmail.com")
            .IdentityType("CIN")
            .Num_Identity("0987567")
            .Birthday("23-34-3333")
            .Address("marrackech")
            .RCS("0-0987cvbnmmnb")
            .Num_de_patente(Integer.valueOf("0777"))
            .password("password")
            .role(Role.valueOf("ADMIN"))
            .tele("08765098766")
            .build();
			System.out.println("Admin token: " + service.register(admin).getAccessToken());};}
/*
			var manager = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("manager@mail.com")
					.password("password")
					.role(AGENT)
					.build();
			System.out.println("Manager token: " + service.register(manager).getAccessToken());

		};*/

//@Bean
//public JavaMailSender javaMailSender() {
//	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//	mailSender.setHost("smtp.gmail.com");
//	mailSender.setPort(587);
//	mailSender.setUsername("zaidgabri976@gmail.com");
//	mailSender.setPassword("password");
//	Properties props = mailSender.getJavaMailProperties();
//	props.put("mail.transport.protocol", "smtp");
//	props.put("mail.smtp.auth", "true");
//	props.put("mail.smtp.starttls.enable", "true");
//	props.put("mail.debug", "true");
//	return mailSender;
//}
}
