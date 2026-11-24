package pims.ui.panels;

import pims.dao.MedicineDAO;
import pims.dao.SupplierDAO;
import pims.model.Medicine;
import pims.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ManageMedicinesPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Company", "Type", "Price", "Stock", "Reorder", "Expiry", "Supplier ID", "Supplier"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable medicineTable = new JTable(tableModel);

    private final JTextField nameField = new JTextField();
    private final JTextField companyField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Tablet", "Capsule", "Syrup", "Injection", "Cream"});
    private final JTextField priceField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JTextField reorderField = new JTextField();
    private final JTextField expiryField = new JTextField("2026-12-31");
    private final JComboBox<Supplier> supplierBox = new JComboBox<>();

    public ManageMedicinesPanel() {
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(medicineTable), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);

        medicineTable.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        loadSuppliers();
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Medicine Details"));

        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Company"));
        panel.add(companyField);

        panel.add(new JLabel("Type"));
        panel.add(typeBox);
        panel.add(new JLabel("Price"));
        panel.add(priceField);

        panel.add(new JLabel("Stock"));
        panel.add(stockField);
        panel.add(new JLabel("Reorder Level"));
        panel.add(reorderField);

        panel.add(new JLabel("Expiry (YYYY-MM-DD)"));
        panel.add(expiryField);
        panel.add(new JLabel("Supplier"));
        panel.add(supplierBox);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addMedicine());
        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> updateMedicine());
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteMedicine());

        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);

        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
        container.add(actions, BorderLayout.SOUTH);
        return container;
    }

    private void loadSuppliers() {
        supplierBox.removeAllItems();
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier supplier : suppliers) {
            supplierBox.addItem(supplier);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = medicineDAO.getAllMedicines();
        for (Medicine m : medicines) {
            tableModel.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getCompany(),
                    m.getMedicineType(),
                    m.getPrice(),
                    m.getQuantityInStock(),
                    m.getReorderLevel(),
                    m.getExpiryDate(),
                    m.getSupplierId(),
                    m.getSupplierName()
            });
        }
    }

    private void addMedicine() {
        try {
            Supplier supplier = (Supplier) supplierBox.getSelectedItem();
            if (supplier == null) {
                JOptionPane.showMessageDialog(this, "Please create a supplier first.");
                return;
            }

            medicineDAO.createMedicine(
                    nameField.getText().trim(),
                    companyField.getText().trim(),
                    String.valueOf(typeBox.getSelectedItem()),
                    new BigDecimal(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim()),
                    Integer.parseInt(reorderField.getText().trim()),
                    LocalDate.parse(expiryField.getText().trim()),
                    supplier.getSupplierId()
            );
            clearForm();
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMedicine() {
        int row = medicineTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a medicine to update.");
            return;
        }

        try {
            int medicineId = (int) tableModel.getValueAt(row, 0);
            Supplier supplier = (Supplier) supplierBox.getSelectedItem();
            if (supplier == null) {
                JOptionPane.showMessageDialog(this, "Please choose a supplier.");
                return;
            }

            medicineDAO.updateMedicine(
                    medicineId,
                    nameField.getText().trim(),
                    companyField.getText().trim(),
                    String.valueOf(typeBox.getSelectedItem()),
                    new BigDecimal(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim()),
                    Integer.parseInt(reorderField.getText().trim()),
                    LocalDate.parse(expiryField.getText().trim()),
                    supplier.getSupplierId()
            );
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicine() {
        int row = medicineTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a medicine to delete.");
            return;
        }

        int medicineId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete medicine ID " + medicineId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        medicineDAO.deleteMedicine(medicineId);
        clearForm();
        refreshTable();
    }

    private void fillFormFromSelection() {
        int row = medicineTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        companyField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        typeBox.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 3)));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        stockField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        reorderField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        expiryField.setText(String.valueOf(tableModel.getValueAt(row, 7)));

        int supplierId = (int) tableModel.getValueAt(row, 8);
        for (int i = 0; i < supplierBox.getItemCount(); i++) {
            Supplier supplier = supplierBox.getItemAt(i);
            if (supplier.getSupplierId() == supplierId) {
                supplierBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        companyField.setText("");
        priceField.setText("");
        stockField.setText("");
        reorderField.setText("");
        expiryField.setText("2026-12-31");
        if (supplierBox.getItemCount() > 0) {
            supplierBox.setSelectedIndex(0);
        }
    }
}
