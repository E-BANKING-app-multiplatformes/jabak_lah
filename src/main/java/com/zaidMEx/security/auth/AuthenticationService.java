package com.zaidMEx.security.auth;

import com.zaidMEx.security.config.JwtService;
import com.zaidMEx.security.token.Token;
import com.zaidMEx.security.token.TokenRepository;
import com.zaidMEx.security.token.TokenType;
import com.zaidMEx.security.user.Role;
import com.zaidMEx.security.user.User;
import com.zaidMEx.security.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    if (!request.getEmail().equals(request.getConfirm_email())) {
      throw new IllegalArgumentException("Email and confirm email must match.");
    }

    var user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .confirm_email(request.getConfirm_email())
            .IdentityType(request.getIdentityType())
            .Num_Identity(request.getNum_Identity())
            .Birthday(request.getBirthday())
            .Address(request.getAddress())
            .RCS(request.getRCS())
            .Num_de_patente(request.getNum_de_patente())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .tele(request.getTele())
            .build();

    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }
/*
@Autowired
private JavaMailSender mailSender;

  public AuthenticationResponse register(RegisterRequest request) {
    if (!request.getEmail().equals(request.getConfirm_email())) {
      throw new IllegalArgumentException("Email and confirm email must match.");
    }

    // Generate a random password for the user
    String password = RandomStringUtils.randomAlphanumeric(10);

    // Hash the password using SHA-256 for storage in the database
    String hashedPassword = DigestUtils.sha256Hex(password);

    var user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .confirm_email(request.getConfirm_email())
            .IdentityType(request.getIdentityType())
            .Num_Identity(request.getNum_Identity())
            .Birthday(request.getBirthday())
            .Address(request.getAddress())
            .RCS(request.getRCS())
            .Num_de_patente(request.getNum_de_patente())
            .role(request.getRole())
            .tele(request.getTele())
            .password(hashedPassword) // store hashed password in database
            .build();

    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);

    // Send the user an email with their generated password
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
      helper.setTo(user.getEmail());
      helper.setSubject("Welcome to My App!");
      helper.setText("Hello " + user.getFirstname() + ",\n\n" +
              "Your password has been generated: " + password + "\n\n" +
              "Please log in to My App using your email and this password. " +
              "We recommend that you change your password after logging in for security reasons.\n\n" +
              "Thank you,\n" +
              "My App Team");
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send email", e);
    }

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }
*/

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
