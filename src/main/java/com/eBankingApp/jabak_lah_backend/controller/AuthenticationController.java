package com.eBankingApp.jabak_lah_backend.controller;

import com.eBankingApp.jabak_lah_backend.auth.AuthenticationResponse;
import com.eBankingApp.jabak_lah_backend.auth.AuthenticationService;
import com.eBankingApp.jabak_lah_backend.auth.AuthenticationRequest;
import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.RegisterRequest;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;


@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular app's origin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final ClientRepository repository;

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }





//  //form Here
//  @Autowired
//  private VonageClient vonageClient;
//  @Value("${application.security.jwt.secret-key}")
//  private String secretKey;
//  private final String BRAND_NAME = "NXSMS";
//
//  private static final long RESET_PASSWORD_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours
//  @PostMapping("/forgot-password")
//  public ResponseEntity<?> forgotPassword(@RequestParam("phoneNumber") String phoneNumber , @RequestParam("newPassword") String newPassword) {
//    Client user = repository.findByPhoneNumber(phoneNumber);
//    if (user == null) {
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
//    }
//
//    String verificationCode = generateVerificationCode();
//    // boolean sent = vonageClient.sendVerificationCode(phoneNumber, verificationCode);
//    TextMessage message = new TextMessage(BRAND_NAME, phoneNumber, "Your password is : " + verificationCode);
//    SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
//    // Store the verification code and current timestamp in the user's record in the database
//    user.setVerificationCode(verificationCode);
//    user.setVerificationCodeCreatedAt(String.valueOf(new Date()));
//    repository.save(user);
//
//    return ResponseEntity.ok("Verification code sent successfully");
//
//  }
////  @RequestParam("token") String token,
//  @PostMapping("/reset-password")
//  public ResponseEntity<?> resetPassword( @RequestParam("newPassword") String newPassword) {
//    // Validate the token and extract the user ID
////    String verificationCode = validateResetPasswordToken(token);
//    Client user
//            = repository.
////    if (userId == null) {
////      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
////    }//
////    // Find the user by ID
////    Client user = repository.findById(userId);
////    if (user == null) {
////      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
////    }
//
//    // Update the user's password and clear the verification code
//    user.setPassword(newPassword);
//    user.setVerificationCode(null);
//    user.setVerificationCodeCreatedAt(null);
//    repository.save(user);
//
//    return ResponseEntity.ok("Password reset successfully");
//  }
////  private String generateResetPasswordToken(String userId) {
////    // Generate a JWT token containing the user ID
////    return Jwts.builder()
////            .setSubject(userId)
////            .setIssuedAt(new Date())
////            .setExpiration(new Date(System.currentTimeMillis() + RESET_PASSWORD_TOKEN_EXPIRATION_TIME))
////            .signWith(SignatureAlgorithm.HS512, secretKey)
////            .compact();
////  }
////
////  private String validateResetPasswordToken(String token) {
////    try {
////      // Validate the JWT token and extract the user ID from it
////      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
////    } catch (Exception e) {
////      e.printStackTrace();
////      return null;
////    }
////  }
//
//  private String generateVerificationCode() {
//    // Generate a random verification code (e.g., using a random number generator)
//    // You can customize this code according to your needs
//    return "123456"; // Replace with your code generation logic
//  }
//
//
////To here



}
