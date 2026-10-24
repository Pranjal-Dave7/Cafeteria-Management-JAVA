import java.sql.*;
import java.util.*;

// ---------- INTERFACE ----------
interface Billable {
    double calculateTotal(List<MenuItem> menu, String input);
}

// ---------- ABSTRACT CLASS ----------
abstract class AbstractOrder implements Billable {
    protected double total;

    public abstract void displayBill();
}

// ---------- DB CONNECTION ----------
class DBConnection {
    static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cafeteria", "root", "BestHacker@777");
    }
}

// ---------- MODEL ----------
class MenuItem {
    private int id;
    private String name;
    private double price;

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// ---------- MENU SERVICE ----------
class MenuService {

    public static List<MenuItem> getMenu(String store) {
        List<MenuItem> list = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM " + store;
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new MenuItem(
                        rs.getInt("ItemID"),
                        rs.getString("ItemName"),
                        rs.getDouble("Price")));
            }

        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
        }

        return list;
    }

    // 🔥 ALIGNED MENU
    public static void displayMenu(List<MenuItem> menu) {

        System.out.println("\n---------------------------------------------------");
        System.out.printf("%-10s %-27s %-10s\n", "ItemID", "Item Name", "Price");
        System.out.println("---------------------------------------------------");

        for (MenuItem m : menu) {
            System.out.printf("%-10d %-27s Rs. %-10.2f\n",
                    m.getId(), m.getName(), m.getPrice());
        }

        System.out.println("---------------------------------------------------");
    }
}

// ---------- ORDER CLASS (INHERITANCE + OVERRIDING) ----------
class Order extends AbstractOrder {

    @Override
    public double calculateTotal(List<MenuItem> menu, String input) {

        total = 0;
        String[] items = input.trim().split(" ");

        for (String s : items) {
            int id = Integer.parseInt(s);

            for (MenuItem m : menu) {
                if (m.getId() == id) {
                    total += m.getPrice();
                }
            }
        }

        return total;
    }

    @Override
    public void displayBill() {
        System.out.println("\nOrder Total: Rs. " + total);
    }
}

// ---------- INTERFACE ----------
interface TableAllocator {
    boolean allocate(int members, Scanner sc);
}

// ---------- BASE CLASS ----------
class Table {
    protected int id;
    protected int capacity;
    protected long occupiedTime;
    protected boolean isOccupied;

    public Table(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.isOccupied = false;
    }
}

// ---------- CHILD CLASS ----------
class DiningTable extends Table {

    public DiningTable(int id, int capacity) {
        super(id, capacity);
    }
}

// ---------- TABLE MANAGER (IMPLEMENT INTERFACE) ----------
class TableManager implements TableAllocator {

    private List<Table> tables = new ArrayList<>();

    public TableManager() {

        for (int i = 1; i <= 10; i++) {
            tables.add(new DiningTable(i, 4));
        }

        for (int i = 11; i <= 13; i++) {
            tables.add(new DiningTable(i, 6));
        }
    }

    private void updateTableStatus() {
        long currentTime = System.currentTimeMillis();

        for (Table t : tables) {
            if (t.isOccupied) {
                long diff = currentTime - t.occupiedTime;

                if (diff >= 2* 60 * 1000) {
                    t.isOccupied = false;
                }
            }
        }
    }

    @Override
    public boolean allocate(int members, Scanner sc) {

        updateTableStatus();

        for (Table t : tables) {
            if (!t.isOccupied && t.capacity >= members) {

                t.isOccupied = true;
                t.occupiedTime = System.currentTimeMillis();

                System.out.println("Table " + t.id + " allocated.");
                return true;
            }
        }

        System.out.println("All tables are occupied.");
       // System.out.println("Please wait for 20 minutes and a table will be available");
        System.out.println("Switch to takeaway? (yes/no)");

        String choice = sc.next();

        //return choice.equalsIgnoreCase("yes") ? false : false;
        if (choice.equalsIgnoreCase("yes")) {
        // Return true because we are proceeding with the order as takeaway
        return true; 
    } else {
        // User said 'no', so now we tell them to wait
        System.out.println("Please wait for 20 minutes and a table will be available.");
        return false; 
    }
    }
}

// ---------- MAIN CLASS ----------
public class MainApp {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        TableAllocator allocator = new TableManager();

        while (true) {

            System.out.println("\nPDEU CAFETERIA");
            System.out.println("1. Freezeland");
            System.out.println("2. Apna Adda");
            System.out.println("3. Mumbaiya");
            System.out.println("4. Nescafe");
            System.out.println("5. Exit");

            System.out.print("Enter choice (1-5): ");
            int choice = sc.nextInt();

            if (choice == 5)
                break;

            String store = "";

            switch (choice) {
                case 1:
                    store = "Freezeland";
                    break;
                case 2:
                    store = "Apna_Adda";
                    break;
                case 3:
                    store = "Mumbaiya";
                    break;
                case 4:
                    store = "Nescafe";
                    break;
                default:
                    store = "";
            }

            boolean dineIn = false;

            // 🔥 POLYMORPHISM (Interface reference)

            if (store.equals("Freezeland")) {

                System.out.println("\n1. Dine-in");
                System.out.println("2. Takeaway");
                System.out.print("Enter choice (1-2): ");
                int type = sc.nextInt();

                if (type == 1) {
                    System.out.print("\nEnter members: ");
                    int members = sc.nextInt();

                    boolean proceedWithOrder = allocator.allocate(members, sc);

                    if(!proceedWithOrder){
                        System.out.println("Returning to main menu..");
                        continue;
                    }
                        dineIn = false;
                }
            }

            List<MenuItem> menu = MenuService.getMenu(store);

            if (menu.isEmpty()) {
                System.out.println("No items.");
                continue;
            }

            MenuService.displayMenu(menu);

            sc.nextLine();

            System.out.println("\nEnter item numbers:");
            String input = sc.nextLine();

            System.out.println("Enter comments:");
            String comments = sc.nextLine();

            // 🔥 POLYMORPHISM (Abstract reference)
            AbstractOrder order = new Order();

            order.calculateTotal(menu, input);
            order.displayBill();

            System.out.println("Proceed to payment? (yes/no)");
            String pay = sc.next();

            if (pay.equalsIgnoreCase("yes")) {
                System.out.println("Order Complete. Thankyou.");
            }
        }
    }
}
