package library.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import library.dao.MemberDao;
import library.model.Member;

import java.sql.SQLException;

public class LoginMemberRegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea addressArea;

    private final MemberDao memberDao = new MemberDao();

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty() ||
                surname.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty()) {

            showAlert(Alert.AlertType.ERROR,
                    "Validation error",
                    "Please fill in all required fields.\n(Home address can be left empty.)");
            return;
        }

        try {
            if (memberDao.existsByUsername(username)) {
                showAlert(Alert.AlertType.ERROR,
                        "Registration error",
                        "This username is already taken. Please choose another one.");
                return;
            }

            Member member = new Member();
            member.setName(name);
            member.setSurname(surname);
            member.setUsername(username);
            member.setPassword(password);
            member.setPhone(phone);
            member.setEmail(email);
            member.setHomeAddress(address);

            memberDao.createMember(member);

            showAlert(Alert.AlertType.INFORMATION,
                    "Success",
                    "Account has been created successfully.");

            closeWindow(event);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Database error",
                    "An error occurred while saving the account:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
