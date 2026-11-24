package pims.ui.panels;

import pims.dao.ReportDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportsPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();
    private final JTable reportTable = new JTable();
    private final JComboBox<String> reportSelector = new JComboBox<>(new String[]{
            "Sales Report",
            "Item-Wise Report",
            "Low Stock Report",
            "Expiry Report (Next 1 Month)"
    });

    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel tp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton runBtn = new JButton("Run Report");
        runBtn.addActionListener(e -> loadSelectedReport());

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.addActionListener(e -> exportTableToCsv());

        tp.add(new JLabel("Report:"));
        tp.add(reportSelector);
        tp.add(runBtn);
        tp.add(exportBtn);

        add(tp, BorderLayout.NORTH);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        loadSelectedReport();
    }

    private void exportTableToCsv() {
        if (reportTable.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("report.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
            for (int count = 0; count < model.getColumnCount(); count++) {
                writer.write(model.getColumnName(count));
                if (count < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int count = 0; count < model.getColumnCount(); count++) {
                    Object val = model.getValueAt(row, count);
                    writer.write(val == null ? "" : String.valueOf(val));
                    if (count < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "CSV exported to: " + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedReport() {
        String clected = (String) reportSelector.getSelectedItem();
        DefaultTableModel model;
        if ("Sales Report".equals(clected)) {
            model = reportDAO.getSalesReport();
        } else if ("Item-Wise Report".equals(clected)) {
            model = reportDAO.getItemWiseSalesReport();
        } else if ("Low Stock Report".equals(clected)) {
            model = reportDAO.getLowStockReport();
        } else {
            model = reportDAO.getExpiryReport();
        }
        reportTable.setModel(model);
    }

}
