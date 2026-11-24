package pims.dao;

import pims.db.DBConnection;
import pims.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public List<Supplier> getAllSuppliers() {
        String sql = "SELECT supplier_id, name, contact_person, phone, email, address FROM suppliers ORDER BY supplier_id";
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapSupplier(rs));
            }
            return suppliers;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load suppliers", ex);
        }
    }

    public void createSupplier(String name, String contactPerson, String phone, String email, String address) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, contactPerson);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, address);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create supplier", ex);
        }
    }

    public void updateSupplier(int supplierId, String name, String contactPerson, String phone, String email, String address) {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ? WHERE supplier_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, contactPerson);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, address);
            ps.setInt(6, supplierId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update supplier", ex);
        }
    }

    public void deleteSupplier(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete supplier", ex);
        }
    }

    private Supplier mapSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("name"),
                rs.getString("contact_person"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address")
        );
    }
}
