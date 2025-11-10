package homework.financeapp.service;

import homework.financeapp.model.Transaction;
import homework.financeapp.model.TransactionType;
import homework.financeapp.model.TransferResult;
import homework.financeapp.model.User;
import homework.financeapp.persistence.UserStorage;
import homework.financeapp.repository.UserRepository;

import java.math.BigDecimal;

public class TransferService {

    private final WalletService walletService;
    private final NotificationService notificationService;
    private UserRepository userRepository;
    private UserStorage userStorage;

    public TransferService(WalletService walletService,
                           NotificationService notificationService) {
        this.walletService = walletService;
        this.notificationService = notificationService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setUserStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public TransferResult transfer(User from, String toLogin, BigDecimal amount, String description) {
        if (userRepository == null) {
            return new TransferResult(false, "Репозиторий пользователей не подключен");
        }

        User toUser = userRepository.findByLogin(toLogin).orElse(null);

        if (toUser == null && userStorage != null) {
            var loaded = userStorage.load(toLogin);
            if (loaded.isPresent()) {
                toUser = loaded.get();
                userRepository.save(toUser);
            }
        }

        if (toUser == null) {
            return new TransferResult(false, "Получатель не найден");
        }

        var balance = from.getWallet().getCurrentBalance();
        if (balance.compareTo(amount) < 0) {
            return new TransferResult(false, "Недостаточно средств");
        }

        Transaction outTx = new Transaction(
                TransactionType.TRANSFER_OUT,
                amount,
                "перевод",
                description
        );
        from.getWallet().addTransaction(outTx);

        String incomeCategory = "перевод от " + from.getLogin();
        Transaction inTx = new Transaction(
                TransactionType.TRANSFER_IN,
                amount,
                incomeCategory,
                description == null ? "" : description
        );
        toUser.getWallet().addTransaction(inTx);

        notificationService.notifyIncomingTransfer(toUser.getLogin(), amount, from.getLogin());

        return new TransferResult(true, "Перевод выполнен");
    }
}
