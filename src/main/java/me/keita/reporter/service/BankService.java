package me.keita.reporter.service;

import me.keita.reporter.dao.DAO;
import me.keita.reporter.model.Account;
import me.keita.reporter.model.Transaction;
import me.keita.reporter.model.TransactionType;
import me.keita.reporter.repository.AccountRepository;
import me.keita.reporter.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Simple service layer coordinating repositories. */
public class BankService {
    private final DAO dao = new DAO();
    private final TransactionRepository trxRepo = new TransactionRepository(dao);
    private final AccountRepository accRepo = new AccountRepository(dao);

    private final List<Transaction> transactions = new ArrayList<>();
    private String currentAccount = null;

    public BankService() {
        // Нет автоматического аккаунта: сначала пользователь выбирает или создаёт
    }

    public List<Account> listAccounts() {
        return accRepo.findAll();
    }

    public boolean addAccount(String accountNumber, String ownerName) {
        if (accountNumber == null || accountNumber.isBlank() || ownerName == null || ownerName.isBlank()) return false;
        accRepo.save(new Account(accountNumber, ownerName, java.time.Instant.now()));
        return true;
    }

    public boolean deleteAccount(String accountNumber) {
        boolean ok = accRepo.deleteByAccountNumber(accountNumber);
        if (ok && accountNumber.equals(currentAccount)) {
            currentAccount = null;
            transactions.clear();
        }
        return ok;
    }

    public boolean selectAccount(String accountNumber) {
    var acc = accRepo.findByAccountNumber(accountNumber);
    if (acc == null) return false;
    this.currentAccount = accountNumber;
    transactions.clear();
    transactions.addAll(trxRepo.findAllByAccount(accountNumber));
    return true;
}


    public String getCurrentAccount() { return currentAccount; }

    public boolean add(TransactionType type, double amount, String description) {
        if (currentAccount == null) return false;
        if (amount <= 0) return false;
        Transaction t = new Transaction(UUID.randomUUID().toString(), amount, description, type);
        trxRepo.save(t, currentAccount);
        transactions.add(0, t);
        return true;
    }

    public boolean removeById(String id) {
        if (currentAccount == null) return false;
        boolean removedDb = trxRepo.deleteById(id);
        if (removedDb) {
            return transactions.removeIf(t -> t.getId().equals(id));
        }
        return false;
    }

    public Transaction findTransactionById(String id) {
        return trxRepo.findById(id);
    }

    public Account findAccountByNumber(String number) {
        return accRepo.findByAccountNumber(number);
    }

    public List<Transaction> getAll() { return new ArrayList<>(transactions); }

    public double getBalance() {
        double sum = 0.0;
        for (Transaction t : transactions) {
            sum += (t.getType() == TransactionType.DEPOSIT ? t.getAmount() : -t.getAmount());
        }
        return sum;
    }

/** 
 * Legacy compatibility: create or update account and select it. 
 */
public void setCurrentAccount(String accountNumber, String ownerNameIfNew) {
    addAccount(accountNumber, ownerNameIfNew);
    selectAccount(accountNumber);
}

}
