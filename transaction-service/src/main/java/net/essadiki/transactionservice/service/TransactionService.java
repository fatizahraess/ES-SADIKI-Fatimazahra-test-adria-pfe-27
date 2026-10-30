
package net.essadiki.transactionservice.service;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import net.essadiki.transactionservice.client.AccountDTO;
import net.essadiki.transactionservice.client.AccountFeignClient;
import net.essadiki.transactionservice.domain.Transaction;
import net.essadiki.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountFeignClient accountFeignClient;

    @Value("${bank.transaction.max-amount:1000}")
    private BigDecimal maxAmount;

    // ==============================
    // CONSULTATION DES TRANSACTIONS
    // ==============================

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction getTransaction(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Transaction introuvable"
                        ));
    }

    public List<Transaction> getTransactionsByAccount(
            UUID accountId) {
        return repository.findByAccountId(accountId);
    }

    // ==============================
    // OPENFEIGN + RESILIENCE4J
    // ==============================

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "accountFallback"
    )
    public AccountDTO getAccountDetails(UUID accountId) {
        return accountFeignClient.getAccount(accountId);
    }

    public AccountDTO accountFallback(
            UUID accountId,
            Throwable exception) {

        if (exception instanceof FeignException.NotFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Compte bancaire introuvable",
                    exception
            );
        }

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Account-Service temporairement indisponible",
                exception
        );
    }

    // ==============================
    // CREATION D'UNE TRANSACTION
    // ==============================

    @Transactional
    public Transaction createTransaction(
            Transaction transaction) {

        // 1. Verifier l'identifiant du compte
        if (transaction.getAccountId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Identifiant du compte obligatoire"
            );
        }

        // 2. Verifier le montant
        BigDecimal amount = transaction.getAmount();

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant doit etre strictement positif"
            );
        }

        // 3. Verifier le plafond
        if (amount.compareTo(maxAmount) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant depasse le plafond autorise"
            );
        }

        // 4. Verifier la devise
        if (transaction.getCurrency() == null ||
                transaction.getCurrency().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Devise obligatoire"
            );
        }

        // C.3.2 non realisee :
        // La verification du compte actif, de sa devise
        // et la mise a jour du solde seront ajoutees ensuite.

        // 5. Preparer la transaction
        transaction.setId(null);
        transaction.setCreatedAt(null);

        // 6. Enregistrer dans H2
        return repository.save(transaction);
    }
}
