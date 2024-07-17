package com.example;

import com.example.domain.DatabaseManager;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();

        try {
            dbManager.connect();

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.println("Меню:");
                System.out.println("1. Создание таблиц");
                System.out.println("2. Заполнение таблиц");
                System.out.println("3. Вывод информации из таблиц");
                System.out.println("4. Удаление таблиц");


                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        dbManager.createTables();
                        break;
                    case 2:
                        dbManager.fillTables();
                        break;
                    case 3:
                        dbManager.displayTables();
                        break;
                    case 4:
                        dbManager.dropTables();
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Неверный выбор");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                dbManager.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
