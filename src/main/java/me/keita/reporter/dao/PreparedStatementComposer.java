package me.keita.reporter.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Строит параметры для PreparedStatement перед выполнением. */
@FunctionalInterface
public interface PreparedStatementComposer {
    void compose(PreparedStatement ps) throws SQLException;
}
