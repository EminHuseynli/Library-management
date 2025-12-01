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
import library.dao.MemberDao;
import library.model.Book;
import library.model.Loan;
import library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LibrarianIssuedBooksController {

    @FXML
    private TableView<OverdueLoanView> overdueTable;

    @FXML
    private TableColumn<OverdueLoanView, String> bookTitleColumn;

    @FXML
    private TableColumn<OverdueLoanView, String> memberNameColumn;

    @FXML
    private TableColumn<OverdueLoanView, LocalDate> loanDateColumn;

    @FXML
    private TableColumn<OverdueLoanView, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<OverdueLoanView, Long> daysOverdueColumn;

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Label bookCategoryLabel;

    @FXML
    private Label memberNameLabel;

    @FXML
    private Label memberUsernameLabel;

    @FXML
    private Label memberPhoneLabel;

    @FXML
    private Label memberEmailLabel;

    @FXML
    private Label memberAddressLabel;

    @FXML
    private Label loanDateLabel;

    @FXML
    private Label dueDateLabel;

    @FXML
    private Label daysOverdueLabel;

    private final ObservableList<OverdueLoanView> overdueList = FXCollections.observableArrayList();

    private final LoanDao loanDao = new LoanDao();
    private final BookDao bookDao = new BookDao();
    private final MemberDao memberDao = new MemberDao();

    @FXML
    public void initialize() {
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        daysOverdueColumn.setCellValueFactory(new PropertyValueFactory<>("daysOverdue"));

        overdueTable.setItems(overdueList);

        overdueTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showDetails(newVal)
        );

        loadOverdueLoans();
    }

    private void loadOverdueLoans() {
        overdueList.clear();
        clearDetails();

        try {
            List<Loan> loans = loanDao.getOverdueLoans2();
            LocalDate today = LocalDate.now();

            for (Loan loan : loans) {
                Book book = null;
                Member member = null;

                try {
                    book = bookDao.getBookById(loan.getBookId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    member = memberDao.getMemberById(loan.getMemberId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String bookTitle = (book != null) ? safe(book.getTitle(), "Unknown book")
                        : "Book #" + loan.getBookId();
                String category = (book != null) ? safe(book.getCategory(), "Unknown")
                        : "Unknown";

                String memberFullName = (member != null)
                        ? safe(member.getName(), "") + " " + safe(member.getSurname(), "")
                        : "Member #" + loan.getMemberId();

                LocalDate due = loan.getDueDate();
                long daysOverdue = 0;
                if (due != null && due.isBefore(today)) {
                    daysOverdue = ChronoUnit.DAYS.between(due, today);
                }

                OverdueLoanView view = new OverdueLoanView(
                        loan,
                        book,
                        member,
                        bookTitle,
                        category,
                        memberFullName.trim(),
                        loan.getLoanDate(),
                        due,
                        daysOverdue
                );

                overdueList.add(view);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(OverdueLoanView view) {
        if (view == null) {
            clearDetails();
            return;
        }

        Book book = view.getBook();
        Member member = view.getMember();
        Loan loan = view.getLoan();

        bookTitleLabel.setText(
                (book != null) ? safe(book.getTitle(), "-") : view.getBookTitle()
        );
        bookCategoryLabel.setText(
                (book != null) ? safe(book.getCategory(), "-") : safe(view.getCategory(), "-")
        );

        if (member != null) {
            memberNameLabel.setText(
                    safe(member.getName(), "") + " " + safe(member.getSurname(), "")
            );
            memberUsernameLabel.setText(safe(member.getUsername(), "-"));
            memberPhoneLabel.setText(safe(member.getPhone(), "-"));
            memberEmailLabel.setText(safe(member.getEmail(), "-"));
            memberAddressLabel.setText(safe(member.getHomeAddress(), "-"));
        } else {
            memberNameLabel.setText(view.getMemberName());
            memberUsernameLabel.setText("-");
            memberPhoneLabel.setText("-");
            memberEmailLabel.setText("-");
            memberAddressLabel.setText("-");
        }

        loanDateLabel.setText(toStr(loan.getLoanDate()));
        dueDateLabel.setText(toStr(loan.getDueDate()));
        daysOverdueLabel.setText(String.valueOf(view.getDaysOverdue()));
    }

    private void clearDetails() {
        bookTitleLabel.setText("-");
        bookCategoryLabel.setText("-");
        memberNameLabel.setText("-");
        memberUsernameLabel.setText("-");
        memberPhoneLabel.setText("-");
        memberEmailLabel.setText("-");
        memberAddressLabel.setText("-");
        loanDateLabel.setText("-");
        dueDateLabel.setText("-");
        daysOverdueLabel.setText("-");
    }

    private String safe(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        return value;
    }

    private String toStr(LocalDate date) {
        return (date == null) ? "-" : date.toString();
    }


    public static class OverdueLoanView {
        private final Loan loan;
        private final Book book;
        private final Member member;

        private final String bookTitle;
        private final String category;
        private final String memberName;
        private final LocalDate loanDate;
        private final LocalDate dueDate;
        private final long daysOverdue;

        public OverdueLoanView(Loan loan,
                               Book book,
                               Member member,
                               String bookTitle,
                               String category,
                               String memberName,
                               LocalDate loanDate,
                               LocalDate dueDate,
                               long daysOverdue) {
            this.loan = loan;
            this.book = book;
            this.member = member;
            this.bookTitle = bookTitle;
            this.category = category;
            this.memberName = memberName;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
            this.daysOverdue = daysOverdue;
        }

        public Loan getLoan() {
            return loan;
        }

        public Book getBook() {
            return book;
        }

        public Member getMember() {
            return member;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getCategory() {
            return category;
        }

        public String getMemberName() {
            return memberName;
        }

        public LocalDate getLoanDate() {
            return loanDate;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public long getDaysOverdue() {
            return daysOverdue;
        }
    }
}
