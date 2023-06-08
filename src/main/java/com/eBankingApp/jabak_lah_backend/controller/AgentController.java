package com.eBankingApp.jabak_lah_backend.controller;

import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.ClientRequest;
import com.eBankingApp.jabak_lah_backend.model.RegisterAgentResponse;
import com.eBankingApp.jabak_lah_backend.services.ClientService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/client")

@RequiredArgsConstructor
public class AgentController {

private  final ClientService service;
    @Operation(
            description = "Get endpoint for Agent",
            summary = "This is a summary for management get endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )





    @GetMapping("/list")
    @PreAuthorize("hasAuthority('agent:read')")
    public List<Client> getAllClient() {
        return service.findAll();
    }

    @GetMapping("/client/{id}")
    @PreAuthorize("hasAuthority('agent:read')")
    public Client getById(@PathVariable("id") Long id) {
        // Logic to find user by ID
        Client Client = service.findById(id);
        // Return the found user or handle the case where user is not found
        return Client;
    }


    @PostMapping("/register")
    @PreAuthorize("hasAuthority('agent:create')")
    @Hidden
    public ResponseEntity<RegisterAgentResponse> registerClient(
            @RequestBody ClientRequest request
    ) {
        return ResponseEntity.ok(service.registerClient(request));
    }



    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('agent:update')")
    public ResponseEntity<RegisterAgentResponse> updateClient(@PathVariable("id") Long id, @RequestBody ClientRequest updatedAgent)  {
        return ResponseEntity.ok(service.updateClient(id,updatedAgent));
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('agent:delete')")
    public RegisterAgentResponse deleteClient(@PathVariable("id") Long id) { return service.deleteClient(id);}}
