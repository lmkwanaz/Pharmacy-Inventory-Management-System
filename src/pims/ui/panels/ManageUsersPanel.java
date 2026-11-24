package pims.ui.panels;

import pims.dao.UserDAO;
import pims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Role", "Full Name"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable usersTable = new JTable(tableModel);
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField fullNameField = new JTextField();

    public ManageUsersPanel() {
        setLayout(new BorderLayout(10, 10));

        add(new JScrollPane(usersTable), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder("Cashier Accounts"));

        JPanel fields = new JPanel(new GridLayout(2, 3, 8, 8));
        fields.add(new JLabel("Username"));
        fields.add(new JLabel("Password"));
        fields.add(new JLabel("Full Name"));
        fields.add(usernameField);
        fields.add(passwordField);
        fields.add(fullNameField);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Create Cashier");
        addBtn.addActionListener(e -> createCashier());

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelected());

        actions.add(addBtn);
        actions.add(deleteBtn);

        container.add(fields, BorderLayout.CENTER);
        container.add(actions, BorderLayout.SOUTH);
        return container;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllCashiers();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getFullName()
            });
        }
    }

    private void createCashier() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();

        if (username.trim().isEmpty() || password.trim().isEmpty() || fullName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        userDAO.createCashier(username, password, fullName);
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        refreshTable();
    }

    private void deleteSelected() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a cashier account to delete.");
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete cashier ID " + userId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        userDAO.deleteUser(userId);
        refreshTable();
    }
}
