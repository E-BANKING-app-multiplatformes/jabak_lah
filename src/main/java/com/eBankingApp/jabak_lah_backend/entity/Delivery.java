package com.eBankingApp.jabak_lah_backend.entity;

import com.eBankingApp.jabak_lah_backend.model.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private Date deliveryDate;

    private double deliverPrice;

    private DeliveryStatus status;

    // Other fields and relationships as needed
}
