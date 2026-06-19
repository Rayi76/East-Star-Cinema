module com.theater {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.theater to javafx.fxml;
    opens com.theater.controller to javafx.fxml;

    exports com.theater;
    exports com.theater.controller;
    exports com.theater.model;
    exports com.theater.utils;
    exports com.theater.main;
}
