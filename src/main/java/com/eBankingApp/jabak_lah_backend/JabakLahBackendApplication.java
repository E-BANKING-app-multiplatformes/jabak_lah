package com.eBankingApp.jabak_lah_backend;

import com.eBankingApp.jabak_lah_backend.auth.AuthenticationService;
import com.eBankingApp.jabak_lah_backend.model.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.eBankingApp.jabak_lah_backend.entity.Role.ADMIN;

@SpringBootApplication
public class JabakLahBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JabakLahBackendApplication.class, args);
	}




//	@Bean
//	public CommandLineRunner commandLineRunner(
//			AuthenticationService service
//	) {
//		return args -> {
//			var admin = RegisterRequest.builder()
//					.firstname("Admin")
//					.lastname("Admin")
//					.email("admin@mail.com")
//					.password("password")
//
//					.role(ADMIN)
//
//					.build();
//			System.out.println("Admin token: " + service.register(admin).getAccessToken());};}
//@Bean
//public CommandLineRunner commandLineRunner(AuthenticationService authenticationService) {
//	return args -> {
//		var authenticationRequest = AuthenticationRequest.builder()
//				.email("admin@mail.com")
//				.password("password")
//				.build();
//
//		var authenticationResponse = authenticationService.authenticate(authenticationRequest);
//
//		System.out.println("Access Token: " + authenticationResponse.getAccessToken());
//		System.out.println("Refresh Token: " + authenticationResponse.getRefreshToken());
//	};
//}

}
