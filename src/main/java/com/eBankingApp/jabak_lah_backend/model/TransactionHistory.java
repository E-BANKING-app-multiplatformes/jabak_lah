package com.eBankingApp.jabak_lah_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class TransactionHistory {
    private long id ;
    private String creditor ;
    private  double amount ;
    private Date date ;
}

