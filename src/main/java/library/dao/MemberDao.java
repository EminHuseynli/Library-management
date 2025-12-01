package library.dao;

import library.model.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {


    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS members (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT,
                    name         TEXT NOT NULL,
                    surname      TEXT NOT NULL,
                    username     TEXT NOT NULL UNIQUE,
                    password     TEXT NOT NULL,
                    phone        TEXT NOT NULL,
                    email        TEXT NOT NULL,
                    home_address TEXT
                );
                """;

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }


    public void createMember(Member member) throws SQLException {
        String sql = """
                INSERT INTO members(name, surname, username, password, phone, email, home_address)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getSurname());
            ps.setString(3, member.getUsername());
            ps.setString(4, member.getPassword());
            ps.setString(5, member.getPhone());
            ps.setString(6, member.getEmail());

            if (member.getHomeAddress() != null && !member.getHomeAddress().isBlank()) {
                ps.setString(7, member.getHomeAddress());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    member.setId(rs.getInt(1));
                }
            }
        }
    }


    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM members WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public Member findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM members WHERE username = ? AND password = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMember(rs);
                }
            }
        }

        return null;
    }


    public Member findById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMember(rs);
                }
            }
        }

        return null;
    }


    public List<Member> getAllMembers() throws SQLException {
        String sql = "SELECT * FROM members ORDER BY surname ASC, name ASC";

        List<Member> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToMember(rs));
            }
        }

        return list;
    }


    public List<Member> searchMembers(String keyword) throws SQLException {
        String sql = """
            SELECT * FROM members
             WHERE name LIKE ?
                OR surname LIKE ?
                OR username LIKE ?
                OR phone LIKE ?
                OR email LIKE ?
                OR home_address LIKE ?
            ORDER BY surname ASC, name ASC
            """;

        List<Member> list = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);
            ps.setString(6, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMember(rs));
                }
            }
        }

        return list;
    }



    public void updateMember(Member member) throws SQLException {
        String sql = """
                UPDATE members
                   SET name = ?,
                       surname = ?,
                       username = ?,
                       password = ?,
                       phone = ?,
                       email = ?,
                       home_address = ?
                 WHERE id = ?
                """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getSurname());
            ps.setString(3, member.getUsername());
            ps.setString(4, member.getPassword());
            ps.setString(5, member.getPhone());
            ps.setString(6, member.getEmail());
            if (member.getHomeAddress() != null && !member.getHomeAddress().isBlank()) {
                ps.setString(7, member.getHomeAddress());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            ps.setInt(8, member.getId());

            ps.executeUpdate();
        }
    }


    public void deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    private Member mapRowToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setName(rs.getString("name"));
        member.setSurname(rs.getString("surname"));
        member.setUsername(rs.getString("username"));
        member.setPassword(rs.getString("password"));
        member.setPhone(rs.getString("phone"));
        member.setEmail(rs.getString("email"));
        member.setHomeAddress(rs.getString("home_address"));
        return member;
    }


    public Member getMemberById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMember(rs);
                }
            }
        }

        return null;
    }


}
