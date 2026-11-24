package pims.ui.panels;

import pims.dao.MedicineDAO;
import pims.dao.SaleDAO;
import pims.model.CartItem;
import pims.model.Medicine;
import pims.model.User;
import pims.ui.dialogs.BillDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {

    private final User cashier;
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    private final JTextField searchField = new JTextField(24);
    private final JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

    private final DefaultTableModel stockModel = new DefaultTableModel(
            new String[]{"ID", "Medicine", "Type", "Price", "Stock"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel cartModel = new DefaultTableModel(
            new String[]{"Medicine ID", "Medicine", "Qty", "Unit Price", "Line Total"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable stockTable = new JTable(stockModel);
    private final JTable cartTable = new JTable(cartModel);
    private final JLabel totalLabel = new JLabel("Total: 0.00");

    private final List<CartItem> cartItems = new ArrayList<>();

    public POSPanel(User cashier) {
        this.cashier = cashier;
        setLayout(new BorderLayout(10, 10));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterSplit(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadAllMedicines();
    }

    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchMedicines());

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> addSelectedMedicineToCart());

        panel.add(new JLabel("Medicine:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtySpinner);
        panel.add(addToCartButton);

        return panel;
    }

    private JSplitPane buildCenterSplit() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Medicines"));
        leftPanel.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
        rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.55);
        return splitPane;
    }

    private JPanel buildBottomBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton clearButton = new JButton("Clear Cart");
        clearButton.addActionListener(e -> clearCart());

        JButton checkoutButton = new JButton("Checkout & Bill");
        checkoutButton.addActionListener(e -> checkout());

        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 16f));

        panel.add(totalLabel);
        panel.add(clearButton);
        panel.add(checkoutButton);

        return panel;
    }

    private void loadAllMedicines() {
        updateStockTable(medicineDAO.getAllMedicines());
    }

    private void searchMedicines() {
        String keyword = searchField.getText().trim();
        if (keyword.trim().isEmpty()) {
            loadAllMedicines();
            return;
        }
        updateStockTable(medicineDAO.searchByName(keyword));
    }

    private void updateStockTable(List<Medicine> medicines) {
        stockModel.setRowCount(0);
        for (Medicine m : medicines) {
            stockModel.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getMedicineType(),
                    m.getPrice(),
                    m.getQuantityInStock()
            });
        }
    }

    private void addSelectedMedicineToCart() {
        int row = stockTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a medicine first.");
            return;
        }

        int medicineId = (int) stockModel.getValueAt(row, 0);
        String name = String.valueOf(stockModel.getValueAt(row, 1));
        BigDecimal unitPrice = new BigDecimal(String.valueOf(stockModel.getValueAt(row, 3)));
        int stock = Integer.parseInt(String.valueOf(stockModel.getValueAt(row, 4)));
        int qty = (int) qtySpinner.getValue();

        if (qty > stock) {
            JOptionPane.showMessageDialog(this, "Requested quantity exceeds available stock.");
            return;
        }

        for (CartItem item : cartItems) {
            if (item.getMedicineId() == medicineId) {
                if (item.getQuantity() + qty > stock) {
                    JOptionPane.showMessageDialog(this, "Total quantity in cart exceeds available stock.");
                    return;
                }
                item.addQuantity(qty);
                refreshCartTable();
                return;
            }
        }

        cartItems.add(new CartItem(medicineId, name, unitPrice, qty));
        refreshCartTable();
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            cartModel.addRow(new Object[]{
                    item.getMedicineId(),
                    item.getMedicineName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getLineTotal()
            });
            total = total.add(item.getLineTotal());
        }
        totalLabel.setText("Total: " + String.format("%.2f", total));
    }

    private void clearCart() {
        cartItems.clear();
        refreshCartTable();
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Process this sale?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            List<CartItem> billItems = new ArrayList<>(cartItems);
            int saleId = saleDAO.processSale(cashier.getUserId(), cartItems);
            clearCart();
            loadAllMedicines();

            BillDialog dialog = new BillDialog(SwingUtilities.getWindowAncestor(this), saleId, cashier.getFullName(), billItems);
            dialog.setVisible(true);

            JOptionPane.showMessageDialog(this, "Sale completed successfully. Sale ID: " + saleId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Checkout failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
