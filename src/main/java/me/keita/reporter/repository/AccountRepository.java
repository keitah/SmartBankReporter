package me.keita.reporter.repository;

import me.keita.reporter.dao.DAO;
import me.keita.reporter.dao.PreparedStatementComposer;
import me.keita.reporter.dao.ResultSetHandler;
import me.keita.reporter.model.Account;

import java.util.List;

public class AccountRepository {
    private final DAO dao;
    public AccountRepository(DAO dao) { this.dao = dao; }

    private static final ResultSetHandler<Account> MAPPER = rs ->
            new Account(
                    rs.getString("account_number"),
                    rs.getString("owner_name"),
                    rs.getTimestamp("created_at").toInstant()
            );

    public void save(Account a) {
        String sql = "INSERT INTO accounts(account_number, owner_name) VALUES (?, ?) " +
                     "ON CONFLICT (account_number) DO UPDATE SET owner_name = EXCLUDED.owner_name";
        dao.update(sql, (PreparedStatementComposer) ps -> {
            ps.setString(1, a.getAccountNumber());
            ps.setString(2, a.getOwnerName());
        });
    }

    public List<Account> findAll() {
        return dao.query("SELECT account_number, owner_name, created_at FROM accounts ORDER BY created_at DESC", MAPPER);
    }

    public Account findByAccountNumber(String accountNumber) {
        return dao.queryOne("SELECT account_number, owner_name, created_at FROM accounts WHERE account_number = ?", MAPPER, accountNumber);
    }

    public boolean deleteByAccountNumber(String accountNumber) {
        return dao.update("DELETE FROM accounts WHERE account_number = ?", accountNumber) > 0;
    }
}
