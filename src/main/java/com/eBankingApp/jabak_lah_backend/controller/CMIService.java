package com.eBankingApp.jabak_lah_backend.controller;
import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.entity.PaymentAccount;
import com.eBankingApp.jabak_lah_backend.entity.Transaction;
import com.eBankingApp.jabak_lah_backend.model.TransactionRequest;
import com.eBankingApp.jabak_lah_backend.model.TransactionResponse;
import com.eBankingApp.jabak_lah_backend.model.TransactionStatus;
import com.eBankingApp.jabak_lah_backend.repository.PaymentAccountRepository;
import com.eBankingApp.jabak_lah_backend.repository.TransactionRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/fim/est3Dgate")
public class CMIService {
    @Autowired
    private PaymentAccountRepository paymentAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private VonageClient vonageClient;
    private final String BRAND_NAME = "Vonage APIs";
    private String generateVerificationCode() {
        int min = 1000;
        int max = 9999;
        int verificationCodeInt = min + (int) (Math.random() * (max - min + 1));
        return String.valueOf(verificationCodeInt);
    }
    @GetMapping("/sendVerificationCode/{accountId}")
    public String sendVerificationCode(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));

        Client client = account.getClient();
        if (client == null) {
            return "Client not found for the specified account.";
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
            return "Verification code: [" + verificationCode + "] sent to phone number: " + phoneNumber;
        } else {
            return "message not sent ";
        }
    }
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
                .build();

        if (account.getAccountBalance() >= transactionRequest.getAmount()) {
            double updateBalance = account.getAccountBalance() - transactionRequest.getAmount();
            account.setAccountBalance(updateBalance);
            transaction.setTransactionStatus(TransactionStatus.SUCCEEDED);
            transactionRepository.save(transaction);

            List<Transaction> transactions = account.getTransactions();
            transactions.add(transaction);
            account.setTransactions(transactions);
            paymentAccountRepository.save(account);
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
    @GetMapping("/getAccountBalance/{accountId}")
    @Transactional(readOnly = true)
    public double getAccountBalance(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        return account.getAccountBalance();
    }

    @GetMapping("/getTransactionHistories/{accountId}")
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistories(@PathVariable("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        account.getTransactions().size();
        return account.getTransactions();
    }

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

}
