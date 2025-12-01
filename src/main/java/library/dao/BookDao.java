package library.dao;

import library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDao {


    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS books (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    title       TEXT NOT NULL,
                    author      TEXT NOT NULL,
                    isbn        TEXT NOT NULL UNIQUE,
                    category    TEXT,
                    copy_count  INTEGER NOT NULL
                );
                """;

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }


    public void insertBook(Book book) throws SQLException {
        String sql = """
                INSERT INTO books(title, author, isbn, category, copy_count)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getCategory());
            ps.setInt(5, book.getCopyCount());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    book.setId(generatedId);
                }
            }
        }
    }


    public void updateBook(Book book) throws SQLException {
        String sql = """
                UPDATE books
                   SET title = ?,
                       author = ?,
                       isbn = ?,
                       category = ?,
                       copy_count = ?
                 WHERE id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getCategory());
            ps.setInt(5, book.getCopyCount());
            ps.setInt(6, book.getId());

            ps.executeUpdate();
        }
    }


    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        }

        return null;
    }


    public Book getBookByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, isbn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        }

        return null;
    }


    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM books ORDER BY title ASC";

        List<Book> books = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        }

        return books;
    }


    public List<Book> searchBooks(String keyword) throws SQLException {
        String sql = """
                SELECT * FROM books
                 WHERE title    LIKE ?
                    OR author   LIKE ?
                    OR isbn     LIKE ?
                    OR category LIKE ?
                ORDER BY title ASC
                """;

        List<Book> books = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        }

        return books;
    }


    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setCategory(rs.getString("category"));
        book.setCopyCount(rs.getInt("copy_count"));
        return book;
    }
}
