package net.cmauri.chain.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    public Transaction(BigDecimal amount, String sender, String recipient) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
    }

    private String id;
    private BigDecimal amount;
    private String sender;
    private String recipient;

    public String getPublicRepresentation() {
        return amount + " from " + sender + " to " + recipient;
    }
}
