import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpenseFrame extends JFrame {
    private Database db;
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel; // Összeg megjelenítéséhez

    public ExpenseFrame() {
        db = new Database();

        setTitle("Költségelszámoló");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Táblázat modell
        model = new DefaultTableModel(new Object[]{"ID", "Megnevezés", "Összeg (Ft)"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Gombok
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Hozzáadás");
        JButton deleteButton = new JButton("Törlés");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.NORTH);

        // 🔹 Összes költség megjelenítése (alul)
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Összes költség: 0 Ft");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel);
        add(totalPanel, BorderLayout.SOUTH);

        // 🔹 Táblázat feltöltése (a totalLabel már létezik!)
        refreshTable();

        // 🔹 Eseménykezelők
        addButton.addActionListener(e -> addExpense());
        deleteButton.addActionListener(e -> deleteExpense());

        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Expense> expenses = db.getAllExpenses();

        double total = 0;
        for (Expense exp : expenses) {
            model.addRow(new Object[]{exp.getId(), exp.getName(), exp.getAmount()});
            total += exp.getAmount();
        }

        // Összeg frissítése a labelben
        totalLabel.setText(String.format("Összes költség: %.2f Ft", total));
    }

    private void addExpense() {
        String name = JOptionPane.showInputDialog(this, "Megnevezés:");
        if (name == null || name.isBlank()) return;

        String amountStr = JOptionPane.showInputDialog(this, "Összeg (Ft):");
        if (amountStr == null || amountStr.isBlank()) return;

        try {
            double amount = Double.parseDouble(amountStr);
            db.addExpense(name, amount);
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Érvénytelen összeg!");
        }
    }

    private void deleteExpense() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Válassz ki egy sort a törléshez!");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        db.deleteExpense(id);
        refreshTable();
    }
}
