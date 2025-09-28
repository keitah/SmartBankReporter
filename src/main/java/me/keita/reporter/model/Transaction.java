package me.keita.reporter.model;

import me.keita.reporter.orm.Table;
import me.keita.reporter.orm.Column;
import me.keita.reporter.orm.Id;

@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "id")
    private final String id;

    @Column(name = "amount")
    private final double amount;

    @Column(name = "description")
    private final String description;

    @Column(name = "type")
    private final TransactionType type;

    public Transaction(String id, double amount, String description, TransactionType type) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.type = type;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public TransactionType getType() { return type; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }
}
