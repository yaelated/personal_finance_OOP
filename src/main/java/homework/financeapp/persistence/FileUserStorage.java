package homework.financeapp.persistence;

import homework.financeapp.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileUserStorage implements UserStorage {

    private final Path baseDir;

    public FileUserStorage(Path baseDir) {
        this.baseDir = baseDir;
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удаётся создать директорию для данных", e);
        }
    }

    @Override
    public Optional<User> load(String login) {
        Path file = baseDir.resolve(login + ".ser");
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            User user = (User) ois.readObject();
            return Optional.of(user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void save(User user) {
        Path file = baseDir.resolve(user.getLogin() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
