package library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import library.dao.MemberDao;
import library.dao.LibrarianDao;
import library.model.Member;
import library.model.Librarian;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField memberUsernameField;

    @FXML
    private PasswordField memberPasswordField;

    @FXML
    private TextField librarianUsernameField;

    @FXML
    private PasswordField librarianPasswordField;

    private final MemberDao memberDao = new MemberDao();
    private final LibrarianDao librarianDao = new LibrarianDao();

    @FXML
    private void handleMemberLogin(ActionEvent event) {
        String username = memberUsernameField.getText().trim();
        String password = memberPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login error",
                    "Please enter both username and password.");
            return;
        }

        try {
            Member member = memberDao.findByUsernameAndPassword(username, password);
            if (member != null) {
                openMemberMainScreen(event, member);
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Login failed",
                        "Invalid member credentials.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "An error occurred during member login:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleMemberRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Login_Member_Register.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create Member Account");
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Could not open registration window:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleLibrarianLogin(ActionEvent event) {
        String username = librarianUsernameField.getText().trim();
        String password = librarianPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login error",
                    "Please enter both username and password.");
            return;
        }

        try {
            Librarian librarian = librarianDao.findByUsernameAndPassword(username, password);
            if (librarian != null) {
                openLibrarianMainScreen(event, librarian);
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Login failed",
                        "Invalid librarian credentials.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "An error occurred during librarian login:\n" + e.getMessage());
        }
    }

    private void openMemberMainScreen(ActionEvent event, Member member) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/member_main.fxml"));
        Parent root = loader.load();

        MemberMainController controller = loader.getController();
        controller.setLoggedInMember(member);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Member Panel");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openLibrarianMainScreen(ActionEvent event, Librarian librarian) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/librarian_main.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Librarian Panel");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

