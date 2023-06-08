package com.eBankingApp.jabak_lah_backend.controller;

import com.eBankingApp.jabak_lah_backend.entity.BankAccount;
import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.entity.PaymentAccount;
import com.eBankingApp.jabak_lah_backend.model.OpenPaymentAccountResponse;
import com.eBankingApp.jabak_lah_backend.model.OpenPaymentAccountTransactionRequest;
import com.eBankingApp.jabak_lah_backend.repository.*;
import com.eBankingApp.jabak_lah_backend.services.CreditorService;
import com.vonage.client.VonageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
@RestController
@CrossOrigin(origins = "*") // Allow requests from Angular app's origin
@PreAuthorize("hasAuthority('AGENT')")
@RequestMapping("/fim/est3DgateV2")
public class CMIServiceV2 {

    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    private PaymentAccountRepository paymentAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private VonageClient vonageClient;
    @Autowired
    private CreditorService creditorService;
    @Autowired
    CustomerOrderRepository customerOrderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    ClientRepository clientRepository;
    @PreAuthorize("hasAuthority('agent:create')")
    @PostMapping("/openPaymentAccount/{clientId}")
    public ResponseEntity<OpenPaymentAccountResponse> openPaymentAccount(@PathVariable("clientId") long clientId, @RequestBody OpenPaymentAccountTransactionRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));

        String cni = client.getCIN();
        BankAccount bankAccount = bankAccountRepository.findByClientCni(cni);

        double accountBalance = bankAccount.getBalance();

        if (accountBalance >= request.getBalance()) {
            LocalDate currentDate = LocalDate.now();

            PaymentAccount paymentAccount=PaymentAccount.builder()
                    .accountBalance(request.getBalance())
                    .bankName("CIH")
                    .createdDate(currentDate)
                    .build();
            paymentAccountRepository.save(paymentAccount);
            long paymentAccountId =paymentAccount.getPaymentAccountId();

            client.setIsPaymentAccountActivated(true);
            client.setPaymentAccount(paymentAccount);


            clientRepository.save(client);



            return new ResponseEntity<>(OpenPaymentAccountResponse.builder().isOpened(true).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(OpenPaymentAccountResponse.builder().isOpened(false).build(), HttpStatus.OK);
        }
    }

}
