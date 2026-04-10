package org.example;

/**
 * Точка входа приложения: выводит микро-резюме в консоль.
 */
public class Main {

    /**
     * Запуск приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Resume resume = new Resume();
        System.out.println(resume.shortSummary());
    }
}
