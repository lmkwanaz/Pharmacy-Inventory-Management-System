package pims.ui.panels;

import pims.dao.MedicineDAO;
import pims.model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockCheckPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final JTextField searchField = new JTextField(30);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Medicine", "Type", "Price", "Stock", "Expiry"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public StockCheckPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> search());
        top.add(new JLabel("Medicine Name:"));
        top.add(searchField);
        top.add(searchButton);

        JTable table = new JTable(tableModel);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadAll();
    }

    private void updateTable(List<Medicine> medicines) {
        tableModel.setRowCount(0);
        for (Medicine m : medicines) {
            tableModel.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getMedicineType(),
                    m.getPrice(),
                    m.getQuantityInStock(),
                    m.getExpiryDate()
            });
        }
    }

    private void loadAll() {
        updateTable(medicineDAO.getAllMedicines());
    }

    private void search() {
        String keyword = searchField.getText().trim();
        if (keyword.trim().isEmpty()) {
            loadAll();
            return;
        }
        updateTable(medicineDAO.searchByName(keyword));
    }

}
