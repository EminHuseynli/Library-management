package library.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import library.dao.BookDao;
import library.dao.LoanDao;
import library.model.Book;
import library.model.Loan;
import library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemberBooksController {

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
    private TableColumn<Book, String> availabilityColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Label selectedBookLabel;

    @FXML
    private DatePicker loanDatePicker;

    @FXML
    private Label dueDateLabel;

    private final BookDao bookDao = new BookDao();
    private final LoanDao loanDao = new LoanDao();

    private final ObservableList<Book> bookList = FXCollections.observableArrayList();

    private final Map<Integer, String> availabilityMap = new HashMap<>();

    private Member loggedInMember;

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Availability custom factory
        availabilityColumn.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            String status = availabilityMap.getOrDefault(book.getId(), "Unknown");
            return new SimpleStringProperty(status);
        });

        booksTable.setItems(bookList);

        booksTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateSelectedBookLabel(newVal)
        );

        // Loan date default = bugün
        loanDatePicker.setValue(LocalDate.now());
        updateDueDateLabel();
        loanDatePicker.valueProperty().addListener(
                (obs, oldVal, newVal) -> updateDueDateLabel()
        );

        loadAllBooks();
    }

    public void setLoggedInMember(Member member) {
        this.loggedInMember = member;
    }

    private void loadAllBooks() {
        try {
            List<Book> books = bookDao.getAllBooks();
            recomputeAvailability(books);
            bookList.setAll(books);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading books", e.getMessage());
        }
    }

    private void recomputeAvailability(List<Book> books) throws SQLException {
        availabilityMap.clear();
        for (Book book : books) {
            int activeLoans = loanDao.countActiveLoansForBook(book.getId());
            int availableCopies = book.getCopyCount() - activeLoans;
            String status = (availableCopies > 0) ? "Available" : "Not available";
            availabilityMap.put(book.getId(), status);
        }
    }

    private void updateSelectedBookLabel(Book book) {
        if (book == null) {
            selectedBookLabel.setText("-");
        } else {
            selectedBookLabel.setText(
                    book.getTitle() + " - " + book.getAuthor()
            );
        }
    }

    private void updateDueDateLabel() {
        LocalDate loanDate = loanDatePicker.getValue();
        if (loanDate == null) {
            dueDateLabel.setText("-");
        } else {
            LocalDate dueDate = loanDate.plusDays(14);
            dueDateLabel.setText(dueDate.toString());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllBooks();
            return;
        }

        try {
            List<Book> result = bookDao.searchBooks(keyword);
            recomputeAvailability(result);
            bookList.setAll(result);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Search error", e.getMessage());
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadAllBooks();
    }

    @FXML
    private void handleBorrowBook() {
        if (loggedInMember == null) {
            showError("Error", "No information was found for the logged-in member..");
            return;
        }

        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Validation error", "Please select book from table.");
            return;
        }

        String status = availabilityMap.getOrDefault(selected.getId(), "Not available");
        if (!"Available".equalsIgnoreCase(status)) {
            showError("Not available",
                    "This book is currently unavailable (all copies are on loan)..");
            return;
        }

        LocalDate loanDate = loanDatePicker.getValue();
        if (loanDate == null) {
            showError("Validation error", "Please select a loan date..");
            return;
        }

        LocalDate dueDate = loanDate.plusDays(14);

        try {
            Loan loan = new Loan(selected.getId(), loggedInMember.getId(), loanDate, dueDate);
            loanDao.insertLoan(loan);

            showInfo("Success", "The book has been successfully borrowed..");
            loadAllBooks();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", "An error occurred while creating the loan record.:\n" + e.getMessage());
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
