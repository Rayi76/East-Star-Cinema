package com.theater.main;

import com.theater.utils.DatabaseHelper;
import javafx.application.Application;
import com.theater.TheaterApplication;

/**
 * 應用程式進入點：初始化資料庫並啟動 JavaFX
 */
public class Main {
    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        Application.launch(TheaterApplication.class, args);
    }
}
