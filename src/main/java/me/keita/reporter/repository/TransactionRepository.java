package me.keita.reporter.repository;

import me.keita.reporter.dao.DAO;
import me.keita.reporter.dao.ResultSetHandler;
import me.keita.reporter.dao.PreparedStatementComposer;
import me.keita.reporter.model.Transaction;
import me.keita.reporter.model.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TransactionRepository {
    private final DAO dao;

    public TransactionRepository(DAO dao) { this.dao = dao; }

    private static final ResultSetHandler<Transaction> MAPPER = rs ->
            new Transaction(
                    rs.getString("id"),
                    rs.getDouble("amount"),
                    rs.getString("description"),
                    TransactionType.valueOf(rs.getString("type"))
            );

    public List<Transaction> findAll() {
        return dao.query("SELECT id, amount, description, type FROM transactions ORDER BY created_at DESC NULLS LAST, id", MAPPER);
    }

    /** Requirement 5) findById for transaction */
    public Transaction findById(String id) {
        return dao.queryOne("SELECT id, amount, description, type FROM transactions WHERE id = ?", MAPPER, id);
    }

    public void save(Transaction t, String accountNumber) {
        String sql = "INSERT INTO transactions(id, account_number, amount, description, type) VALUES (?, ?, ?, ?, ?)";
        dao.update(sql, (PreparedStatementComposer) ps -> {
            ps.setString(1, t.getId());
            ps.setString(2, accountNumber);
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(t.getAmount()));
            ps.setString(4, t.getDescription());
            ps.setString(5, t.getType().name());
        });
    }

    public boolean deleteById(String id) {
        return dao.update("DELETE FROM transactions WHERE id = ?", id) > 0;
    }

/** Return all transactions for given account_number. */
public List<Transaction> findAllByAccount(String accountNumber) {
    return dao.query("SELECT id, amount, description, type FROM transactions WHERE account_number = ? ORDER BY created_at DESC NULLS LAST, id",
            MAPPER, accountNumber);
}

}
