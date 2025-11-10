package homework.financeapp.service;

import java.math.BigDecimal;

public class NotificationService {

    public void notifyBudgetExceeded(String category, BigDecimal limit, BigDecimal spent) {
        System.out.printf("⚠ Превышен лимит по категории '%s': лимит=%s, потрачено=%s%n",
                category, limit, spent);
    }

    public void notifyTotalExpensesGreater(BigDecimal income, BigDecimal expense) {
        System.out.printf("⚠ Расходы (%s) превысили доходы (%s)%n", expense, income);
    }

    public void notifyIncomingTransfer(String toLogin, BigDecimal amount, String fromLogin) {
        System.out.printf("📩 Пользователю %s: вам пришёл перевод на сумму %s от %s%n",
                toLogin, amount, fromLogin);
    }
}
