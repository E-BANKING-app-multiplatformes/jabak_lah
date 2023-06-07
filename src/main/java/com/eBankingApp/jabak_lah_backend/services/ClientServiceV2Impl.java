package com.eBankingApp.jabak_lah_backend.services;

import com.eBankingApp.jabak_lah_backend.entity.Client;
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
}
