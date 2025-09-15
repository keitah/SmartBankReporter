package me.keita.reporter.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Single entry point to the DB: query/execute using SQL + small functional helpers. */
public class DAO {
    private final String url;
    private final String user;
    private final String password;

    public DAO() {
        this.url = getenvOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/smartbank");
        this.user = getenvOrDefault("DB_USER", "reporter");
        this.password = getenvOrDefault("DB_PASSWORD", "reporter");
    }

    private static String getenvOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    private Connection conn() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public <T> List<T> query(String sql, ResultSetHandler<T> mapper, Object... params) {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) out.add(mapper.handle(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB query failed: " + e.getMessage(), e);
        }
    }

    public <T> T queryOne(String sql, ResultSetHandler<T> mapper, Object... params) {
        List<T> list = query(sql, mapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    public int update(String sql, Object... params) {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB update failed: " + e.getMessage(), e);
        }
    }

    public int update(String sql, PreparedStatementComposer composer) {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            composer.compose(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB update failed: " + e.getMessage(), e);
        }
    }

    private void bind(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
