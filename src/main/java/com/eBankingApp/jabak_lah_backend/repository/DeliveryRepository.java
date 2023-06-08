package com.eBankingApp.jabak_lah_backend.repository;

import com.eBankingApp.jabak_lah_backend.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
}
