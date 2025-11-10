package homework.financeapp.repository;

import homework.financeapp.model.User;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

    @Override
    public void save(User user) {
        users.put(user.getLogin(), user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
