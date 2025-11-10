package homework.financeapp.persistence;

import homework.financeapp.model.User;

import java.util.Optional;

public interface UserStorage {
    Optional<User> load(String login);
    void save(User user);
}
