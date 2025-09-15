package me.keita.reporter.model;

import java.time.Instant;

public class Account {
    private final String accountNumber;
    private final String ownerName;
    private final Instant createdAt;

    public Account(String accountNumber, String ownerName, Instant createdAt) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
