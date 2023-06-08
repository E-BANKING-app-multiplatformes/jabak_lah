package com.eBankingApp.jabak_lah_backend.repository;

import com.eBankingApp.jabak_lah_backend.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {



    BankAccount findByClientCni(String cni);
}
