package com.eBankingApp.jabak_lah_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentResposne {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
