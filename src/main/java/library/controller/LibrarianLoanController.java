package library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import library.dao.BookDao;
import library.dao.LoanDao;
import library.dao.MemberDao;
import library.model.Book;
import library.model.Loan;
import library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LibrarianLoanController {

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, String> memberNameColumn;

    @FXML
    private TableColumn<Member, String> memberSurnameColumn;

    @FXML
    private TableColumn<Member, String> memberUsernameColumn;

    @FXML
    private TableColumn<Member, String> memberPhoneColumn;

    @FXML
    private TextField memberSearchField;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, String> bookTitleColumn;

    @FXML
    private TableColumn<Book, String> bookAuthorColumn;

    @FXML
    private TableColumn<Book, String> bookIsbnColumn;

    @FXML
    private TableColumn<Book, String> bookCategoryColumn;

    @FXML
    private TextField bookSearchField;

    @FXML
    private Label selectedMemberLabel;

    @FXML
    private Label selectedBookLabel;

    @FXML
    private DatePicker loanDatePicker;

    @FXML
    private Label dueDateLabel;

    private final MemberDao memberDao = new MemberDao();
    private final BookDao bookDao = new BookDao();
    private final LoanDao loanDao = new LoanDao();

    private final ObservableList<Member> memberList = FXCollections.observableArrayList();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Member table
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        memberUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        memberPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Book table
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookIsbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        loadAllMembers();
        loadAllBooks();

        membersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateSelectedMemberLabel(newVal)
        );

        booksTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateSelectedBookLabel(newVal)
        );

        loanDatePicker.setValue(LocalDate.now());
        updateDueDateLabel();
        loanDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateDueDateLabel());
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

    private void updateSelectedMemberLabel(Member member) {
        if (member == null) {
            selectedMemberLabel.setText("-");
        } else {
            selectedMemberLabel.setText(
                    member.getName() + " " + member.getSurname() +
                            " (" + member.getUsername() + ")"
            );
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
    private void handleSearchMembers() {
        String keyword = memberSearchField.getText().trim();
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
    private void handleClearMemberSearch() {
        memberSearchField.clear();
        loadAllMembers();
    }

    @FXML
    private void handleSearchBooks() {
        String keyword = bookSearchField.getText().trim();
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
    private void handleClearBookSearch() {
        bookSearchField.clear();
        loadAllBooks();
    }

    @FXML
    private void handleIssueLoan() {
        Member member = membersTable.getSelectionModel().getSelectedItem();
        Book book = booksTable.getSelectionModel().getSelectedItem();

        if (member == null) {
            showError("Validation error", "Please select a member");
            return;
        }
        if (book == null) {
            showError("Validation error", "Please select a book.");
            return;
        }

        LocalDate loanDate = loanDatePicker.getValue();
        if (loanDate == null) {
            showError("Validation error", "Please select a loan date.");
            return;
        }

        LocalDate dueDate = loanDate.plusDays(14);

        try {
            Loan loan = new Loan(book.getId(), member.getId(), loanDate, dueDate);
            loanDao.insertLoan(loan);

            showInfo("Success", "The book has been successfully loaned.");


        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error", "An error occurred while creating the loan record:\n" + e.getMessage());
        }
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

