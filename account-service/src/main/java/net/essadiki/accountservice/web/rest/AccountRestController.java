package net.essadiki.accountservice.web.rest;
import lombok.RequiredArgsConstructor;
import net.essadiki.accountservice.domain.Account;
import net.essadiki.accountservice.domain.AccountStatus;
import net.essadiki.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable UUID id) {
        return accountService.getAccount(id);
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @PutMapping("/{id}/balance")
    public Account updateBalance(
            @PathVariable UUID id,
            @RequestParam double amount) {
        return accountService.updateBalance(id, amount);
    }

    @PutMapping("/{id}/status")
    public Account updateStatus(
            @PathVariable UUID id,
            @RequestParam AccountStatus status) {
        return accountService.changeStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
    }
}
