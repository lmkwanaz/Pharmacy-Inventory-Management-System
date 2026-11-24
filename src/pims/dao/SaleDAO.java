package pims.dao;

import pims.db.DBConnection;
import pims.model.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class SaleDAO {

    private final MedicineDAO medicineDAO = new MedicineDAO();

    public int processSale(int userId, List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        String insertSaleSql = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";

        BigDecimal total = cartItems.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement salePs = conn.prepareStatement(insertSaleSql, Statement.RETURN_GENERATED_KEYS)) {
                salePs.setBigDecimal(1, total);
                salePs.setInt(2, userId);
                salePs.executeUpdate();

                int saleId;
                try (ResultSet keys = salePs.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Failed to retrieve sale ID");
                    }
                    saleId = keys.getInt(1);
                }

                try (PreparedStatement itemPs = conn.prepareStatement(insertItemSql)) {
                    for (CartItem item : cartItems) {
                        medicineDAO.reduceStock(conn, item.getMedicineId(), item.getQuantity());

                        itemPs.setInt(1, saleId);
                        itemPs.setInt(2, item.getMedicineId());
                        itemPs.setInt(3, item.getQuantity());
                        itemPs.setBigDecimal(4, item.getUnitPrice());
                        itemPs.addBatch();
                    }
                    itemPs.executeBatch();
                }

                conn.commit();
                return saleId;
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to process sale", ex);
        }
    }
}
