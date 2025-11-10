package homework.financeapp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction implements Serializable {
    private final String id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String category;
    private final String description;
    private final LocalDateTime dateTime;

    public Transaction(TransactionType type,
                       BigDecimal amount,
                       String category,
                       String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.dateTime = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
