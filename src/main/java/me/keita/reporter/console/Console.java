package me.keita.reporter.console;

import me.keita.reporter.service.BankService;
import me.keita.reporter.model.Account;
import java.util.List;
import java.util.Scanner;

/** Точка входа с выбором по имени владельца (авто-ID). */
public class Console {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        BankService service = new BankService();
        while (true) {
            System.out.println("\n=== SmartBank Reporter (Аккаунты) ===");
            System.out.println("1. Добавить аккаунт");
            System.out.println("2. Выбрать/создать по имени владельца");
            System.out.println("3. Список аккаунтов и выбор");
            System.out.println("4. Выход");
            System.out.print("Выбор: ");
            String s = in.nextLine().trim();
            switch (s) {
                case "1" -> {
                    System.out.print("Имя владельца: ");
                    String owner = in.nextLine().trim();
                    service.setCurrentAccount(owner);
                    System.out.println("Аккаунт выбран: " + service.getCurrentAccount() + " (владелец: " + owner + ")");
                    new Menu(service).show();
                }
                case "2" -> {
                    System.out.print("Имя владельца: ");
                    String owner = in.nextLine().trim();
                    var found = service.findAccountByOwner(owner);
                    if (found == null) {
                        System.out.print("Аккаунт не найден. Создать? (yes/no): ");
                        String conf = in.nextLine().trim().toLowerCase();
                        if (!conf.equals("yes")) { System.out.println("Отмена."); break; }
                        var acc = service.ensureAccountForOwner(owner);
                        System.out.println("Аккаунт создан: " + acc.getAccountNumber());
                    } else {
                        service.selectAccount(found.getAccountNumber());
                        System.out.println("Выбран аккаунт: " + found.getAccountNumber() + " (" + found.getOwnerName() + ")");
                    }
                    new Menu(service).show();
                }
                case "3" -> {
                    List<Account> list = service.listAccounts();
                    if (list.isEmpty()) { System.out.println("Список пуст."); break; }
                    System.out.println("Доступные аккаунты:");
                    for (int i = 0; i < list.size(); i++) {
                        System.out.printf("%d) %s (%s)%n", i + 1, list.get(i).getAccountNumber(), list.get(i).getOwnerName());
                    }
                    System.out.print("Введите номер в списке: ");
                    try {
                        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
                        if (idx < 0 || idx >= list.size()) { System.out.println("Неверный выбор."); break; }
                        String acc = list.get(idx).getAccountNumber();
                        boolean ok = service.selectAccount(acc);
                        if (!ok) { System.out.println("Не удалось выбрать аккаунт."); break; }
                        System.out.println("Выбран: " + acc);
                        new Menu(service).show();
                    } catch (Exception e) {
                        System.out.println("Неверный ввод.");
                    }
                }
                case "4" -> { return; }
                default -> System.out.println("Нет такого пункта.");
            }
        }
    }
}
