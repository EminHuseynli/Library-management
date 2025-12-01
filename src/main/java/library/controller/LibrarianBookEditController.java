package library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import library.dao.BookDao;
import library.model.Book;

import java.sql.SQLException;

public class LibrarianBookEditController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField isbnField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField copyCountField;

    private final BookDao bookDao = new BookDao();
    private Book book;

    public void setBook(Book book) {
        this.book = book;
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        categoryField.setText(book.getCategory());
        copyCountField.setText(String.valueOf(book.getCopyCount()));
    }

    @FXML
    private void handleSaveBook(ActionEvent event) {
        if (book == null) {
            closeWindow(event);
            return;
        }

        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String category = categoryField.getText().trim();
        String copyCountText = copyCountField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()
                || category.isEmpty() || copyCountText.isEmpty()) {
            showError("Validation error", "Please fill in all fields..");
            return;
        }

        int copyCount;
        try {
            copyCount = Integer.parseInt(copyCountText);
            if (copyCount < 0) throw new NumberFormatException("negative");
        } catch (NumberFormatException ex) {
            showError("Validation error", "Copy count must be a positive integer.");
            return;
        }

        try {
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setCategory(category);
            book.setCopyCount(copyCount);

            bookDao.updateBook(book);

            showInfo("Success", "Book updated successfully.");
            closeWindow(event);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", "An error occurred while updating the book:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteBook(ActionEvent event) {
        if (book == null) {
            closeWindow(event);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this book?");
        var result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            bookDao.deleteBook(book.getId());
            showInfo("Deleted", "Book has been deleted.");
            closeWindow(event);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", "An error occurred while deleting the book:\n" + e.getMessage());
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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

