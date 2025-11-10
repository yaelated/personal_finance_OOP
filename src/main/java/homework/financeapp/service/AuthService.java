package homework.financeapp.service;

import homework.financeapp.model.User;
import homework.financeapp.persistence.UserStorage;
import homework.financeapp.repository.UserRepository;
import homework.financeapp.validation.ValidationException;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;
    private final UserStorage userStorage;
    private User currentUser;

    public AuthService(UserRepository userRepository, UserStorage userStorage) {
        this.userRepository = userRepository;
        this.userStorage = userStorage;
    }

    public void register(String login, String password) {
        if (login == null || login.isBlank())
            throw new ValidationException("Логин не может быть пустым");
        if (password == null || password.isBlank())
            throw new ValidationException("Пароль не может быть пустым");
        if (userRepository.findByLogin(login).isPresent())
            throw new ValidationException("Пользователь уже существует");

        String passHash = hash(password);
        User user = new User(login, passHash);
        userRepository.save(user);
        userStorage.save(user);
        this.currentUser = user;
    }

    public void login(String login, String password) {
        Optional<User> fromFile = userStorage.load(login);
        User user = fromFile.orElseGet(() ->
                userRepository.findByLogin(login)
                        .orElseThrow(() -> new ValidationException("Пользователь не найден"))
        );
        if (!user.getPasswordHash().equals(hash(password))) {
            throw new ValidationException("Неверный пароль");
        }
        userRepository.save(user);
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUserOrThrow() {
        if (currentUser == null)
            throw new ValidationException("Пользователь не авторизован");
        return currentUser;
    }

    public void persistCurrentUser() {
        if (currentUser != null) {
            userStorage.save(currentUser);
        }
    }

    private String hash(String password) {
        return Integer.toHexString(password.hashCode());
    }
}
