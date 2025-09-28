package me.keita.reporter.orm;

import me.keita.reporter.dao.PreparedStatementComposer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Простой ORM-хелпер для CRUD через аннотации. */
public class OrmAnnotationHandler {

    /** Получить имя таблицы из @Table на классе. */
    public String getTableName(Class<?> clazz) {
        Table ann = clazz.getAnnotation(Table.class);
        if (ann != null) return ann.name();
        throw new RuntimeException("Класс " + clazz.getName() + " не содержит @" + Table.class.getSimpleName());
    }

    /** Поля с @Column (без @Transient) в порядке объявления. */
    public List<Field> getColumnFields(Class<?> clazz) {
        Field[] declared = clazz.getDeclaredFields();
        List<Field> out = new ArrayList<>();
        for (Field f : declared) {
            if (f.getAnnotation(Column.class) != null && f.getAnnotation(Transient.class) == null) {
                f.setAccessible(true);
                out.add(f);
            }
        }
        return out;
    }

    public List<String> getColumnNames(Object obj) {
        return getColumnFields(obj.getClass()).stream()
                .map(f -> f.getAnnotation(Column.class))
                .filter(Objects::nonNull)
                .map(Column::name)
                .collect(Collectors.toList());
    }

    public List<Object> getColumnValues(Object obj) {
        try {
            List<Object> values = new ArrayList<>();
            for (Field f : getColumnFields(obj.getClass())) {
                values.add(f.get(obj));
            }
            return values;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Нет доступа к полям: " + e.getMessage(), e);
        }
    }

    /** INSERT INTO table(col1,col2) VALUES (?,?) */
    public String buildInsertSql(Object obj) {
        String table = getTableName(obj.getClass());
        List<String> cols = getColumnNames(obj);
        if (cols.isEmpty()) throw new RuntimeException("Нет @Column в " + obj.getClass().getName());
        String placeholders = cols.stream().map(c -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + table + " (" + String.join(", ", cols) + ") VALUES (" + placeholders + ")";
    }

    /** Заполнение PreparedStatement в нужном порядке. */
    public PreparedStatementComposer buildComposer(Object obj) {
        List<Object> values = getColumnValues(obj);
        return ps -> {
            for (int i = 0; i < values.size(); i++) {
                Object v = values.get(i);
                int idx = i + 1;
                if (v == null) {
                    ps.setObject(idx, null);
                } else if (v instanceof java.lang.Enum<?> e) {
                    ps.setString(idx, e.name());
                } else if (v instanceof java.time.Instant ins) {
                    ps.setTimestamp(idx, java.sql.Timestamp.from(ins));
                } else {
                    ps.setObject(idx, v);
                }
            }
        };
    }

    /** UPDATE table SET col1=?,col2=? WHERE id=? */
    public String buildUpdateSql(Object obj, String idField) {
        String table = getTableName(obj.getClass());
        List<String> cols = getColumnNames(obj);
        String setClause = cols.stream().map(c -> c + "=?").collect(Collectors.joining(", "));
        return "UPDATE " + table + " SET " + setClause + " WHERE " + idField + "=?";
    }

    public String buildDeleteSql(Class<?> clazz, String idField) {
        String table = getTableName(clazz);
        return "DELETE FROM " + table + " WHERE " + idField + "=?";
    }

    public String buildSelectByIdSql(Class<?> clazz, String idField) {
        List<String> cols = getColumnFields(clazz).stream()
                .map(f -> f.getAnnotation(Column.class)).filter(Objects::nonNull).map(Column::name).collect(Collectors.toList());
        String table = getTableName(clazz);
        return "SELECT " + String.join(", ", cols) + " FROM " + table + " WHERE " + idField + "=?";
    }
}
