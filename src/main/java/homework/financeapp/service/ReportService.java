package homework.financeapp.service;

import homework.financeapp.model.User;
import java.math.BigDecimal;
import java.util.Map;

public class ReportService {

    private final WalletService walletService;
    private final BudgetService budgetService;

    public ReportService(WalletService walletService, BudgetService budgetService) {
        this.walletService = walletService;
        this.budgetService = budgetService;
    }

    public void printFullReport(User user) {
        BigDecimal totalIncome = walletService.getTotalIncome(user);
        BigDecimal totalExpense = walletService.getTotalExpense(user);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        System.out.println("=== Общая статистика ===");
        System.out.println("Общие доходы:  " + totalIncome);
        System.out.println("Общие расходы: " + totalExpense);
        System.out.println("Текущий баланс: " + balance);
        System.out.println();

        // ДОХОДЫ ПО КАТЕГОРИЯМ
        System.out.println("=== Доходы по категориям ===");
        Map<String, BigDecimal> incomeByCat = walletService.getIncomeByCategory(user);
        if (incomeByCat.isEmpty()) {
            System.out.println("Доходов нет");
        } else {
            incomeByCat.forEach((cat, sum) ->
                    System.out.println("  " + cat + ": " + sum)
            );
        }
        System.out.println();

        // РАСХОДЫ ПО КАТЕГОРИЯМ
        System.out.println("=== Расходы по категориям ===");
        Map<String, BigDecimal> expenseByCat = walletService.getExpensesByCategory(user);
        if (expenseByCat.isEmpty()) {
            System.out.println("Расходов нет");
        } else {
            expenseByCat.forEach((cat, sum) ->
                    System.out.println("  " + cat + ": " + sum)
            );
        }
        System.out.println();

        // БЮДЖЕТЫ
        System.out.println("=== Бюджеты ===");
        user.getWallet().getBudgets().forEach((category, budget) -> {
            BigDecimal spent = budgetService.getSpentForCategory(user, category);
            BigDecimal remaining = budget.getLimitAmount().subtract(spent);
            System.out.printf("%s: лимит=%s, потрачено=%s, осталось=%s%n",
                    category, budget.getLimitAmount(), spent, remaining);
        });
    }
}

