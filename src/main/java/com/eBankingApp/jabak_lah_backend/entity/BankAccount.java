package com.eBankingApp.jabak_lah_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccount {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance;

    private String accountNumber;

    private String clientCni;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
