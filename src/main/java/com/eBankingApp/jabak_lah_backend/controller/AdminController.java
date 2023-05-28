package com.eBankingApp.jabak_lah_backend.controller;


import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.AgentRequest;
import com.eBankingApp.jabak_lah_backend.model.RegisterAgentResponse;
import com.eBankingApp.jabak_lah_backend.repository.ClientRepository;

import com.eBankingApp.jabak_lah_backend.services.AdminService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor

public class AdminController {
    private final AdminService service;
    private final ClientRepository userRepository;





    @GetMapping("/list")
   @PreAuthorize("hasAuthority('admin:read')")
    public List<Client> get() {
        return service.findAll();
    }

    @GetMapping("/agent/{id}")
    @PreAuthorize("hasAuthority('admin:read')")
    public Client getById(@PathVariable("id") Long id) {
        // Logic to find user by ID
        Client agent = service.findById(id);
        // Return the found user or handle the case where user is not found
        return agent;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin:create')")
    @Hidden
    public ResponseEntity<RegisterAgentResponse> registerAgent(
            @RequestBody AgentRequest request
    ) {
        return ResponseEntity.ok(service.registerAgent(request));
    }



//    @PutMapping("/up")
//    @PreAuthorize("hasAuthority('admin:update')")
//    @Hidden
//    public String put() {
//        return "PUT:: admin controller";
//    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<RegisterAgentResponse> updateUser(@PathVariable("id") Long id, @RequestBody AgentRequest updatedAgent)  {
      return ResponseEntity.ok(service.updateAgent(id,updatedAgent));
    }






    @DeleteMapping("/del")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Hidden
    public String delete() {
        return "DELETE:: admin controller";
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public RegisterAgentResponse deleteUser(@PathVariable("id") Long id)  {
            return service.delete(id);
    }

}
