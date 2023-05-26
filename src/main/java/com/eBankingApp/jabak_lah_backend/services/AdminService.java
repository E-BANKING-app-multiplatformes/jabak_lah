package com.eBankingApp.jabak_lah_backend.services;


import com.eBankingApp.jabak_lah_backend.auth.AuthenticationResponse;
import com.eBankingApp.jabak_lah_backend.config.JwtService;
import com.eBankingApp.jabak_lah_backend.entity.Client;

import com.eBankingApp.jabak_lah_backend.entity.Role;
import com.eBankingApp.jabak_lah_backend.model.AgentRequest;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;
import com.eBankingApp.jabak_lah_backend.token.Token;
import com.eBankingApp.jabak_lah_backend.token.TokenRepository;
import com.eBankingApp.jabak_lah_backend.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ClientRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


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
    public String registerAgent(AgentRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        var Agent = Client.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.AGENT)
                .build();
        var savedAgent = repository.save(Agent);
        var jwtToken = jwtService.generateToken(Agent);
      //  var refreshToken = jwtService.generateRefreshToken(Agent);
        saveUserToken(savedAgent, jwtToken);
        return "Success";
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
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getClientId());
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






  public ResponseEntity<String> updateAgent(Long id ,AgentRequest userUpdateRequest) {
        Client agent=
               repository.findAgentByClientId(id);
                       if(agent!=null) {

                           agent.setFirstName(userUpdateRequest.getFirstname());
                           agent.setLastName(userUpdateRequest.getLastname());
                           agent.setEmail(userUpdateRequest.getEmail());
                           agent.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));

                           repository.save(agent);
                       }else {
                           System.out.println("The Agent with the Id Given not exist in the database ");
                       }
            return ResponseEntity.ok("User updated successfully");
        }

    public void delete(Long id) {
        Client agent = repository.findAgentByClientId(id);
        if(agent !=null){
            repository.delete(agent);
        }else {
            System.out.println("No Agent with the given Id exist in the database ");
        }

        }

}

