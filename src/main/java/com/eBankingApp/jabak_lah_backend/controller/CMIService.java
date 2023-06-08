package com.eBankingApp.jabak_lah_backend.controller;
import com.eBankingApp.jabak_lah_backend.entity.*;
import com.eBankingApp.jabak_lah_backend.model.*;
import com.eBankingApp.jabak_lah_backend.repository.*;
import com.eBankingApp.jabak_lah_backend.services.CreditorService;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@PreAuthorize("hasRole('CLIENT')")
@RestController
@CrossOrigin(origins = "http://localhost:8888") // Allow requests from Angular app's origin
@RequestMapping("/fim/est3Dgate")
public class CMIService {
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


    private final String BRAND_NAME = "Vonage APIs";
    private String generateVerificationCode() {
        int min = 1000;
        int max = 9999;
        int verificationCodeInt = min + (int) (Math.random() * (max - min + 1));
        return String.valueOf(verificationCodeInt);
    }
    @PreAuthorize("hasAuthority('client:read')")
    @GetMapping("/sendVerificationCode/{accountId}")
    public ResponseEntity<SendVerificationCodeResponse> sendVerificationCode(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));

        Client client = account.getClient();
        if (client == null) {
            return new ResponseEntity<>(SendVerificationCodeResponse.builder().message("error 001").build(), HttpStatus.OK);
        }

        String verificationCode = generateVerificationCode();

        // Send the SMS with the verification code
        String phoneNumber = client.getPhoneNumber();
        TextMessage message = new TextMessage(BRAND_NAME, phoneNumber, "Your verification code is: " + verificationCode);

        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            // Save the verification code in the PaymentAccount entity
            account.setVerificationCode(verificationCode);
            paymentAccountRepository.save(account);
            return new ResponseEntity<>( SendVerificationCodeResponse.builder().message("Verification code: [" + verificationCode + "] sent to phone number: " + phoneNumber).build(), HttpStatus.OK);

        } else {
            return new ResponseEntity<>( SendVerificationCodeResponse.builder().message("message not sent ").build(), HttpStatus.OK);
        }
    }
    @PreAuthorize("hasAuthority('client:create')")
    @PostMapping("/{verificationCode}/makeTransaction")
    @Transactional
    public TransactionResponse executeTransaction(@PathVariable("verificationCode") String verificationCode , @RequestBody TransactionRequest transactionRequest) {
        PaymentAccount account = paymentAccountRepository.findById(transactionRequest.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        // Check the verification code
        if (!verificationCode.equals(account.getVerificationCode())) {
            return TransactionResponse.builder()
                    .message("Invalid verification code. Transaction not allowed.")
                    .build();
        }
        Transaction transaction = Transaction.builder()
                .amount(transactionRequest.getAmount())
                .paymentAccount(account)
                .creditor(transactionRequest.getCreditor())
                .date(transactionRequest.getDate())
                .transactionStatus(TransactionStatus.PENDING)
                .creditorType(transactionRequest.getCreditorType())
                .description(transactionRequest.getDescription())
                .phoneNumber(transactionRequest.getPhoneNumber())
                .build();
        if (transaction.getCreditorType() == CreditorType.STORE) {
            // Build the Delivery
            Delivery delivery = null;
            if (transactionRequest.getAddress() != null) {
                delivery = Delivery.builder()
                        .address(transactionRequest.getAddress())
                        .deliveryDate(new Date()) // Set the delivery date as the current date
                        .deliverPrice(70.00) // Set the initial delivery price to 0.0
                        .status(DeliveryStatus.PENDING) // Set the delivery status as PENDING
                        .build();

                // Save the Delivery in the database
                deliveryRepository.save(delivery);
            }

            // Build the CustomerOrder
            CustomerOrder customerOrder = CustomerOrder.builder()
                    .delivery(delivery) // Associate the Delivery with the CustomerOrder
                    .totalAmount(0.0) // Set the initial total amount to 0.0
                    .build();

            // Save the CustomerOrder in the database
            customerOrderRepository.save(customerOrder);

            // Convert OrderItemRequest list to OrderItem list and save all OrderItems
            List<OrderItem> orderItems = transactionRequest.getItems().stream()
                    .map(orderItemRequest -> {
                        OrderItem orderItem = OrderItem.builder()
                                .itemName(orderItemRequest.getItemName())
                                .quantity(orderItemRequest.getQuantity())
                                .price(orderItemRequest.getPrice())
                                .category(orderItemRequest.getCategory())
                                .productNumber(orderItemRequest.getProductNumber())
                                .order(customerOrder) // Set the CustomerOrder for each OrderItem
                                .build();

                        // Save the OrderItem in the database
                        orderItemRepository.save(orderItem);

                        return orderItem;
                    })
                    .collect(Collectors.toList());

            // Set the list of OrderItems for the CustomerOrder
            customerOrder.setItems(orderItems);
            customerOrder.setDelivery(delivery);


            // Calculate the total amount
            double totalAmount = customerOrder.calculateTotalAmount();
            customerOrder.setTotalAmount(totalAmount);

            // Save the updated CustomerOrder in the database
            customerOrderRepository.save(customerOrder);

            // Associate the CustomerOrder with the Transaction
            transaction.setAmount(totalAmount);
            transaction.setOrder(customerOrder);

        }


        boolean isRechargeType = transaction.getCreditorType() == CreditorType.RECHARGE;
        boolean checkPhone = creditorService.checkPhoneNumber(transactionRequest.getPhoneNumber(), transaction.getCreditor());
        boolean sendMessage=true ;
        if (isRechargeType && !checkPhone) {
            sendMessage=false;
            return TransactionResponse.builder()
                    .message("Invalid phone number for :"+transaction.getCreditor()+". Transaction not allowed.")
                    .build();
        }
if (account.getAccountBalance() >= transactionRequest.getAmount()) {
            double updateBalance = account.getAccountBalance() - transactionRequest.getAmount();
            account.setAccountBalance(updateBalance);
            transaction.setTransactionStatus(TransactionStatus.SUCCEEDED);
            transactionRepository.save(transaction);
            List<Transaction> transactions = account.getTransactions();
            transactions.add(transaction);
            account.setTransactions(transactions);
            paymentAccountRepository.save(account);
            if(sendMessage&&isRechargeType){

            String phoneNumber = transaction.getPhoneNumber();
             TextMessage message = new TextMessage(BRAND_NAME, phoneNumber, "You have activated the offer : " + transaction.getDescription()+"amount:"+transaction.getAmount());}

    return TransactionResponse.builder()
                    .message("Transaction executed successfully")
                    .build();
        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            return TransactionResponse.builder()
                    .message("Transaction Failed!! try again later ")
                    .build();
        }
    }
    @PreAuthorize("hasAuthority('client:read')")
    @GetMapping("/getAccountBalance/{accountId}")
    @Transactional(readOnly = true)
    public ResponseEntity<GetAccountBalanceResponse> getAccountBalance(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        return new ResponseEntity<>(GetAccountBalanceResponse.builder().balance(account.getAccountBalance()).build(),HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('client:read')")
    @GetMapping("/getTransactionHistories/{accountId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Transaction>>getTransactionHistories(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        account.getTransactions().size();
        return new ResponseEntity<>(account.getTransactions(),HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('client:read')")
    @GetMapping("/getFailedTransactions/{accountId}")
    @Transactional(readOnly = true)
    public List<Transaction> getFailedTransactions(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        account.getTransactions().size();
        return account.getTransactions().stream()
                .filter(transaction -> transaction.getTransactionStatus() == TransactionStatus.FAILED)
                .collect(Collectors.toList());
    }
    @PreAuthorize("hasAuthority('client:read')")
    @GetMapping("/getSucceedTransactions/{accountId}")
    @Transactional(readOnly = true)
    public List<Transaction> getSucceedTransactions(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        account.getTransactions().size();
        return account.getTransactions().stream()
                .filter(transaction -> transaction.getTransactionStatus() == TransactionStatus.SUCCEEDED)
                .collect(Collectors.toList());
    }
}
