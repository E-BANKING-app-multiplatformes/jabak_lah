package com.eBankingApp.jabak_lah_backend.services;

import com.eBankingApp.jabak_lah_backend.config.JwtService;
import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.entity.Role;
import com.eBankingApp.jabak_lah_backend.model.AgentRequest;
import com.eBankingApp.jabak_lah_backend.model.ClientRequest;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;
import com.eBankingApp.jabak_lah_backend.token.Token;
import com.eBankingApp.jabak_lah_backend.token.TokenRepository;
import com.eBankingApp.jabak_lah_backend.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public String registerAgent(ClientRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        var Clinet = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .build();
        var savedAgent = repository.save(Clinet);
        var jwtToken = jwtService.generateToken(Clinet);
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


    public ResponseEntity<String> updateClient(Long id , ClientRequest clientRequest) {
        Client client=
                repository.findClientByClientId(id);
                     if(client!=null) {
                         client.setFirstName(clientRequest.getFirstName());
                         client.setLastName(clientRequest.getLastName());
                         client.setEmail(clientRequest.getEmail());
                         client.setAddress(clientRequest.getAddress());
                         client.setCIN(clientRequest.getCIN());
                         client.setPhoneNumber(clientRequest.getPhoneNumber());
                         client.setPassword(passwordEncoder.encode(clientRequest.getPassword()));

                         repository.save(client);
                     }else {System.out.println("The Client with the given Id not exist in the database");}
        return ResponseEntity.ok("Client has been updated successfully");
    }


    public List<Client> findAll() {
        List<Client> clients = repository.findAllClientsWithRoleClient();
        return clients;
    }
    public Client findById(Long id) {
        Client client = repository.findClientByClientId(id);
        return  client;
    }

    public Boolean deleteClient(Long id) {
        Client client = repository.findClientByClientId(id);
        if(client !=null){
            repository.delete(client);
            return true;
        }else {
            System.out.println("No clinet with a id given exist in the database ");
            return false;
        }
    }
}
