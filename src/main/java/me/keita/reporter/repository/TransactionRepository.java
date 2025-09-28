package me.keita.reporter.repository;

import me.keita.reporter.dao.DAO;
import me.keita.reporter.dao.PreparedStatementComposer;
import me.keita.reporter.dao.ResultSetHandler;
import me.keita.reporter.model.Transaction;
import me.keita.reporter.model.TransactionType;
import me.keita.reporter.orm.OrmAnnotationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionRepository {
    private final DAO dao;
    public TransactionRepository(DAO dao) { this.dao = dao; }

    private static final ResultSetHandler<Transaction> MAPPER = rs ->
            new Transaction(rs.getString("id"), rs.getDouble("amount"), rs.getString("description"),
                    TransactionType.valueOf(rs.getString("type")));

    /** Надёжный INSERT без String.replace: строим SQL явно и добавляем account_number как последнюю колонку. */
    public boolean save(Transaction tx, String accountNumber) {
        OrmAnnotationHandler orm = new OrmAnnotationHandler();
        // Колонки транзакции из аннотаций
        List<String> cols = new ArrayList<>(orm.getColumnNames(tx));
        cols.add("account_number");
        String placeholders = String.join(", ", Collections.nCopies(cols.size(), "?"));
        String sql = "INSERT INTO " + orm.getTableName(Transaction.class) +
                " (" + String.join(", ", cols) + ") VALUES (" + placeholders + ")";
        PreparedStatementComposer composer = ps -> {
            orm.buildComposer(tx).compose(ps); // 1..N (4 шт.)
            ps.setObject(cols.size(), accountNumber); // последний параметр
        };
        return dao.update(sql, composer) > 0;
    }

    /** Надёжный UPDATE: SET для всех колонок транзакции + account_number, WHERE id=? */
    public boolean update(Transaction tx, String accountNumber) {
        OrmAnnotationHandler orm = new OrmAnnotationHandler();
        List<String> cols = new ArrayList<>(orm.getColumnNames(tx)); // [id, amount, description, type]
        StringBuilder set = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            if (i > 0) set.append(", ");
            set.append(cols.get(i)).append("=?");
        }
        set.append(", account_number=?");
        String sql = "UPDATE " + orm.getTableName(Transaction.class) + " SET " + set + " WHERE id=?";
        PreparedStatementComposer composer = ps -> {
            orm.buildComposer(tx).compose(ps); // 1..N
            ps.setObject(cols.size() + 1, accountNumber); // после полей транзакции
            ps.setObject(cols.size() + 2, tx.getId());    // последний: id для WHERE
        };
        return dao.update(sql, composer) > 0;
    }

    public boolean deleteById(String id) {
        return dao.update("DELETE FROM transactions WHERE id = ?", id) > 0;
    }

    public List<Transaction> findAllByAccount(String accountNumber) {
        return dao.query("SELECT id, amount, description, type FROM transactions WHERE account_number = ? ORDER BY id", MAPPER, accountNumber);
    }

    public Transaction findById(String id) {
        return dao.queryOne("SELECT id, amount, description, type FROM transactions WHERE id = ?", MAPPER, id);
    }
}
