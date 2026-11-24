package pims.dao;

import pims.db.DBConnection;
import pims.model.Medicine;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public List<Medicine> getAllMedicines() {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.medicine_type, m.price, m.quantity_in_stock, " +
            "m.reorder_level, m.expiry_date, m.supplier_id, s.name AS supplier_name " +
            "FROM medicines m " +
            "LEFT JOIN suppliers s ON s.supplier_id = m.supplier_id " +
            "ORDER BY m.medicine_id";

        List<Medicine> medicines = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicines.add(mapMedicine(rs));
            }
            return medicines;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch medicines", ex);
        }
    }

    public List<Medicine> searchByName(String keyword) {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.medicine_type, m.price, m.quantity_in_stock, " +
            "m.reorder_level, m.expiry_date, m.supplier_id, s.name AS supplier_name " +
            "FROM medicines m " +
            "LEFT JOIN suppliers s ON s.supplier_id = m.supplier_id " +
            "WHERE m.name LIKE ? " +
            "ORDER BY m.name";

        List<Medicine> medicines = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicines.add(mapMedicine(rs));
                }
            }
            return medicines;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to search medicine", ex);
        }
    }

    public Medicine getMedicineById(int medicineId) {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.medicine_type, m.price, m.quantity_in_stock, " +
            "m.reorder_level, m.expiry_date, m.supplier_id, s.name AS supplier_name " +
            "FROM medicines m " +
            "LEFT JOIN suppliers s ON s.supplier_id = m.supplier_id " +
            "WHERE m.medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, medicineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMedicine(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch medicine by ID", ex);
        }
    }

    public void createMedicine(String name, String company, String type, BigDecimal price, int qty, int reorder, LocalDate expiry, int supplierId) {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, company);
            ps.setString(3, type);
            ps.setBigDecimal(4, price);
            ps.setInt(5, qty);
            ps.setInt(6, reorder);
            ps.setDate(7, Date.valueOf(expiry));
            ps.setInt(8, supplierId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create medicine", ex);
        }
    }

    public void updateMedicine(int medicineId, String name, String company, String type, BigDecimal price, int qty, int reorder, LocalDate expiry, int supplierId) {
        String sql = "UPDATE medicines " +
                "SET name = ?, company = ?, medicine_type = ?, price = ?, quantity_in_stock = ?, " +
                "reorder_level = ?, expiry_date = ?, supplier_id = ? " +
                "WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, company);
            ps.setString(3, type);
            ps.setBigDecimal(4, price);
            ps.setInt(5, qty);
            ps.setInt(6, reorder);
            ps.setDate(7, Date.valueOf(expiry));
            ps.setInt(8, supplierId);
            ps.setInt(9, medicineId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update medicine", ex);
        }
    }

    public void deleteMedicine(int medicineId) {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, medicineId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete medicine", ex);
        }
    }

    public void reduceStock(Connection conn, int medicineId, int qty) throws SQLException {
        String sql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ? AND quantity_in_stock >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, medicineId);
            ps.setInt(3, qty);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Insufficient stock for medicine ID " + medicineId);
            }
        }
    }

    private Medicine mapMedicine(ResultSet rs) throws SQLException {
        return new Medicine(
                rs.getInt("medicine_id"),
                rs.getString("name"),
                rs.getString("company"),
                rs.getString("medicine_type"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity_in_stock"),
                rs.getInt("reorder_level"),
                rs.getDate("expiry_date").toLocalDate(),
                rs.getInt("supplier_id"),
                rs.getString("supplier_name")
        );
    }
}
