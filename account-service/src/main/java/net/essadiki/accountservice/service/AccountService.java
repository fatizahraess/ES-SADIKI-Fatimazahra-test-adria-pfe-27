package net.essadiki.accountservice.service;
import lombok.RequiredArgsConstructor;
import net.essadiki.accountservice.domain.Account;
import net.essadiki.accountservice.domain.AccountStatus;
import net.essadiki.accountservice.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public Account getAccount(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Compte introuvable"
                ));
    }

    @Transactional
    public Account createAccount(Account account) {
        account.setId(null);

        if (account.getCustomerReference() == null ||
                account.getCustomerReference().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La référence client est obligatoire"
            );
        }

        if (account.getCurrency() == null ||
                account.getCurrency().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La devise est obligatoire"
            );
        }

        if (!Double.isFinite(account.getBalance()) ||
                account.getBalance() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solde initial invalide"
            );
        }

        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ACTIVE);
        }

        return repository.save(account);
    }

    @Transactional
    public Account updateBalance(UUID id, double amount) {
        Account account = getAccount(id);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le compte est bloqué"
            );
        }

        if (!Double.isFinite(amount) ||
                !Double.isFinite(account.getBalance() + amount) ||
                account.getBalance() + amount < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solde insuffisant ou montant invalide"
            );
        }

        account.setBalance(account.getBalance() + amount);
        return repository.save(account);
    }

    @Transactional
    public Account changeStatus(UUID id, AccountStatus status) {
        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Statut obligatoire"
            );
        }

        Account account = getAccount(id);
        account.setStatus(status);

        return repository.save(account);
    }

    public void deleteAccount(UUID id) {
        Account account = getAccount(id);
        repository.delete(account);
    }
}
