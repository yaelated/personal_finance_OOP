package homework.financeapp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class Wallet implements Serializable {
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Budget> budgets = new HashMap<>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Map<String, Budget> getBudgets() {
        return budgets;
    }

    public void addTransaction(Transaction tx) {
        transactions.add(tx);
    }

    public void setBudget(Budget budget) {
        budgets.put(budget.getCategory(), budget);
    }

    public Budget getBudget(String category) {
        return budgets.get(category);
    }

    public BigDecimal getCurrentBalance() {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            switch (t.getType()) {
                case INCOME, TRANSFER_IN -> income = income.add(t.getAmount());
                case EXPENSE, TRANSFER_OUT -> expense = expense.add(t.getAmount());
            }
        }
        return income.subtract(expense);
    }
}
