package pims.dao;

import pims.db.DBConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReportDAO {

    public DefaultTableModel getSalesReport() {
        String sql = "SELECT s.sale_id, s.sale_date, u.full_name AS cashier, s.total_amount " +
            "FROM sales s " +
            "JOIN users u ON u.user_id = s.user_id " +
            "ORDER BY s.sale_date DESC";
        return queryToTableModel(sql);
    }

    public DefaultTableModel getItemWiseSalesReport() {
        String sql = "SELECT m.name AS medicine_name, " +
            "SUM(si.quantity_sold) AS total_units_sold, " +
            "ROUND(SUM(si.quantity_sold * si.price_at_sale), 2) AS total_revenue " +
            "FROM sale_items si " +
            "JOIN medicines m ON m.medicine_id = si.medicine_id " +
            "GROUP BY m.name " +
            "ORDER BY total_units_sold DESC";
        return queryToTableModel(sql);
    }

    public DefaultTableModel getLowStockReport() {
        String sql = "SELECT medicine_id, name, quantity_in_stock, reorder_level " +
            "FROM medicines " +
            "WHERE quantity_in_stock <= reorder_level " +
            "ORDER BY quantity_in_stock ASC";
        return queryToTableModel(sql);
    }

    public DefaultTableModel getExpiryReport() {
        String sql = "SELECT medicine_id, name, expiry_date, quantity_in_stock " +
            "FROM medicines " +
            "WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH) " +
            "ORDER BY expiry_date ASC";
        return queryToTableModel(sql);
    }

    private DefaultTableModel queryToTableModel(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            String[] cols = new String[colCount];
            for (int i = 1; i <= colCount; i++) {
                cols[i - 1] = md.getColumnLabel(i);
            }

            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 1; i <= colCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
            return model;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to generate report", ex);
        }
    }
}
