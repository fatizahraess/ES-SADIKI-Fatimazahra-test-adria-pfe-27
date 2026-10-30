package net.essadiki.accountservice.mcp;
import lombok.RequiredArgsConstructor;
import net.essadiki.accountservice.domain.Account;
import net.essadiki.accountservice.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountMcpTools {

    private final AccountService accountService;

    @Tool(description = "Consulter tous les comptes bancaires")
    public List<Account> getAllBankAccounts() {
        return accountService.getAllAccounts();
    }

    @Tool(description = "Consulter un compte bancaire par son UUID")
    public Account getBankAccount(String accountId) {
        return accountService.getAccount(
                UUID.fromString(accountId));
    }

    @Tool(description = "Consulter le solde d'un compte bancaire")
    public double getAccountBalance(String accountId) {
        return accountService.getAccount(
                UUID.fromString(accountId)).getBalance();
    }
}
