
package net.essadiki.transactionservice.client;

import java.util.UUID;

public record AccountDTO(
        UUID id,
        String customerReference,
        String status,
        String currency,
        double balance
) {
}
