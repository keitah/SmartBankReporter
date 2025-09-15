package me.keita.reporter.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Maps a ResultSet row into an object of type T. */
@FunctionalInterface
public interface ResultSetHandler<T> {
    T handle(ResultSet rs) throws SQLException;
}
