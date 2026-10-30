
package net.essadiki.transactionservice.service;

import lombok.RequiredArgsConstructor;
import net.essadiki.transactionservice.domain.Transaction;
import net.essadiki.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import net.essadiki.transactionservice.client.AccountDTO;
import net.essadiki.transactionservice.client.AccountFeignClient;
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

    public List<Transaction> getTransactionsByAccount(UUID accountId) {
        return repository.findByAccountId(accountId);
    }

    public AccountDTO getAccountDetails(UUID accountId) {
        return accountFeignClient.getAccount(accountId);
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {

        if (transaction.getAccountId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Identifiant du compte obligatoire"
            );
        }

        if (transaction.getAmount() == null ||
                transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant doit être strictement positif"
            );
        }

        if (transaction.getAmount().compareTo(maxAmount) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant dépasse le plafond autorisé"
            );
        }

        if (transaction.getCurrency() == null ||
                transaction.getCurrency().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Devise obligatoire"
            );
        }

        // C.3 : ajouter la validation du compte via OpenFeign
        // et la mise à jour du solde avant l'utilisation métier réelle.

        transaction.setId(null);
        transaction.setCreatedAt(null);

        return repository.save(transaction);
    }
}
