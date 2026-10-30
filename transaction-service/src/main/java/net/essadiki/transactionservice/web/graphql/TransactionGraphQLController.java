
package net.essadiki.transactionservice.web.graphql;

import lombok.RequiredArgsConstructor;
import net.essadiki.transactionservice.domain.Transaction;
import net.essadiki.transactionservice.service.TransactionService;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.getAllTransactions();
    }

    @QueryMapping
    public Transaction transactionById(@Argument String id) {
        return transactionService.getTransaction(
                UUID.fromString(id));
    }

    @QueryMapping
    public List<Transaction> transactionsByAccount(
            @Argument String accountId) {
        return transactionService.getTransactionsByAccount(
                UUID.fromString(accountId));
    }

    @MutationMapping
    public Transaction createTransaction(
            @Argument String accountId,
            @Argument Double amount,
            @Argument String currency,
            @Argument String merchantCategory,
            @Argument String country) {

        Transaction transaction = Transaction.builder()
                .accountId(UUID.fromString(accountId))
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .merchantCategory(merchantCategory)
                .country(country)
                .build();

        return transactionService.createTransaction(transaction);
    }
}
