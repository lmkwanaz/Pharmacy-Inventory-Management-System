package pims.ui;

import pims.model.User;
import pims.ui.panels.ManageMedicinesPanel;
import pims.ui.panels.ManageSuppliersPanel;
import pims.ui.panels.ManageUsersPanel;
import pims.ui.panels.ReportsPanel;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame(User usr) {
        setTitle("HealthFirst PIMS - Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + usr.getFullName() + " (Admin)");
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
        tabs.addTab("Manage Medicines", new ManageMedicinesPanel());
        tabs.addTab("Manage Suppliers", new ManageSuppliersPanel());
        tabs.addTab("Manage Users", new ManageUsersPanel());
        tabs.addTab("Reports", new ReportsPanel());

        add(tpBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
