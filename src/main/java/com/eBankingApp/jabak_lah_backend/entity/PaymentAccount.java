//package com.eBankingApp.jabak_lah_backend.entity;
//
//import com.fasterxml.jackson.annotation.JsonIdentityInfo;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.fasterxml.jackson.annotation.ObjectIdGenerators;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
////@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "paymentAccountId")
//public class PaymentAccount {
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Id
//    private Long paymentAccountId;
//
//
//    @JsonManagedReference
//    @OneToOne(mappedBy = "paymentAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Client client;
//
//    private double accountBalance;
//
//    @Temporal(TemporalType.DATE)
//    private LocalDate createdDate;
//
//    private String bankName;
//
//
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "paymentAccountId")
//    private List<Transaction> transactions = new ArrayList<>();
//    private  String verificationCode;
//}
package com.eBankingApp.jabak_lah_backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "paymentAccountId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PaymentAccount {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long paymentAccountId;
    @JsonManagedReference
    @OneToOne(mappedBy = "paymentAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer"}) // Add this line
    private Client client;

    private double accountBalance;

    @Column(name = "created_date")
    private LocalDate createdDate;

    private String bankName;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "paymentAccountId")
    @JsonIgnoreProperties({"hibernateLazyInitializer"}) // Add this line
    private List<Transaction> transactions = new ArrayList<>();

    private String verificationCode;
}

