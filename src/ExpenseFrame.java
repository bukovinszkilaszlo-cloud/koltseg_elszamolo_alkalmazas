import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpenseFrame extends JFrame {
    private Database db;
    private JTable table;
    private DefaultTableModel model;

    public ExpenseFrame() {
        db = new Database();
        setTitle("Költségelszámoló");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Táblázat
        model = new DefaultTableModel(new String[]{"ID", "Megnevezés", "Összeg (Ft)"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Gombok
        JButton addBtn = new JButton("Hozzáadás");
        JButton deleteBtn = new JButton("Törlés");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();

        addBtn.addActionListener(e -> addExpense());
        deleteBtn.addActionListener(e -> deleteExpense());

        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Expense> expenses = db.getAllExpenses();
        for (Expense ex : expenses) {
            model.addRow(new Object[]{ex.getId(), ex.getName(), ex.getAmount()});
        }
    }

    private void addExpense() {
        String name = JOptionPane.showInputDialog(this, "Megnevezés:");
        if (name == null || name.isEmpty()) return;

        String amountStr = JOptionPane.showInputDialog(this, "Összeg (Ft):");
        try {
            double amount = Double.parseDouble(amountStr);
            db.addExpense(name, amount);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Érvénytelen összeg!", "Hiba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);
        db.deleteExpense(id);
        refreshTable();
    }
}
