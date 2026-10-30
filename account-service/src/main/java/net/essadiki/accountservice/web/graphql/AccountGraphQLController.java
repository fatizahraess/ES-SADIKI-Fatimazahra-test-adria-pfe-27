package net.essadiki.accountservice.web.graphql;



import lombok.RequiredArgsConstructor;
import net.essadiki.accountservice.domain.Account;
import net.essadiki.accountservice.domain.AccountStatus;
import net.essadiki.accountservice.service.AccountService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphQLController {

    private final AccountService accountService;

    @QueryMapping
    public List<Account> accounts() {
        return accountService.getAllAccounts();
    }

    @QueryMapping
    public Account accountById(@Argument String id) {
        return accountService.getAccount(UUID.fromString(id));
    }

    @MutationMapping
    public Account createAccount(
            @Argument String customerReference,
            @Argument String currency,
            @Argument double balance) {

        Account account = Account.builder()
                .customerReference(customerReference)
                .currency(currency)
                .balance(balance)
                .status(AccountStatus.ACTIVE)
                .build();

        return accountService.createAccount(account);
    }

    @MutationMapping
    public Account updateBalance(
            @Argument String id,
            @Argument double amount) {
        return accountService.updateBalance(
                UUID.fromString(id), amount);
    }

    @MutationMapping
    public Account changeAccountStatus(
            @Argument String id,
            @Argument AccountStatus status) {
        return accountService.changeStatus(
                UUID.fromString(id), status);
    }

    @MutationMapping
    public boolean deleteAccount(@Argument String id) {
        accountService.deleteAccount(UUID.fromString(id));
        return true;
    }
}
