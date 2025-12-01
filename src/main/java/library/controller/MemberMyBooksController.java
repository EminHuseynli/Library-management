package library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import java.util.List;

public class MemberMyBooksController {

    @FXML
    private TableView<LoanView> loansTable;

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

    @FXML
    private TableColumn<LoanView, String> statusColumn;

    private final ObservableList<LoanView> loanList = FXCollections.observableArrayList();

    private final LoanDao loanDao = new LoanDao();
    private final BookDao bookDao = new BookDao();

    private Member loggedInMember;

    @FXML
    public void initialize() {
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loansTable.setItems(loanList);
    }

    public void setLoggedInMember(Member member) {
        this.loggedInMember = member;
        loadLoans();
    }

    private void loadLoans() {
        loanList.clear();

        if (loggedInMember == null) {
            return;
        }

        try {
            List<Loan> loans = loanDao.getLoansByMember(loggedInMember.getId());
            LocalDate today = LocalDate.now();

            for (Loan loan : loans) {
                Book book = bookDao.getBookById(loan.getBookId());
                String title = (book != null) ? book.getTitle() : ("Book #" + loan.getBookId());
                String category = (book != null && book.getCategory() != null)
                        ? book.getCategory()
                        : "Unknown";

                LocalDate loanDate = loan.getLoanDate();
                LocalDate dueDate = loan.getDueDate();
                LocalDate returnDate = loan.getReturnDate();

                String status;
                if (returnDate == null) {
                    if (dueDate != null && dueDate.isBefore(today)) {
                        status = "Overdue";
                    } else {
                        status = "Active";
                    }
                } else {
                    status = "Returned";
                }

                loanList.add(new LoanView(
                        title,
                        category,
                        loanDate,
                        dueDate,
                        returnDate,
                        status
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static class LoanView {
        private final String bookTitle;
        private final String category;
        private final LocalDate loanDate;
        private final LocalDate dueDate;
        private final LocalDate returnDate;
        private final String status;

        public LoanView(String bookTitle,
                        String category,
                        LocalDate loanDate,
                        LocalDate dueDate,
                        LocalDate returnDate,
                        String status) {
            this.bookTitle = bookTitle;
            this.category = category;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
            this.status = status;
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

        public String getStatus() {
            return status;
        }
    }
}

