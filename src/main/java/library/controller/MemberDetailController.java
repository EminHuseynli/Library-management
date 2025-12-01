package library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.dao.BookDao;
import library.dao.LoanDao;
import library.model.Book;
import library.model.Loan;
import library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class MemberDetailController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label favoriteCategoryLabel;

    @FXML
    private TableView<LoanView> loanTable;

    @FXML
    private TableColumn<LoanView, String> bookTitleColumn;

    @FXML
    private TableColumn<LoanView, String> categoryColumn;

    @FXML
    private TableColumn<LoanView, LocalDate> loanDateColumn;

    @FXML
    private TableColumn<LoanView, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<LoanView, LocalDate> returnDateColumn;

    private final LoanDao loanDao = new LoanDao();
    private final BookDao bookDao = new BookDao();
    private final ObservableList<LoanView> loanList = FXCollections.observableArrayList();

    private Member member;

    @FXML
    public void initialize() {
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        loanTable.setItems(loanList);
    }

    public void setMember(Member member) {
        this.member = member;

        // Member information
        nameLabel.setText(member.getName());
        surnameLabel.setText(member.getSurname());
        usernameLabel.setText(member.getUsername());
        phoneLabel.setText(member.getPhone());
        emailLabel.setText(member.getEmail());
        addressLabel.setText(
                member.getHomeAddress() != null ? member.getHomeAddress() : ""
        );

        loadLoanHistoryAndFavoriteCategory();
    }

    private void loadLoanHistoryAndFavoriteCategory() {
        if (member == null) return;

        try {
            List<Loan> loans = loanDao.getLoansByMember(member.getId());
            loanList.clear();

            Map<String, Integer> categoryCount = new HashMap<>();

            for (Loan loan : loans) {
                Book book = bookDao.getBookById(loan.getBookId());
                String title = (book != null) ? book.getTitle() : ("Book #" + loan.getBookId());
                String category = (book != null && book.getCategory() != null)
                        ? book.getCategory() : "Unknown";

                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);

                loanList.add(new LoanView(
                        title,
                        category,
                        loan.getLoanDate(),
                        loan.getDueDate(),
                        loan.getReturnDate()
                ));
            }

            String favorite = "-";
            int max = 0;
            for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    favorite = entry.getKey();
                }
            }

            favoriteCategoryLabel.setText(favorite);

        } catch (SQLException e) {
            e.printStackTrace();
            favoriteCategoryLabel.setText("-");
        }
    }


    public static class LoanView {
        private final String bookTitle;
        private final String category;
        private final LocalDate loanDate;
        private final LocalDate dueDate;
        private final LocalDate returnDate;

        public LoanView(String bookTitle, String category,
                        LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
            this.bookTitle = bookTitle;
            this.category = category;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getCategory() {
            return category;
        }

        public LocalDate getLoanDate() {
            return loanDate;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public LocalDate getReturnDate() {
            return returnDate;
        }
    }
}
