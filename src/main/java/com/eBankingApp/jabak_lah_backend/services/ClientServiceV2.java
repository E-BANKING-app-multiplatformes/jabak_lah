package com.eBankingApp.jabak_lah_backend.services;

import com.eBankingApp.jabak_lah_backend.model.ClientProfileResponse;

public interface ClientServiceV2 {

    long getAccountId(long id);

    ClientProfileResponse getAccount(long id);
}
