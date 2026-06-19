package com.theater.controller;

import com.theater.model.User;
import com.theater.model.UserDAO;
import com.theater.model.UserDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;
    @FXML private PasswordField confirmPasswordInput;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAOImpl();

    @FXML
    public void registerAction(ActionEvent event) {
        messageLabel.setVisible(false);
        String username = usernameInput.getText().trim();
        String password = passwordInput.getText();
        String confirm = confirmPasswordInput.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("請填寫完整資料");
            return;
        }
        if (!password.equals(confirm)) {
            showMessage("兩次密碼不一致");
            return;
        }
        User user = new User(username, password);
        if (userDAO.registerUser(user)) {
            AppController.switchScene(event, "/com/theater/login.fxml", "Theater - 登入");
        } else {
            showMessage("註冊失敗，帳號可能已存在");
        }
    }

    @FXML
    public void backToLoginAction(ActionEvent event) {
        AppController.switchScene(event, "/com/theater/login.fxml", "Theater - 登入");
    }

    private void showMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
    }
}
