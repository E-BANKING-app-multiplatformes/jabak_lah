package com.eBankingApp.jabak_lah_backend.services;

import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.ClientProfileResponse;
import com.eBankingApp.jabak_lah_backend.model.ClientRequest;
import com.eBankingApp.jabak_lah_backend.model.RegisterAgentResponse;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceV2Impl implements  ClientServiceV2{
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;


    @Override
    public long getAccountId(long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));
      return client.getPaymentAccount().getPaymentAccountId();
    }

    @Override
    public ClientProfileResponse getAccount(long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));
        return ClientProfileResponse.builder().firstName(client.getFirstName()).lastName(client.getLastName()).CIN(client.getCIN())
                .phoneNumber(client.getPhoneNumber()).email(client.getEmail()).address(client.getAddress()).build();  }

    public RegisterAgentResponse changePassword(ClientRequest request) {
      //  String phoneNumber = request.getPhoneNumber();
        String newPassword = request.getNewPassword();

        Client client = clientRepository.findByPhoneNumber(request.getPhoneNumber());

        if (!(client == null) ) {

             client.setPassword(passwordEncoder.encode(request.getNewPassword()));
            client.setIsFirstLogin(false);
                clientRepository.save(client);
                return RegisterAgentResponse.builder().message("Password updated successfully").build();
            } else {
            return RegisterAgentResponse.builder().message("Client not found").build();
            }
        }



    }








