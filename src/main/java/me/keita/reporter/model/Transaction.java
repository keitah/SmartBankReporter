package me.keita.reporter.model;

public class Transaction {
    private final String id;
    private final double amount; // signed: DEPOSIT > 0, DEBIT < 0
    private final String description;
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
