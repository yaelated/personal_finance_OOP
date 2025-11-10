package homework.financeapp.service;

import homework.financeapp.model.Budget;
import homework.financeapp.model.Transaction;
import homework.financeapp.model.TransactionType;
import homework.financeapp.model.User;

import java.math.BigDecimal;
import java.util.Optional;

public class BudgetService {

    public void setBudget(User user, String category, BigDecimal limit) {
        Budget budget = new Budget(category, limit);
        user.getWallet().setBudget(budget);
    }

    public Optional<Budget> getBudget(User user, String category) {
        return Optional.ofNullable(user.getWallet().getBudget(category));
    }

    public BigDecimal getSpentForCategory(User user, String category) {
        return user.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> category.equalsIgnoreCase(t.getCategory()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isBudgetExceeded(User user, String category) {
        Budget budget = user.getWallet().getBudget(category);
        if (budget == null) return false;
        BigDecimal spent = getSpentForCategory(user, category);
        return spent.compareTo(budget.getLimitAmount()) > 0;
    }
}
