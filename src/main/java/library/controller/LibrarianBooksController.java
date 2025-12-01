package library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import library.dao.BookDao;
import library.model.Book;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LibrarianBooksController {

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> isbnColumn;

    @FXML
    private TableColumn<Book, String> categoryColumn;

    @FXML
    private TableColumn<Book, Integer> copyCountColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField newTitleField;

    @FXML
    private TextField newAuthorField;

    @FXML
    private TextField newIsbnField;

    @FXML
    private TextField newCategoryField;

    @FXML
    private TextField newCopyCountField;

    private final BookDao bookDao = new BookDao();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        copyCountColumn.setCellValueFactory(new PropertyValueFactory<>("copyCount"));

        loadAllBooks();
    }

    private void loadAllBooks() {
        try {
            List<Book> books = bookDao.getAllBooks();
            bookList.setAll(books);
            booksTable.setItems(bookList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading books", e.getMessage());
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadAllBooks();
            return;
        }

        try {
            List<Book> result = bookDao.searchBooks(keyword);
            bookList.setAll(result);
            booksTable.setItems(bookList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Search error", e.getMessage());
        }
    }

    @FXML
    private void handleClearSearch(ActionEvent event) {
        searchField.clear();
        loadAllBooks();
    }

    @FXML
    private void handleAddBook(ActionEvent event) {
        String title = newTitleField.getText().trim();
        String author = newAuthorField.getText().trim();
        String isbn = newIsbnField.getText().trim();
        String category = newCategoryField.getText().trim();
        String copyCountText = newCopyCountField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() ||
                category.isEmpty() || copyCountText.isEmpty()) {

            showError("Validation error",
                    "Please fill in all fields (including the copy count).");
            return;
        }

        int copyCount;
        try {
            copyCount = Integer.parseInt(copyCountText);
            if (copyCount < 0) {
                throw new NumberFormatException("negative");
            }
        } catch (NumberFormatException ex) {
            showError("Validation error",
                    "Copy count must be a positive integer.");
            return;
        }

        try {
            Book book = new Book(title, author, isbn, category, copyCount);
            bookDao.insertBook(book);

            loadAllBooks();
            clearNewBookForm();

            showInfo("Success", "Book has been added.");

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error",
                    "An error occurred while adding the book.:\n" + e.getMessage());
        }
    }

    private void clearNewBookForm() {
        newTitleField.clear();
        newAuthorField.clear();
        newIsbnField.clear();
        newCategoryField.clear();
        newCopyCountField.clear();
    }

    @FXML
    private void handleEditSelectedBook(ActionEvent event) {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Please select a book from the table..");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/librarian_book_edit.fxml"));
            Parent root = loader.load();

            LibrarianBookEditController controller = loader.getController();
            controller.setBook(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadAllBooks();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "The edit window could not be opened.\n" + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

