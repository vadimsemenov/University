package ru.ifmo.ctddev.semenov.todo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Creator {
    public static void main(String[] args) {
        createDB();
    }

    private static void createDB() {
        try (Connection c1 = DriverManager.getConnection("jdbc:sqlite:tasks.db");
                Statement stmt = c1.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS TASKS" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " TASKLIST       TEXT    NOT NULL, " +
                    " COMPLETE       CHAR    NOT NULL)";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
