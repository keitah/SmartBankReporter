package me.keita.reporter.repository;

import me.keita.reporter.dao.DAO;
import me.keita.reporter.dao.ResultSetHandler;
import me.keita.reporter.model.Account;
import me.keita.reporter.orm.OrmAnnotationHandler;

import java.util.List;

public class AccountRepository {
    private final DAO dao;
    public AccountRepository(DAO dao) { this.dao = dao; }

    private static final ResultSetHandler<Account> MAPPER = rs ->
            new Account(rs.getString("account_number"), rs.getString("owner_name"), rs.getTimestamp("created_at").toInstant());

    public List<Account> findAll() {
        return dao.query("SELECT account_number, owner_name, created_at FROM accounts ORDER BY created_at DESC", MAPPER);
    }

    public Account findByAccountNumber(String num) {
        return dao.queryOne("SELECT account_number, owner_name, created_at FROM accounts WHERE account_number = ?", MAPPER, num);
    }

    public Account findByOwnerName(String owner) {
        return dao.queryOne("SELECT account_number, owner_name, created_at FROM accounts WHERE owner_name = ? LIMIT 1", MAPPER, owner);
    }

    public boolean deleteByAccountNumber(String num) {
        return dao.update("DELETE FROM accounts WHERE account_number = ?", num) > 0;
    }

    public boolean save(Account account) {
        OrmAnnotationHandler orm = new OrmAnnotationHandler();
        String sql = orm.buildInsertSql(account);
        return dao.update(sql, orm.buildComposer(account)) > 0;
    }

    public boolean update(Account account) {
        OrmAnnotationHandler orm = new OrmAnnotationHandler();
        String sql = orm.buildUpdateSql(account, "account_number");
        return dao.update(sql, orm.buildComposer(account)) > 0;
    }

    public boolean deleteById(String accountNumber) {
        OrmAnnotationHandler orm = new OrmAnnotationHandler();
        String sql = orm.buildDeleteSql(Account.class, "account_number");
        return dao.update(sql, accountNumber) > 0;
    }

    public Account findById(String accountNumber) {
        OrmAnnotationHandler orm = new OrmAnnotationHandler();
        String sql = orm.buildSelectByIdSql(Account.class, "account_number");
        return dao.queryOne(sql, MAPPER, accountNumber);
    }
}
