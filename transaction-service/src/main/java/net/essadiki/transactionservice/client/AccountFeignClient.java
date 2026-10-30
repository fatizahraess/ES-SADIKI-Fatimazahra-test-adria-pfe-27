
package net.essadiki.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${account.service.url}"
)
public interface AccountFeignClient {

    @GetMapping("/api/accounts/{id}")
    AccountDTO getAccount(@PathVariable("id") UUID id);

    @PutMapping("/api/accounts/{id}/balance")
    AccountDTO updateBalance(
            @PathVariable("id") UUID id,
            @RequestParam("amount") double amount
    );
}
