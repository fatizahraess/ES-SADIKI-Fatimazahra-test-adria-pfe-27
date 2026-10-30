
package net.essadiki.transactionservice.web.rest;

import lombok.RequiredArgsConstructor;
import net.essadiki.transactionservice.domain.Transaction;
import net.essadiki.transactionservice.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import net.essadiki.transactionservice.client.AccountDTO;
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/account-info/{accountId}")
    public AccountDTO getAccountInfo(
            @PathVariable UUID accountId) {

        return transactionService.getAccountDetails(accountId);
    }

    @GetMapping("/{id}")
    public Transaction getTransaction(@PathVariable UUID id) {
        return transactionService.getTransaction(id);
    }

    @GetMapping("/account/{accountId}")
    public List<Transaction> getTransactionsByAccount(
            @PathVariable UUID accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }

    @PostMapping
    public Transaction createTransaction(
            @RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }
}
