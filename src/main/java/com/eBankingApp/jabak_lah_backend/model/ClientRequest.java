package com.eBankingApp.jabak_lah_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ClientRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String CIN;
    private String phoneNumber;
    private String password;
}
