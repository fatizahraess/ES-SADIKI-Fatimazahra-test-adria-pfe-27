
package net.essadiki.transactionservice.mcp;

import lombok.RequiredArgsConstructor;
import net.essadiki.transactionservice.domain.Transaction;
import net.essadiki.transactionservice.service.TransactionService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMcpTools {

    private final TransactionService transactionService;

    @McpTool(description = "Consulter toutes les transactions bancaires")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @McpTool(description = "Consulter une transaction par son UUID")
    public Transaction getTransaction(String transactionId) {
        return transactionService.getTransaction(
                UUID.fromString(transactionId));
    }

    @McpTool(description = "Consulter les transactions d'un compte bancaire")
    public List<Transaction> getAccountTransactions(String accountId) {
        return transactionService.getTransactionsByAccount(
                UUID.fromString(accountId));
    }
}
