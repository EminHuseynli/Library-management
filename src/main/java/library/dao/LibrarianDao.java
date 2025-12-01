
package library.dao;

import library.model.Librarian;
import java.sql.*;

public class LibrarianDao {


    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS librarians (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    username  TEXT NOT NULL UNIQUE,
                    password  TEXT NOT NULL
                );
                """;

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }


    public void insertLibrarian(Librarian librarian) throws SQLException {
        String sql = """
                INSERT INTO librarians(username, password)
                VALUES (?, ?)
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, librarian.getUsername());
            ps.setString(2, librarian.getPassword());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    librarian.setId(rs.getInt(1));
                }
            }
        }
    }


    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM librarians WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public Librarian findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM librarians WHERE username = ? AND password = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLibrarian(rs);
                }
            }
        }

        return null;
    }


    public Librarian findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM librarians WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLibrarian(rs);
                }
            }
        }

        return null;
    }


    private Librarian mapRowToLibrarian(ResultSet rs) throws SQLException {
        Librarian librarian = new Librarian();
        librarian.setId(rs.getInt("id"));
        librarian.setUsername(rs.getString("username"));
        librarian.setPassword(rs.getString("password"));
        return librarian;
    }
}
