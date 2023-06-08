package com.eBankingApp.jabak_lah_backend.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {
    private String itemName;
    private int quantity;
    private Category category;
    private String productNumber;
    private double price ;


    // Other fields as needed
}