package com.eBankingApp.jabak_lah_backend.entity;

import com.eBankingApp.jabak_lah_backend.model.CreditorType;
import com.eBankingApp.jabak_lah_backend.services.CreditorServiceImpl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Creditor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String  name  ;
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "creditor_phone_numbers",
            joinColumns = @JoinColumn(name = "creditor_id"))
    private List<String> phoneNumbers;
   private CreditorType creditorType;

    @PostLoad
    public void postLoad() {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            phoneNumbers = new ArrayList<>();
            phoneNumbers.add("+212617093514");
            phoneNumbers.add("+212616671210");
        }
    }
}
