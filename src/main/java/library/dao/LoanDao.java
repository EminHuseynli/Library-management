package library.dao;

import library.model.Loan;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDao {

    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS loans (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id     INTEGER NOT NULL,
                    member_id   INTEGER NOT NULL,
                    loan_date   DATE NOT NULL,
                    due_date    DATE NOT NULL,
                    return_date DATE,
                    FOREIGN KEY (book_id)  REFERENCES books(id),
                    FOREIGN KEY (member_id) REFERENCES members(id)
                );
                """;

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }


    public void insertLoan(Loan loan) throws SQLException {
        String sql = """
                INSERT INTO loans(book_id, member_id, loan_date, due_date, return_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, loan.getBookId());
            ps.setInt(2, loan.getMemberId());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));
            ps.setDate(4, Date.valueOf(loan.getDueDate()));

            if (loan.getReturnDate() != null) {
                ps.setDate(5, Date.valueOf(loan.getReturnDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    loan.setId(rs.getInt(1));
                }
            }
        }
    }


    public void markAsReturned(int loanId, LocalDate returnDate) throws SQLException {
        String sql = """
                UPDATE loans
                   SET return_date = ?
                 WHERE id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, loanId);

            ps.executeUpdate();
        }
    }


    public Loan getLoanById(int id) throws SQLException {
        String sql = "SELECT * FROM loans WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLoan(rs);
                }
            }
        }

        return null;
    }


    public List<Loan> getActiveLoans() throws SQLException {
        String sql = "SELECT * FROM loans WHERE return_date IS NULL";

        List<Loan> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToLoan(rs));
            }
        }

        return list;
    }

    public List<Loan> getActiveLoansByMember(int memberId) throws SQLException {
        String sql = """
                SELECT * FROM loans
                 WHERE member_id = ?
                   AND return_date IS NULL
                """;

        List<Loan> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToLoan(rs));
                }
            }
        }

        return list;
    }

    public List<Loan> getLoansByMember(int memberId) throws SQLException {
        String sql = """
                SELECT * FROM loans
                 WHERE member_id = ?
                 ORDER BY loan_date DESC
                """;

        List<Loan> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToLoan(rs));
                }
            }
        }

        return list;
    }


    public List<Loan> getOverdueLoans(LocalDate today) throws SQLException {
        String sql = """
                SELECT * FROM loans
                 WHERE due_date < ?
                   AND return_date IS NULL
                """;

        List<Loan> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(today));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToLoan(rs));
                }
            }
        }

        return list;
    }

    public List<Loan> getOverdueLoans2() throws SQLException {
        String sql = "SELECT * FROM loans WHERE return_date IS NULL";
        List<Loan> list = new ArrayList<>();
        LocalDate today = LocalDate.now();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Loan loan = mapRowToLoan(rs);
                LocalDate due = loan.getDueDate();

                if (due != null && due.isBefore(today)) {
                    list.add(loan);
                }
            }
        }

        return list;
    }


    public int countActiveLoansForBook(int bookId) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS cnt
                  FROM loans
                 WHERE book_id = ?
                   AND return_date IS NULL
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        }

        return 0;
    }


    private Loan mapRowToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setMemberId(rs.getInt("member_id"));

        Date loanDate = rs.getDate("loan_date");
        Date dueDate = rs.getDate("due_date");
        Date returnDate = rs.getDate("return_date");

        if (loanDate != null) {
            loan.setLoanDate(loanDate.toLocalDate());
        }
        if (dueDate != null) {
            loan.setDueDate(dueDate.toLocalDate());
        }
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDate());
        }

        return loan;
    }


    public List<Loan> getAllLoans() throws SQLException {
        String sql = "SELECT * FROM loans";

        List<Loan> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToLoan(rs));
            }
        }

        return list;
    }

}

