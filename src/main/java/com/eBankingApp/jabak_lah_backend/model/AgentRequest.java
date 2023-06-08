package com.eBankingApp.jabak_lah_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentRequest {
    private Integer id;

//    private MultipartFile photo;
    private String firstName;
    private String lastName ;
    private String address ;
    private String email ;
    private String phoneNumber ;
    private String CommercialRn ;
    private String patentNumber ;
    private String password ;

}
