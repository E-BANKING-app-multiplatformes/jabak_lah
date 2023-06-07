package com.eBankingApp.jabak_lah_backend.repository;

import com.eBankingApp.jabak_lah_backend.entity.Creditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditorRepository extends JpaRepository<Creditor,Long> {

    Creditor getByName(String name);
}
