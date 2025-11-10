package homework.financeapp.service;

import homework.financeapp.model.Transaction;
import homework.financeapp.model.TransactionType;
import homework.financeapp.model.User;
import homework.financeapp.validation.ValidationException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class WalletService {

    public void addIncome(User user, BigDecimal amount, String category, String description) {
        validateAmount(amount);
        Transaction tx = new Transaction(TransactionType.INCOME, amount, category, description);
        user.getWallet().addTransaction(tx);
    }

    public void addExpense(User user, BigDecimal amount, String category, String description) {
        validateAmount(amount);
        if (category == null || category.isBlank()) {
            throw new ValidationException("Категория обязательна");
        }
        Transaction tx = new Transaction(TransactionType.EXPENSE, amount, category, description);
        user.getWallet().addTransaction(tx);
    }

    public BigDecimal getTotalIncome(User user) {
        return user.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME || t.getType() == TransactionType.TRANSFER_IN)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense(User user) {
        return user.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE || t.getType() == TransactionType.TRANSFER_OUT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ⬇⬇⬇ новый метод
    public Map<String, BigDecimal> getIncomeByCategory(User user) {
        Map<String, BigDecimal> result = new HashMap<>();
        user.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME || t.getType() == TransactionType.TRANSFER_IN)
                .forEach(t -> {
                    var prev = result.getOrDefault(t.getCategory(), BigDecimal.ZERO);
                    result.put(t.getCategory(), prev.add(t.getAmount()));
                });
        return result;
    }
    // ⬆⬆⬆

    public Map<String, BigDecimal> getExpensesByCategory(User user) {
        Map<String, BigDecimal> result = new HashMap<>();
        user.getWallet().getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .forEach(t -> {
                    var prev = result.getOrDefault(t.getCategory(), BigDecimal.ZERO);
                    result.put(t.getCategory(), prev.add(t.getAmount()));
                });
        return result;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Сумма должна быть больше 0");
        }
    }
}
