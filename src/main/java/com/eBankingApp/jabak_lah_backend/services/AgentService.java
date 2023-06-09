package com.eBankingApp.jabak_lah_backend.services;

import com.eBankingApp.jabak_lah_backend.config.JwtService;
import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.entity.Role;
import com.eBankingApp.jabak_lah_backend.model.ClientRequest;
import com.eBankingApp.jabak_lah_backend.model.RegisterAgentResponse;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;
import com.eBankingApp.jabak_lah_backend.token.Token;
import com.eBankingApp.jabak_lah_backend.token.TokenRepository;
import com.eBankingApp.jabak_lah_backend.token.TokenType;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AgentService {

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


    public RegisterAgentResponse registerClient(ClientRequest request) {

        if (repository.existsByPhoneNumber(request.getPhoneNumber()) && repository.existsByEmail(request.getEmail() ) ) {
            throw new RuntimeException("Email Or Phone already exists");
        }

        String generatedpassword = generatePassword();
        var Clinet = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(generatedpassword))
                .CIN(request.getCIN())
                .isFirstLogin(true)
                .role(Role.CLIENT)
                .isPaymentAccountActivated(false)
                .createdDate(LocalDate.now())
                .isPaymentAccountActivated(false)
                .birthDate(request.getBirthDate())
                .build();
        var savedAgent = repository.save(Clinet);
        var jwtToken = jwtService.generateToken(Clinet);
        //  var refreshToken = jwtService.generateRefreshToken(Agent);
        TextMessage message = new TextMessage(BRAND_NAME, request.getPhoneNumber(), "Your password is : " + generatedpassword);
        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
        saveUserToken(savedAgent, jwtToken);
        return RegisterAgentResponse.builder().message("success " + generatedpassword).build();
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


    public RegisterAgentResponse updateClient(Long id , ClientRequest clientRequest) {
        Client client=
                repository.findClientByClientId(id);
                     if(client!=null) {
                         client.setFirstName(clientRequest.getFirstName());
                         client.setLastName(clientRequest.getLastName());
                         client.setEmail(clientRequest.getEmail());
                         client.setAddress(clientRequest.getAddress());
                         client.setCIN(clientRequest.getCIN());
                         client.setPhoneNumber(clientRequest.getPhoneNumber());


                         repository.save(client);
                     }else {System.out.println("The Client with the given Id not exist in the database");}
        return RegisterAgentResponse.builder().message("Agent updated successfully").build();
    }


    public List<Client> findAll() {
        List<Client> clients = repository.findAllClientsWithRoleClient();
        return clients;
    }
    public Client findById(Long id) {
        Client client = repository.findClientByClientId(id);
        return  client;
    }

    public RegisterAgentResponse deleteClient(Long id) {
        Client client = repository.findClientByClientId(id);
        if(client !=null){
            repository.delete(client);
            return   RegisterAgentResponse.builder().message("Deleted with Success").build();
        }else {

              return RegisterAgentResponse.builder().message("Error during Deleting").build();
        }
    }
}
