package com.eBankingApp.jabak_lah_backend.repository;
import com.eBankingApp.jabak_lah_backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long>{

    boolean existsByEmail(String email);
//    @Query(value = "SELECT * FROM client WHERE phone_number = ?1 AND role = 'CLIENT'", nativeQuery = true)
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Object> findByEmail(String email);

    @Query(value = "SELECT * FROM client WHERE role = 'AGENT'", nativeQuery = true)
    List<Client> findAllAgentWithRoleClient();


    @Query(value = "SELECT * FROM client WHERE id = ?1 AND role = 'AGENT'", nativeQuery = true)
    Client findAgentByClientId(Long id);
    @Query(value = "SELECT * FROM client WHERE id = ?1 AND role = 'CLIENT'", nativeQuery = true)
    Client findClientByClientId(Long id);
    @Query(value = "SELECT * FROM client WHERE role = 'CLIENT'", nativeQuery = true)
   List<Client> findAllClientsWithRoleClient();

   

    Client findById(String userId);

    @Query(value = "SELECT * FROM client WHERE phone_number = ?1 AND role = 'CLIENT'", nativeQuery = true)
    Client findByPhoneNumber(String phoneNumber);


}


