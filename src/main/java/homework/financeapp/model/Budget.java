package homework.financeapp.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Budget implements Serializable {
    private final String category;
    private BigDecimal limitAmount;

    public Budget(String category, BigDecimal limitAmount) {
        this.category = category;
        this.limitAmount = limitAmount;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }
}
