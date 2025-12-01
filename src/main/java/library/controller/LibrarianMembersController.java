package library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import library.dao.MemberDao;
import library.model.Member;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LibrarianMembersController {

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> surnameColumn;

    @FXML
    private TableColumn<Member, String> usernameColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, String> emailColumn;

    @FXML
    private TableColumn<Member, String> addressColumn;

    @FXML
    private TextField searchField;

    private final MemberDao memberDao = new MemberDao();
    private final ObservableList<Member> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("homeAddress"));

        loadAllMembers();

        membersTable.setRowFactory(tv -> {
            TableRow<Member> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()
                        && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {
                    Member clickedMember = row.getItem();
                    openMemberDetail(clickedMember);
                }
            });
            return row;
        });
    }

    private void loadAllMembers() {
        try {
            List<Member> members = memberDao.getAllMembers();
            memberList.setAll(members);
            membersTable.setItems(memberList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading members", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllMembers();
            return;
        }

        try {
            List<Member> result = memberDao.searchMembers(keyword);
            memberList.setAll(result);
            membersTable.setItems(memberList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Search error", e.getMessage());
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadAllMembers();
    }

    @FXML
    private void handleEditSelected() {
        Member selected = membersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Please select a member from table");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/member_edit.fxml"));
            Parent root = loader.load();

            MemberEditController controller = loader.getController();
            controller.setMember(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadAllMembers();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "The edit window did not open:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteSelected() {
        Member selected = membersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Please select a member from table");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this member?");
        var result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            memberDao.deleteMember(selected.getId());
            loadAllMembers();
            showInfo("Deleted", "Member has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", "An error occurred while deleting the member.:\n" + e.getMessage());
        }
    }

    private void openMemberDetail(Member member) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/member_detail.fxml"));
            Parent root = loader.load();

            MemberDetailController controller = loader.getController();
            controller.setMember(member);

            Stage stage = new Stage();
            stage.setTitle("Member details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "The detail window could not be opened.:\n" + e.getMessage());
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

