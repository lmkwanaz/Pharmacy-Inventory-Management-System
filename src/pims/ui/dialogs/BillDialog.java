package pims.ui.dialogs;

import pims.model.CartItem;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillDialog extends JDialog {

    public BillDialog(Window owner, int saleId, String cashierName, List<CartItem> items) {
        super(owner, "Generated Bill", ModalityType.APPLICATION_MODAL);
        setSize(550, 600);
        setLocationRelativeTo(owner);

        JTextArea billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        String billText = buildBillText(saleId, cashierName, items);
        billArea.setText(billText);

        JButton saveBtn = new JButton("Save Bill (.txt)");
        saveBtn.addActionListener(e -> saveBillToFile(billText));

        JButton printBtn = new JButton("Print");
        printBtn.addActionListener(e -> {
            try {
                billArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveBtn);
        buttonPanel.add(printBtn);

        add(new JScrollPane(billArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String buildBillText(int saleId, String cashierName, List<CartItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================\n");
        sb.append("      HEALTHFIRST PHARMACY\n");
        sb.append("==============================\n");
        sb.append("Sale ID   : ").append(saleId).append("\n");
        sb.append("Cashier   : ").append(cashierName).append("\n");
        sb.append("Date/Time : ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("------------------------------\n");
        sb.append(String.format("%-20s %5s %10s\n", "Item", "Qty", "Total"));
        sb.append("------------------------------\n");

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            sb.append(String.format("%-20s %5d %10.2f\n",
                    trim(item.getMedicineName(), 20),
                    item.getQuantity(),
                    item.getLineTotal()));
            total = total.add(item.getLineTotal());
        }

        sb.append("------------------------------\n");
        sb.append(String.format("%-20s %5s %10.2f\n", "GRAND TOTAL", "", total));
        sb.append("==============================\n");
        sb.append("Thank you for your purchase!\n");
        return sb.toString();
    }

    private String trim(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max - 3) + "...";
    }

    private void saveBillToFile(String billText) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("bill_" + System.currentTimeMillis() + ".txt"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(billText);
            JOptionPane.showMessageDialog(this, "Bill saved to: " + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
