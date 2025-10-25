import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:expenses.db";

    public Database() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "amount REAL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM expenses")) {

            while (rs.next()) {
                list.add(new Expense(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addExpense(String name, double amount) {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO expenses (name, amount) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteExpense(int id) {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM expenses WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
