package pims.ui;

import pims.model.User;
import pims.ui.panels.POSPanel;
import pims.ui.panels.StockCheckPanel;

import javax.swing.*;
import java.awt.*;

public class CashierDashboardFrame extends JFrame {

    public CashierDashboardFrame(User usr) {
        setTitle("HealthFirst PIMS - Cashier Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + usr.getFullName() + " (Cashier)");
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel tpBar = new JPanel(new BorderLayout());
        tpBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tpBar.add(welcomeLabel, BorderLayout.WEST);
        tpBar.add(logoutButton, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Point of Sale", new POSPanel(usr));
        tabs.addTab("Stock Check", new StockCheckPanel());

        add(tpBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
