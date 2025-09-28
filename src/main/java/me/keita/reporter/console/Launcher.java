package me.keita.reporter.console;

import me.keita.reporter.service.BankService;
import me.keita.reporter.model.Account;
import java.util.List;
import java.util.Scanner;

/** Предварительное меню аккаунтов. */
public class Launcher {
    private final Scanner in = new Scanner(System.in);
    private final BankService service = new BankService();

    public void run() {
        while (true) {
            System.out.println("\n==== Аккаунты ====");
            System.out.println("1. Добавить аккаунт");
            System.out.println("2. Выбрать аккаунт");
            System.out.println("3. Выход");
            System.out.print("Выбор: ");
            String s = in.nextLine().trim();
            switch (s) {
                case "1" -> addAccount();
                case "2" -> chooseAccount();
                case "3" -> { return; }
                default -> System.out.println("Нет такого пункта");
            }
        }
    }

    private void addAccount() {
        System.out.print("Имя владельца: ");
        String owner = in.nextLine().trim();
        var acc = service.ensureAccountForOwner(owner);
        System.out.println("Создан/выбран аккаунт: " + acc.getAccountNumber() + " (" + acc.getOwnerName() + ")");
    }

    private void chooseAccount() {
        List<Account> list = service.listAccounts();
        if (list.isEmpty()) { System.out.println("Список пуст. Добавьте аккаунт."); return; }
        System.out.println("Доступные аккаунты:");
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d) %s (%s)%n", i + 1, list.get(i).getAccountNumber(), list.get(i).getOwnerName());
        }
        System.out.print("Введите номер в списке: ");
        try {
            int idx = Integer.parseInt(in.nextLine().trim()) - 1;
            if (idx < 0 || idx >= list.size()) { System.out.println("Неверный выбор."); return; }
            String acc = list.get(idx).getAccountNumber();
            boolean ok = service.selectAccount(acc);
            if (!ok) { System.out.println("Не удалось выбрать аккаунт."); return; }
            new Menu(service).show();
        } catch (Exception e) {
            System.out.println("Неверный ввод.");
        }
    }
}
