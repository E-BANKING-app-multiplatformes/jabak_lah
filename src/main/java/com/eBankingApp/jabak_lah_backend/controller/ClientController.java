package com.eBankingApp.jabak_lah_backend.controller;

import com.eBankingApp.jabak_lah_backend.model.GetAccountIdResponse;
import com.eBankingApp.jabak_lah_backend.services.ClientServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('CLIENT')")
@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ClientServiceV2 clientServiceV2;

    @PreAuthorize("hasAuthority('client:read')")
    @GetMapping("/getAccountId/{id}")
    public ResponseEntity<GetAccountIdResponse> getAccountId(@PathVariable("id") long id) {
        long accountId = clientServiceV2.getAccountId(id);
        return new ResponseEntity<>(GetAccountIdResponse.builder().accountId(accountId).build(), HttpStatus.OK);
    }
}
