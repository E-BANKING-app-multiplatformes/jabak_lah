package com.eBankingApp.jabak_lah_backend.model;

import com.eBankingApp.jabak_lah_backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor

public class ClientRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String CIN;
    private String phoneNumber;
    private Date birthDate ;
    private String newPassword;
    private Role role;

}
