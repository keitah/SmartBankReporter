package me.keita.reporter.model;

import me.keita.reporter.orm.Table;
import me.keita.reporter.orm.Column;
import me.keita.reporter.orm.Id;

import java.time.Instant;

@Table(name = "accounts")
public class Account {
    @Id
    @Column(name = "account_number")
    private final String accountNumber;

    @Column(name = "owner_name")
    private final String ownerName;

    @Column(name = "created_at")
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
