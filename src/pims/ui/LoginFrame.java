package pims.ui;

import pims.dao.UserDAO;
import pims.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("HealthFirst PIMS - Login");
        setSize(420, 240);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnl.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        pnl.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pnl.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        pnl.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        pnl.add(loginButton, gbc);

        add(pnl, BorderLayout.CENTER);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose();
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                new AdminDashboardFrame(user).setVisible(true);
            } else {
                new CashierDashboardFrame(user).setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
