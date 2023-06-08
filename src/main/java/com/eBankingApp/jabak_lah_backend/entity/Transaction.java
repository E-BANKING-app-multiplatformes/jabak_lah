package com.eBankingApp.jabak_lah_backend.entity;

import com.eBankingApp.jabak_lah_backend.model.CreditorType;
import com.eBankingApp.jabak_lah_backend.model.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "transactionId")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;
    private double amount;
    private String creditor;
    @Temporal(TemporalType.DATE)
    private Date date;
    private TransactionStatus transactionStatus;
    private String description;
    private CreditorType creditorType;
    private String phoneNumber;
    @OneToOne
    @JoinColumn(name = "order_id")
    private CustomerOrder order;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paymentAccountId")
    @JsonIgnoreProperties("transactions")
    private PaymentAccount paymentAccount;

}
