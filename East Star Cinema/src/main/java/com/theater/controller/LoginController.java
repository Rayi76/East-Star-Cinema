package com.theater.controller;

import com.theater.model.AdminDAO;
import com.theater.model.AdminDAOImpl;
import com.theater.model.User;
import com.theater.model.UserDAO;
import com.theater.model.UserDAOImpl;
import com.theater.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField userUsernameInput;
    @FXML private PasswordField userPasswordInput;
    @FXML private Label userErrorLabel;
    @FXML private TextField adminUsernameInput;
    @FXML private PasswordField adminPasswordInput;
    @FXML private Label adminErrorLabel;
    @FXML private Tab userTab;

    private final UserDAO userDAO = new UserDAOImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();

    @FXML
    public void initialize() {
        UserSession.getInstance().logout();
    }

    @FXML
    public void userLoginAction(ActionEvent event) {
        userErrorLabel.setVisible(false);
        String username = userUsernameInput.getText().trim();
        String password = userPasswordInput.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showUserError("請輸入帳號與密碼");
            return;
        }
        int userId = userDAO.login(username, password);
        if (userId == -1) {
            showUserError("帳號或密碼錯誤");
            return;
        }
        User user = userDAO.findUserById(userId);
        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            showUserError("此帳號已被停權");
            return;
        }
        UserSession.getInstance().loginUser(user);
        AppController.switchScene(event, "/com/theater/user_dashboard.fxml",
                "Theater - 會員 " + user.getUsername());
    }

    @FXML
    public void adminLoginAction(ActionEvent event) {
        adminErrorLabel.setVisible(false);
        String username = adminUsernameInput.getText().trim();
        String password = adminPasswordInput.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showAdminError("請輸入帳號與密碼");
            return;
        }
        int adminId = adminDAO.login(username, password);
        if (adminId == -1) {
            showAdminError("管理者帳號或密碼錯誤");
            return;
        }
        UserSession.getInstance().loginAdmin(adminDAO.findAdminById(adminId));
        AppController.switchScene(event, "/com/theater/admin_dashboard.fxml",
                "Theater - 管理後台");
    }

    @FXML
    public void goToRegisterAction(ActionEvent event) {
        AppController.switchScene(event, "/com/theater/register.fxml", "Theater - 註冊新帳號");
    }

    private void showUserError(String msg) {
        userErrorLabel.setText(msg);
        userErrorLabel.setVisible(true);
    }

    private void showAdminError(String msg) {
        adminErrorLabel.setText(msg);
        adminErrorLabel.setVisible(true);
    }
}
