package library.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import library.dao.BookDao;
import library.dao.LoanDao;
import library.dao.MemberDao;
import library.model.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibrarianMainController {

    @FXML
    private Label totalBooksLabel;

    @FXML
    private Label totalMembersLabel;

    @FXML
    private Label activeLoansLabel;

    @FXML
    private Label membersWithActiveLoansLabel;

    @FXML
    private Label overdueLoansLabel;

    private final BookDao bookDao = new BookDao();
    private final MemberDao memberDao = new MemberDao();
    private final LoanDao loanDao = new LoanDao();

    @FXML
    public void initialize() {
        updateDashboard();
    }

    private void updateDashboard() {
        try {
            int totalBooks = bookDao.getAllBooks().size();

            int totalMembers = memberDao.getAllMembers().size();

            List<Loan> activeLoans = loanDao.getActiveLoans();
            int activeLoansCount = activeLoans.size();

            Set<Integer> memberIdsWithActiveLoans = new HashSet<>();
            for (Loan loan : activeLoans) {
                memberIdsWithActiveLoans.add(loan.getMemberId());
            }
            int membersWithActiveLoansCount = memberIdsWithActiveLoans.size();

            int overdueLoansCount = loanDao.getOverdueLoans(LocalDate.now()).size();

            totalBooksLabel.setText(String.valueOf(totalBooks));
            totalMembersLabel.setText(String.valueOf(totalMembers));
            activeLoansLabel.setText(String.valueOf(activeLoansCount));
            membersWithActiveLoansLabel.setText(String.valueOf(membersWithActiveLoansCount));
            overdueLoansLabel.setText(String.valueOf(overdueLoansCount));

        } catch (SQLException e) {
            e.printStackTrace();
            totalBooksLabel.setText("-");
            totalMembersLabel.setText("-");
            activeLoansLabel.setText("-");
            membersWithActiveLoansLabel.setText("-");
            overdueLoansLabel.setText("-");
        }
    }
}
