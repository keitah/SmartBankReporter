package me.keita.reporter.console;

import me.keita.reporter.service.BankService;
import me.keita.reporter.model.Account;
import java.util.List;
import java.util.Scanner;

/** Entry-point with list-based selection and create-if-not-found search. */
public class Console {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        BankService service = new BankService();
        while (true) {
            System.out.println("\n=== SmartBank Reporter (Accounts) ===");
            System.out.println("1. Добавить аккаунт");
            System.out.println("2. Выбрать/создать по номеру");
            System.out.println("3. Список аккаунтов и выбор");
            System.out.println("4. Выход");
            System.out.print("Выбор: ");
            String s = in.nextLine().trim();
            switch (s) {
                case "1" -> {
                    System.out.print("Номер аккаунта: ");
                    String acc = in.nextLine().trim();
                    System.out.print("Имя владельца: ");
                    String owner = in.nextLine().trim();
                    service.setCurrentAccount(acc, owner); // совместимость: создаёт/обновляет и выбирает
                    System.out.println("Аккаунт создан/обновлён. Выбран: " + acc);
                    new Menu(service).show();
                }
                case "2" -> {
                    System.out.print("Номер аккаунта: ");
                    String acc = in.nextLine().trim();
                    var found = service.findAccountByNumber(acc);
                    if (found == null) {
                        System.out.println("Аккаунт не найден. Создать? (yes/no): ");
                        String conf = in.nextLine().trim().toLowerCase();
                        if (!conf.equals("yes")) {
                            System.out.println("Отмена.");
                            break;
                        }
                        System.out.print("Имя владельца: ");
                        String owner = in.nextLine().trim();
                        service.setCurrentAccount(acc, owner);
                        System.out.println("Аккаунт создан. Выбран: " + acc);
                    } else {
                        service.selectAccount(acc);
                        System.out.println("Выбран аккаунт: " + acc + " (" + found.getOwnerName() + ")");
                    }
                    new Menu(service).show();
                }
                case "3" -> {
                    List<Account> list = service.listAccounts();
                    if (list.isEmpty()) {
                        System.out.println("Список пуст. Добавьте аккаунт.");
                        break;
                    }
                    System.out.println("Доступные аккаунты:");
                    for (int i = 0; i < list.size(); i++) {
                        System.out.printf("%d) %s (%s)%n", i + 1, list.get(i).getAccountNumber(), list.get(i).getOwnerName());
                    }
                    System.out.print("Введите номер в списке: ");
                    try {
                        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
                        if (idx < 0 || idx >= list.size()) {
                            System.out.println("Неверный выбор.");
                            break;
                        }
                        String acc = list.get(idx).getAccountNumber();
                        boolean ok = service.selectAccount(acc);
                        if (!ok) {
                            System.out.println("Не удалось выбрать аккаунт.");
                            break;
                        }
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
