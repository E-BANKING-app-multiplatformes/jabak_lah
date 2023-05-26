package com.eBankingApp.jabak_lah_backend.controller;

import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.model.AgentRequest;
import com.eBankingApp.jabak_lah_backend.model.ClientRequest;
import com.eBankingApp.jabak_lah_backend.services.ClientService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/management")
//@Tag(name = "Management")
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





    @GetMapping("/List")
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
    public ResponseEntity<String> registerClient(
            @RequestBody ClientRequest request
    ) {
        return ResponseEntity.ok(service.registerAgent(request));
    }



    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('agent:update')")
    public ResponseEntity<ResponseEntity<String>> updateClient(@PathVariable("id") Long id, @RequestBody ClientRequest updatedAgent)  {
        return ResponseEntity.ok(service.updateClient(id,updatedAgent));
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('agent:delete')")
    public String deleteClient(@PathVariable("id") Long id) {

        if (service.deleteClient(id) == true) {
            return "Client has been deleted ";
        } else {
            return "No Client with the id :{} given exist in the database : make sure from the Id "  +id;
        }
    }
}
