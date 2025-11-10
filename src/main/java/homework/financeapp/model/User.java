package homework.financeapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private final String login;
    private final String passwordHash;
    private final Wallet wallet;

    public User(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.wallet = new Wallet();
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Wallet getWallet() {
        return wallet;
    }
}

