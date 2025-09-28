package me.keita.reporter.service;

import me.keita.reporter.dao.DAO;
import me.keita.reporter.model.Account;
import me.keita.reporter.model.Transaction;
import me.keita.reporter.model.TransactionType;
import me.keita.reporter.repository.AccountRepository;
import me.keita.reporter.repository.TransactionRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Сервисный слой: логика аккаунтов и транзакций. */
public class BankService {

    private final DAO dao = new DAO();
    private final AccountRepository accounts = new AccountRepository(dao);
    private final TransactionRepository transactions = new TransactionRepository(dao);

    private String currentAccount; // account_number

    // Аккаунты
    public List<Account> getAllAccounts() { return accounts.findAll(); }
    public List<Account> listAccounts() { return getAllAccounts(); }

    public boolean addAccount(String accountNumber, String ownerName) {
        Account existing = accounts.findByAccountNumber(accountNumber);
        Account acc = new Account(accountNumber, ownerName, Instant.now());
        return (existing == null) ? accounts.save(acc) : accounts.update(acc);
    }

    public boolean selectAccount(String accountNumber) {
        Account acc = accounts.findByAccountNumber(accountNumber);
        if (acc == null) return false;
        currentAccount = accountNumber;
        return true;
    }

    public String getCurrentAccount() { return currentAccount; }

    public boolean deleteAccount(String accountNumber) { return accounts.deleteByAccountNumber(accountNumber); }

    // Транзакции
    public boolean addTransaction(double amount, String description, TransactionType type) {
        if (currentAccount == null) return false;
        String id = UUID.randomUUID().toString();
        Transaction tx = new Transaction(id, amount, description, type);
        return transactions.save(tx, currentAccount);
    }

    public List<Transaction> getTransactions() {
        if (currentAccount == null) return java.util.Collections.emptyList();
        return transactions.findAllByAccount(currentAccount);
    }

    public boolean deleteTransaction(String id) { return transactions.deleteById(id); }

    public Transaction getTransaction(String id) { return transactions.findById(id); }

    public double computeBalance() {
        return getTransactions().stream().mapToDouble(t -> t.getType() == TransactionType.DEPOSIT ? t.getAmount() : -t.getAmount()).sum();
    }

    // Совместимость с меню
    public Account findAccountByNumber(String num) { return accounts.findByAccountNumber(num); }

    public Account findAccountByOwner(String owner) { return accounts.findByOwnerName(owner); }

    private String generateAccountNumber() {
        String hex = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ACC-" + hex;
    }

    public Account ensureAccountForOwner(String owner) {
        Account ex = accounts.findByOwnerName(owner);
        if (ex != null) { selectAccount(ex.getAccountNumber()); return ex; }
        String num = generateAccountNumber();
        Account a = new Account(num, owner, Instant.now());
        accounts.save(a);
        selectAccount(num);
        return a;
    }

    public void setCurrentAccount(String accountNumber, String ownerNameIfNew) {
        addAccount(accountNumber, ownerNameIfNew);
        selectAccount(accountNumber);
    }

    public void setCurrentAccount(String ownerName) { ensureAccountForOwner(ownerName); }

    public List<Transaction> getAll() { return getTransactions(); }

    public boolean add(TransactionType type, double amount, String description) {
        if (currentAccount == null || amount <= 0) return false;
        return addTransaction(amount, description, type);
    }

    public boolean removeById(String id) { return deleteTransaction(id); }

    public Transaction findTransactionById(String id) { return getTransaction(id); }

    public double getBalance() { return computeBalance(); }
}
