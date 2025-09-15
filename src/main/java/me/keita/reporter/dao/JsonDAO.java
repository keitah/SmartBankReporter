package me.keita.reporter.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.keita.reporter.model.Transaction;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonDAO {
    public void save(List<Transaction> transactions) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("transactions.json")) {
            gson.toJson(transactions, writer);
            System.out.println("Экспортировано в transactions.json");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи JSON: " + e.getMessage(), e);
        }
    }
}
