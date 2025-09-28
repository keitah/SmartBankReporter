package me.keita.reporter.dao;

import me.keita.reporter.model.Transaction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvDAO {
    public void save(List<Transaction> transactions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.csv"))) {
            bw.write("id,amount,description,type\n");
            for (Transaction t : transactions) {
                bw.write(t.getId() + "," + t.getAmount() + "," + escape(t.getDescription()) + "," + t.getType() + "\n");
            }
            System.out.println("Экспортировано в transactions.csv");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи CSV: " + e.getMessage(), e);
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        String q = s.replace("\"", "\"\""); // удваиваем кавычки
        if (q.contains(",") || q.contains("\"") || q.contains("\n")) {
            return "\"" + q + "\""; // оборачиваем в кавычки
        }
        return q;
    }
}
