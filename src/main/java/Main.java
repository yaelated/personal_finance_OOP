import homework.financeapp.console.ConsoleApp;
import homework.financeapp.persistence.FileUserStorage;
import homework.financeapp.repository.InMemoryUserRepository;
import homework.financeapp.service.AuthService;
import homework.financeapp.service.WalletService;
import homework.financeapp.service.BudgetService;
import homework.financeapp.service.NotificationService;
import homework.financeapp.service.TransferService;
import homework.financeapp.service.ReportService;




import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        var userRepository = new InMemoryUserRepository();
        var userStorage = new FileUserStorage(Paths.get("data"));
        var authService = new AuthService(userRepository, userStorage);
        var walletService = new WalletService();
        var budgetService = new BudgetService();
        var notificationService = new NotificationService();
        var transferService = new TransferService(walletService, notificationService);
        transferService.setUserRepository(userRepository);
        transferService.setUserStorage(userStorage);
        var reportService = new ReportService(walletService, budgetService);

        var app = new ConsoleApp(
                authService,
                walletService,
                budgetService,
                notificationService,
                transferService,
                reportService
        );
        app.run();
    }
}
