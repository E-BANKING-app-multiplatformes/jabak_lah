package com.eBankingApp.jabak_lah_backend.services;

import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.ClientProfileResponse;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceV2Impl implements  ClientServiceV2{
    @Autowired
    private ClientRepository clientRepository;


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
}
