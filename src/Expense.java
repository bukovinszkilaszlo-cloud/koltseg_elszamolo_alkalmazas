public class Expense {
    private int id;
    private String name;
    private double amount;

    public Expense(int id, String name, double amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }

    public void setName(String name) { this.name = name; }
    public void setAmount(double amount) { this.amount = amount; }
}
