package me.keita.reporter.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Builds a PreparedStatement's parameters before execution (e.g., for INSERT/UPDATE). */
@FunctionalInterface
public interface PreparedStatementComposer {
    void compose(PreparedStatement ps) throws SQLException;
}
