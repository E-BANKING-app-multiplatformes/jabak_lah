package com.eBankingApp.jabak_lah_backend.controller;

import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.ClientRequest;
import com.eBankingApp.jabak_lah_backend.model.GetAccountIdResponse;
import com.eBankingApp.jabak_lah_backend.model.ClientProfileResponse;
import com.eBankingApp.jabak_lah_backend.model.RegisterAgentResponse;
import com.eBankingApp.jabak_lah_backend.services.ClientServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('client:read')")
    @GetMapping("/profile/getAccount/{id}")
    public  ResponseEntity<ClientProfileResponse> getAccount(@PathVariable("id")  long id ){
        return  new ResponseEntity<>(clientServiceV2.getAccount(id), HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('client:update')")
    @PutMapping("/changePassword")
    public ResponseEntity<RegisterAgentResponse> changePassword(@RequestBody ClientRequest request) {
        RegisterAgentResponse client = clientServiceV2.changePassword(request);
        return ResponseEntity.ok(client);
    }

}
