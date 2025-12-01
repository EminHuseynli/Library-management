package library.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import library.dao.MemberDao;
import library.model.Member;

import java.sql.SQLException;

public class MemberMainController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea addressArea;

    @FXML
    private MemberBooksController memberBooksController;

    @FXML
    private MemberMyBooksController memberMyBooksController;


    private final MemberDao memberDao = new MemberDao();
    private Member loggedInMember;

    @FXML
    public void initialize() {
    }


    public void setLoggedInMember(Member member) {
        this.loggedInMember = member;
        if (member != null) {
            nameLabel.setText(member.getName());
            surnameLabel.setText(member.getSurname());
            usernameLabel.setText(member.getUsername());

            phoneField.setText(member.getPhone());
            emailField.setText(member.getEmail());
            addressArea.setText(
                    member.getHomeAddress() != null ? member.getHomeAddress() : ""
            );

            if (memberBooksController != null) {
                memberBooksController.setLoggedInMember(member);
            } else {
                System.out.println("memberBooksController NULL, fx:include/fx:id control.");
            }


            if (memberMyBooksController != null) {
                memberMyBooksController.setLoggedInMember(member);
            } else {
                System.out.println("memberBooksController NULL, fx:include/fx:id control.");
            }
        }
    }

    @FXML
    private void handleUpdateProfile() {
        if (loggedInMember == null) {
            showError("Error", "No logged in member info.");
            return;
        }

        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();

        if (phone.isEmpty() || email.isEmpty()) {
            showError("Validation error",
                    "Phone ve email cannot be left blank..");
            return;
        }

        loggedInMember.setPhone(phone);
        loggedInMember.setEmail(email);
        loggedInMember.setHomeAddress(address);

        try {
            memberDao.updateMember(loggedInMember);
            showInfo("Success", "Profile updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setTitle(title);
        a.setContentText(message);
        a.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setTitle(title);
        a.setContentText(message);
        a.showAndWait();
    }
}
