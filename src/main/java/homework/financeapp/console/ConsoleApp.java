package homework.financeapp.console;

import homework.financeapp.model.User;
import homework.financeapp.model.TransferResult;
import homework.financeapp.service.*;
import homework.financeapp.validation.ValidationException;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleApp {

    private final AuthService authService;
    private final WalletService walletService;
    private final BudgetService budgetService;
    private final NotificationService notificationService;
    private final TransferService transferService;
    private final ReportService reportService;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(AuthService authService,
                      WalletService walletService,
                      BudgetService budgetService,
                      NotificationService notificationService,
                      TransferService transferService,
                      ReportService reportService) {
        this.authService = authService;
        this.walletService = walletService;
        this.budgetService = budgetService;
        this.notificationService = notificationService;
        this.transferService = transferService;
        this.reportService = reportService;
    }

    public void run() {
        System.out.println("Финансы v1. Консольное приложение.");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> handleRegister();
                    case "2" -> handleLogin();
                    case "3" -> handleAddIncome();
                    case "4" -> handleAddExpense();
                    case "5" -> handleSetBudget();
                    case "6" -> handleShowStats();
                    case "7" -> handleTransfer();
                    case "8" -> handleExport();
                    case "9" -> {
                        authService.persistCurrentUser();
                        System.out.println("До свидания! Данные сохранены.");
                        running = false;
                    }
                    default -> System.out.println("Неизвестный пункт меню. Введите цифру 1-9.");
                }
            } catch (ValidationException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Внутренняя ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Выберите команду:");
        System.out.println("1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("3. Добавить доход");
        System.out.println("4. Добавить расход");
        System.out.println("5. Установить бюджет");
        System.out.println("6. Показать статистику");
        System.out.println("7. Перевести деньги пользователю");
        System.out.println("8. Экспорт данных пользователя");
        System.out.println("9. Выход");
    }

    private void handleRegister() {
        System.out.println("=== Регистрация ===");
        System.out.print("Введите логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        authService.register(login, password);
        System.out.println("Пользователь зарегистрирован и авторизован.");
    }

    private void handleLogin() {
        System.out.println("=== Вход ===");
        System.out.print("Введите логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        authService.login(login, password);
        System.out.println("Вы вошли как " + login);
    }

    private void handleAddIncome() {
        User user = authService.getCurrentUserOrThrow();
        System.out.println("=== Добавление дохода ===");
        System.out.print("Введите сумму дохода: ");
        BigDecimal amount = readAmount();

        System.out.print("Введите категорию (например: зарплата). Можно оставить пустым: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            category = "прочее";
        }

        System.out.print("Введите описание (можно оставить пустым): ");
        String description = scanner.nextLine().trim();

        walletService.addIncome(user, amount, category, description);

        // после добавления можно сразу проверить общие суммы
        var income = walletService.getTotalIncome(user);
        var expense = walletService.getTotalExpense(user);
        if (expense.compareTo(income) > 0) {
            notificationService.notifyTotalExpensesGreater(income, expense);
        }

        authService.persistCurrentUser();
        System.out.println("Доход добавлен.");
    }

    private void handleAddExpense() {
        User user = authService.getCurrentUserOrThrow();
        System.out.println("=== Добавление расхода ===");
        System.out.print("Введите сумму расхода: ");
        BigDecimal amount = readAmount();

        System.out.print("Введите категорию расхода (обязательно): ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            throw new ValidationException("Категория обязательна для расхода");
        }

        System.out.print("Введите описание (можно оставить пустым): ");
        String description = scanner.nextLine().trim();

        walletService.addExpense(user, amount, category, description);

        budgetService.getBudget(user, category).ifPresent(budget -> {
            var spent = budgetService.getSpentForCategory(user, category);
            if (spent.compareTo(budget.getLimitAmount()) > 0) {
                notificationService.notifyBudgetExceeded(category, budget.getLimitAmount(), spent);
            }
        });

        var income = walletService.getTotalIncome(user);
        var expense = walletService.getTotalExpense(user);
        if (expense.compareTo(income) > 0) {
            notificationService.notifyTotalExpensesGreater(income, expense);
        }

        authService.persistCurrentUser();
        System.out.println("Расход добавлен.");
    }
    private void handleSetBudget() {
        User user = authService.getCurrentUserOrThrow();
        System.out.println("=== Установка бюджета ===");
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            throw new ValidationException("Категория обязательна");
        }
        System.out.print("Введите сумму лимита: ");
        BigDecimal limit = readAmount();

        budgetService.setBudget(user, category, limit);
        authService.persistCurrentUser();
        System.out.println("Бюджет по категории '" + category + "' установлен: " + limit);
    }


    private void handleShowStats() {
        User user = authService.getCurrentUserOrThrow();
        System.out.println("=== Статистика ===");
        reportService.printFullReport(user);
    }


    private void handleTransfer() {
        User user = authService.getCurrentUserOrThrow();
        System.out.println("=== Перевод денег пользователю ===");
        System.out.print("Введите логин получателя: ");
        String toLogin = scanner.nextLine().trim();
        System.out.print("Введите сумму перевода: ");
        BigDecimal amount = readAmount();
        System.out.print("Введите описание (можно пустым): ");
        String desc = scanner.nextLine().trim();

        TransferResult result = transferService.transfer(user, toLogin, amount, desc);
        System.out.println(result.message());
        authService.persistCurrentUser();
    }


    private void handleExport() {
        User user = authService.getCurrentUserOrThrow();
        authService.persistCurrentUser();
        System.out.println("Данные пользователя " + user.getLogin() + " сохранены в файл.");
    }


    private BigDecimal readAmount() {
        String line = scanner.nextLine().trim();


        if (line.isEmpty()) {
            throw new ValidationException("Сумма не может быть пустой");
        }


        if (!line.matches("[0-9]+(\\.[0-9]+)?")) {
            throw new ValidationException("Сумма должна содержать только цифры (и точку для дробных значений)");
        }


        if (line.length() > 1 && line.startsWith("0") && !line.startsWith("0.")) {
            throw new ValidationException("Сумма не может начинаться с нуля");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(line);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректный формат числа");
        }


        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Сумма должна быть положительным числом и больше 0");
        }

        return amount;
    }

}

