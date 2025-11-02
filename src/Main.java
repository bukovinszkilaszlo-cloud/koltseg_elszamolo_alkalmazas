import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Modern layout
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(ExpenseFrame::new);
    }
}