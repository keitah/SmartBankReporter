package me.keita.reporter.console;

import me.keita.reporter.dao.CsvDAO;
import me.keita.reporter.dao.JsonDAO;
import me.keita.reporter.model.Transaction;
import me.keita.reporter.model.TransactionType;
import me.keita.reporter.service.BankService;

import java.util.List;
import java.util.Scanner;

/** Меню после авторизации аккаунта. */
public class Menu {
    private final Scanner in = new Scanner(System.in);
    private final BankService service;
    private final CsvDAO csv = new CsvDAO();
    private final JsonDAO json = new JsonDAO();

    public Menu(BankService service) {
        this.service = service;
    }

    public void show() {
        while (true) {
            String acc = service.getCurrentAccount();
            System.out.println("\n==== SmartBank (" + acc + ") ==== ");
            System.out.println("1. Показать все транзакции");
            System.out.println("2. Добавить депозит");
            System.out.println("3. Добавить расход");
            System.out.println("4. Баланс");
            System.out.println("5. Экспорт CSV");
            System.out.println("6. Экспорт JSON");
            System.out.println("7. Найти транзакцию по ID");
            System.out.println("8. Удалить транзакцию по ID");
            System.out.println("9. Удалить аккаунт");
            System.out.println("0. Назад");
            System.out.print("Выбор: ");
            String s = in.nextLine().trim();
            switch (s) {
                case "1" -> printAll();
                case "2" -> create(true);
                case "3" -> create(false);
                case "4" -> System.out.printf("Баланс: %.2f%n", service.getBalance());
                case "5" -> {
                    var list = service.getAll();
                    if (list.isEmpty()) {
                        System.out.println("Нет данных для экспорта.");
                    } else {
                        csv.save(list);
                    }
                }
                case "6" -> {
                    var list = service.getAll();
                    if (list.isEmpty()) {
                        System.out.println("Нет данных для экспорта.");
                    } else {
                        json.save(list);
                    }
                }
                case "7" -> findById();
                case "8" -> deleteTransaction();
                case "9" -> { if (deleteAccount()) return; }
                case "0" -> { return; }
                default -> System.out.println("Нет такого пункта");
            }
        }
    }

    private void printAll() {
        List<Transaction> list = service.getAll();
        if (list.isEmpty()) {
            System.out.println("Пока пусто");
            return;
        }
        for (Transaction t : list) System.out.println(t);
    }

    private void create(boolean deposit) {
        System.out.print("Сумма: ");
        String amtRaw = in.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtRaw.replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод: используйте число (пример: 123.45).");
            return;
        }
        System.out.print("Описание: ");
        String desc = in.nextLine().trim();
        try {
            boolean ok = service.add(deposit ? TransactionType.DEPOSIT : TransactionType.DEBIT, amount, desc);
            System.out.println(ok ? "Добавлено." : "Ошибка: проверьте, что выбран аккаунт и сумма > 0");
        } catch (RuntimeException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    // ПУНКТ 8: удаление с выводом списка, отменой и подтверждением
    private void deleteTransaction() {
        List<Transaction> list = service.getAll();
        if (list.isEmpty()) {
            System.out.println("Пока пусто");
            return; // сразу назад, без ожидания ввода
        }
        for (Transaction t : list) System.out.println(t);
        System.out.print("ID для удаления (пусто/0/q — отмена): ");
        String id = in.nextLine().trim();
        if (id.isEmpty() || id.equals("0") || id.equalsIgnoreCase("q")) {
            System.out.println("Отменено.");
            return;
        }
        var t = service.findTransactionById(id);
        if (t == null) {
            System.out.println("Транзакция не найдена.");
            return;
        }
        System.out.print("Удалить транзакцию " + id + "? (yes/no): ");
        String conf = in.nextLine().trim().toLowerCase();
        if (!conf.equals("yes")) {
            System.out.println("Отмена.");
            return;
        }
        boolean removed = service.removeById(id);
        System.out.println(removed ? "Удалено." : "Не удалось удалить.");
    }

    // ПУНКТ 7: сначала список, затем ввод, есть отмена
    private void findById() {
        List<Transaction> list = service.getAll();
        if (list.isEmpty()) {
            System.out.println("Пока пусто");
            return; // сразу назад
        }
        for (Transaction t : list) System.out.println(t);
        System.out.print("ID (пусто/0/q — отмена): ");
        String id = in.nextLine().trim();
        if (id.isEmpty() || id.equals("0") || id.equalsIgnoreCase("q")) {
            System.out.println("Отменено.");
            return;
        }
        var t = service.findTransactionById(id);
        System.out.println(t == null ? "Не найдено" : t);
    }

    // Для пункта 9: метод реально существует — компилятор больше не ругается
    private boolean deleteAccount() {
        String acc = service.getCurrentAccount();
        if (acc == null) { System.out.println("Аккаунт не выбран."); return false; }
        System.out.print("Точно удалить аккаунт " + acc + "? (yes/no): ");
        String conf = in.nextLine().trim().toLowerCase();
        if (!conf.equals("yes")) { System.out.println("Отменено."); return false; }
        boolean ok = service.deleteAccount(acc);
        System.out.println(ok ? "Аккаунт удалён." : "Не удалось удалить.");
        return ok;
    }
}
