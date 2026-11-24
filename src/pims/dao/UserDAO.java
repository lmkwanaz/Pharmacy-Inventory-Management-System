package pims.dao;

import pims.db.DBConnection;
import pims.model.User;
import pims.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String plainPassword) {
        String sql = "SELECT user_id, username, password, role, full_name FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String storedPassword = rs.getString("password");
                String hashedInput = PasswordUtil.sha256(plainPassword);

                boolean matches = storedPassword.equals(hashedInput) || storedPassword.equals(plainPassword);
                if (!matches) {
                    return null;
                }

                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("full_name")
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to authenticate user", ex);
        }
    }

    public List<User> getAllCashiers() {
        String sql = "SELECT user_id, username, role, full_name FROM users WHERE role = 'Cashier' ORDER BY user_id";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("full_name")
                ));
            }
            return users;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch cashiers", ex);
        }
    }

    public void createCashier(String username, String plainPassword, String fullName) {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, 'Cashier', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.sha256(plainPassword));
            ps.setString(3, fullName);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create cashier", ex);
        }
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ? AND role = 'Cashier'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete cashier", ex);
        }
    }
}
