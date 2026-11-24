package pims.ui.panels;

import pims.dao.SupplierDAO;
import pims.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSuppliersPanel extends JPanel {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Contact Person", "Phone", "Email", "Address"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable supplierTable = new JTable(tableModel);

    private final JTextField nameField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField addressField = new JTextField();

    public ManageSuppliersPanel() {
        setLayout(new BorderLayout(10, 10));

        add(new JScrollPane(supplierTable), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);

        supplierTable.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));

        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Contact Person"));
        panel.add(contactField);

        panel.add(new JLabel("Phone"));
        panel.add(phoneField);
        panel.add(new JLabel("Email"));
        panel.add(emailField);

        panel.add(new JLabel("Address"));
        panel.add(addressField);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addSupplier());
        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> updateSupplier());
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteSupplier());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);

        panel.add(actions);
        return panel;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                    s.getSupplierId(),
                    s.getName(),
                    s.getContactPerson(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getAddress()
            });
        }
    }

    private void addSupplier() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier name is required.");
            return;
        }

        supplierDAO.createSupplier(
                nameField.getText().trim(),
                contactField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressField.getText().trim()
        );
        clearForm();
        refreshTable();
    }

    private void updateSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a supplier to update.");
            return;
        }

        int supplierId = (int) tableModel.getValueAt(row, 0);
        supplierDAO.updateSupplier(
                supplierId,
                nameField.getText().trim(),
                contactField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressField.getText().trim()
        );
        refreshTable();
    }

    private void deleteSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a supplier to delete.");
            return;
        }

        int supplierId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete supplier ID " + supplierId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        supplierDAO.deleteSupplier(supplierId);
        clearForm();
        refreshTable();
    }

    private void fillFormFromSelection() {
        int row = supplierTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        contactField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        emailField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        addressField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        nameField.setText("");
        contactField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
    }
}
