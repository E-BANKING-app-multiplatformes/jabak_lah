package com.eBankingApp.jabak_lah_backend.services;

import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.ClientProfileResponse;
import com.eBankingApp.jabak_lah_backend.model.ClientRequest;
import com.eBankingApp.jabak_lah_backend.model.RegisterAgentResponse;

public interface ClientServiceV2 {

    long getAccountId(long id);

    ClientProfileResponse getAccount(long id);


    RegisterAgentResponse changePassword(  ClientRequest request);
}
