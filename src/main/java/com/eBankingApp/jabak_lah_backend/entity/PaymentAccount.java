package com.eBankingApp.jabak_lah_backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "paymentAccountId")
public class PaymentAccount {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long paymentAccountId;
    @JsonManagedReference
    @OneToOne(mappedBy = "paymentAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Client client;

    private double accountBalance;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    private String bankName;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "paymentAccountId")
    private List<Transaction> transactions = new ArrayList<>();
    private  String verificationCode;
}
