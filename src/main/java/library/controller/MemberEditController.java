package library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import library.dao.MemberDao;
import library.model.Member;

import java.sql.SQLException;

public class MemberEditController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea addressArea;

    private final MemberDao memberDao = new MemberDao();
    private Member member;

    public void setMember(Member member) {
        this.member = member;
        nameField.setText(member.getName());
        surnameField.setText(member.getSurname());
        usernameField.setText(member.getUsername());
        phoneField.setText(member.getPhone());
        emailField.setText(member.getEmail());
        addressArea.setText(member.getHomeAddress());
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (member == null) {
            closeWindow(event);
            return;
        }

        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty() || surname.isEmpty() || username.isEmpty()
                || phone.isEmpty() || email.isEmpty()) {
            showError("Validation error", "Name, surname, username, phone, email cannot be left blank..");
            return;
        }

        try {
            member.setName(name);
            member.setSurname(surname);
            member.setUsername(username);
            member.setPhone(phone);
            member.setEmail(email);
            member.setHomeAddress(address);

            memberDao.updateMember(member);
            showInfo("Success", "Member updated successfully.");
            closeWindow(event);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (member == null) {
            closeWindow(event);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are your sure you want to delete this member?");
        var result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            memberDao.deleteMember(member.getId());
            showInfo("Deleted", "Member has been deleted.");
            closeWindow(event);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", e.getMessage());
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

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}
