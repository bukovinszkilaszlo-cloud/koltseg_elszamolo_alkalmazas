import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExpenseFrame extends JFrame {
    private Database db;
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;

    public ExpenseFrame() {
        db = new Database();

        setTitle("Költségelszámoló");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLayout(new BorderLayout());

        // Táblázat
        model = new DefaultTableModel(new Object[]{"ID", "Megnevezés", "Összeg (Ft)"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Gombok
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Hozzáadás");
        JButton deleteButton = new JButton("Törlés");
        JButton exportButton = new JButton("Exportálás Excelbe");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Összes költség label
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Összes költség: 0 Ft");
        totalLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        totalPanel.add(totalLabel);
        add(totalPanel, BorderLayout.SOUTH);


        // Feltöltés
        refreshTable();

        // Eseménykezelők
        addButton.addActionListener(e -> addExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        exportButton.addActionListener(e -> exportToExcel());

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

    private void exportToExcel() {
        List<Expense> expenses = db.getAllExpenses();
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nincs exportálandó adat!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Mentés Excel fájlként");
        fileChooser.setSelectedFile(new java.io.File("koltsegek.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        java.io.File fileToSave = fileChooser.getSelectedFile();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Költségek");

            // Fejléc
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Megnevezés", "Összeg (Ft)"};

            CellStyle boldStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont(); // POI Font teljesen kvalifikált
            font.setBold(true);
            boldStyle.setFont(font);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(boldStyle);
            }

            // Adatok
            int rowNum = 1;
            for (Expense exp : expenses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(exp.getId());
                row.createCell(1).setCellValue(exp.getName());
                row.createCell(2).setCellValue(exp.getAmount());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                workbook.write(fileOut);
            }

            JOptionPane.showMessageDialog(this, "✅ Export sikeres: " + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Hiba az exportálás során: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
