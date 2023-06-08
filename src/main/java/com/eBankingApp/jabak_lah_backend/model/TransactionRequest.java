package com.eBankingApp.jabak_lah_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
    private double amount ;
    private String creditor ;
    private Date date ;
    private long accountId  ;
    private String description ;
    private String phoneNumber ;
    private CreditorType creditorType;
    private List<OrderItemRequest> items;
    private String address ;




}
