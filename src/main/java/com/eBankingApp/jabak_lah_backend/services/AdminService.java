package com.eBankingApp.jabak_lah_backend.services;


import com.eBankingApp.jabak_lah_backend.auth.AuthenticationResponse;
import com.eBankingApp.jabak_lah_backend.config.JwtService;
import com.eBankingApp.jabak_lah_backend.entity.Client;

import com.eBankingApp.jabak_lah_backend.entity.Role;
import com.eBankingApp.jabak_lah_backend.model.AgentRequest;
import com.eBankingApp.jabak_lah_backend.model.RegisterAgentResponse;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;
import com.eBankingApp.jabak_lah_backend.token.Token;
import com.eBankingApp.jabak_lah_backend.token.TokenRepository;
import com.eBankingApp.jabak_lah_backend.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular app's origin
@Service
@RequiredArgsConstructor
public class AdminService {
    private final ClientRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String BRAND_NAME = "NXSMS";
    @Autowired
    private VonageClient vonageClient;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String generatePassword() {
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }


    private void saveAgentToken(Client user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    public RegisterAgentResponse registerAgent(AgentRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");

        }
        String generatedPassword =generatePassword();
        var Agent = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .CommercialRn(request.getCommercialRn())
                .patentNumber(request.getPatentNumber())
                .password(passwordEncoder.encode(generatedPassword))
                .role(Role.AGENT)
                .build();
        var savedAgent = repository.save(Agent);
        var jwtToken = jwtService.generateToken(Agent);
      //  var refreshToken = jwtService.generateRefreshToken(Agent);
        saveUserToken(savedAgent, jwtToken);
        TextMessage message = new TextMessage(BRAND_NAME, request.getPhoneNumber(), "Your password is : " + generatedPassword);
        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
        return RegisterAgentResponse.builder().message("success"+generatedPassword).build();
    }


    private void saveUserToken(Client user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Client user) {
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
            Client user = (Client) this.repository.findByEmail(userEmail)
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

    public List<Client> findAll() {
        List<Client> users = repository.findAllAgentWithRoleClient();
        return users;
    }

    public Client findById(Long id) {
        Client agent = repository.findAgentByClientId(id);
        return  agent;
    }






  public RegisterAgentResponse updateAgent(Long id , AgentRequest userUpdateRequest) {
        Client agent=
               repository.findAgentByClientId(id);
                       if(agent!=null) {

                     agent.setFirstName(userUpdateRequest.getFirstName());
                     agent.setLastName(userUpdateRequest.getLastName());
                     agent.setEmail(userUpdateRequest.getEmail());
                     agent.setAddress(userUpdateRequest.getAddress());
                     agent.setPhoneNumber(userUpdateRequest.getPhoneNumber());
                     agent.setCommercialRn(userUpdateRequest.getCommercialRn());
                     agent.setPatentNumber(userUpdateRequest.getPatentNumber());

                           repository.save(agent);
                       }else {
                           System.out.println("The Agent with the Id Given not exist in the database ");
                       }

            return RegisterAgentResponse.builder().message("Agent updated successfully").build();
        }

    public RegisterAgentResponse delete(Long id) {
        Client agent = repository.findAgentByClientId(id);
        if(agent !=null){
            repository.delete(agent);
            return RegisterAgentResponse.builder().message("Deleted with Success").build();
        }else {
            return RegisterAgentResponse.builder().message("Error during Deleting").build();

        }}

}

