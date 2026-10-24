package mainpackage;

import dbpackage.DBConnection;
import java.sql.*;
import java.util.Scanner;

// =====================================================
// INTERFACE
// A promise that any class which "implements" this
// must provide a calculateTotal method
// =====================================================
interface Bill {
    double calculateTotal(Menu_Item[] menu, int menuSize, String input);
}

// =====================================================
// ABSTRACT CLASS
// Has some things defined, but forces child classes
// to fill in the displayBill() method themselves
// =====================================================
abstract class Abstract_Order implements Bill {
    protected double total; // stores the bill amount

    public abstract void displayBill(); // child class must write this
}

// =====================================================
// MODEL CLASS
// Represents one item on the menu (like a data holder)
// =====================================================
class Menu_Item {
    int id;
    String name;
    double price;

    public Menu_Item(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

// =====================================================
// MENU FETCHER CLASS
// Handles fetching the menu from DB and printing it
// =====================================================
class Menu_Fetcher {

    // Fetches menu items from DB and fills up the menu array
    // Returns how many items were actually loaded
    public static int getMenu(String store, Menu_Item[] menu) {
        int count = 0;
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + store);

            while (rs.next() && count < menu.length) {
                menu[count] = new Menu_Item(
                        rs.getInt("ItemID"),
                        rs.getString("ItemName"),
                        rs.getDouble("Price"));
                count++;
            }
        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
        }
        return count; // tells us how many items were loaded
    }

    // Prints the menu array on the console
    public static void displayMenu(Menu_Item[] menu, int size) {
        System.out.println("\n---------------------------------------------------");
        System.out.printf("%-10s %-27s %-10s\n", "ItemID", "Item Name", "Price");
        System.out.println("---------------------------------------------------");
        for (int i = 0; i < size; i++) {
            System.out.printf("%-10d %-27s Rs. %-10.2f\n",
                    menu[i].id, menu[i].name, menu[i].price);
        }
        System.out.println("---------------------------------------------------");
    }
}

// =====================================================
// ORDER CLASS
// Extends Abstract_Order and fills in the actual logic
// for calculateTotal and displayBill
// =====================================================
class Order extends Abstract_Order {

    // Goes through the input, finds each item in the menu, adds up the price
    // Returns -1 if anything goes wrong (invalid input or ID not found)
    @Override
    public double calculateTotal(Menu_Item[] menu, int menuSize, String input) {
        total = 0;

        if (input.trim().isEmpty()) {
            System.out.println("Error: Input cannot be empty.");
            return -1; // -1 signals something went wrong
        }

        String[] items = input.trim().split("\\s+"); // split by spaces

        for (int i = 0; i < items.length; i++) {
            int id;

            // Try to convert the typed string into a number
            try {
                id = Integer.parseInt(items[i]);
            } catch (NumberFormatException e) {
                System.out.println("Error: '" + items[i] + "' is not a valid number.");
                return -1;
            }

            // Search for that ID in the menu array
            boolean found = false;
            for (int j = 0; j < menuSize; j++) {
                if (menu[j].id == id) {
                    total += menu[j].price;
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Error: Item ID " + id + " does not exist in the menu.");
                return -1;
            }
        }

        return total;
    }

    @Override
    public void displayBill() {
        System.out.println("\nOrder Total: Rs. " + total);
    }
}

// =====================================================
// TABLE CLASS
// Represents one physical table in the cafeteria
// =====================================================
class Table {
    int id, capacity;
    boolean isOccupied;
    long occupiedTime;

    public Table(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.isOccupied = false;
    }
}

// =====================================================
// DINING TABLE CLASS
// Child class of Table — inheritance example
// Can be extended later with dine-in specific features
// =====================================================
class Dine_Table extends Table {
    public Dine_Table(int id, int capacity) {
        super(id, capacity); // calls Table's constructor
    }
}

// =====================================================
// INTERFACE FOR TABLE ALLOCATION
// Any class that implements this must provide allocate()
// =====================================================
interface Table_Allocator {
    boolean allocate(int members, Scanner sc);
}

// =====================================================
// TABLE MANAGER CLASS
// Manages all tables: tracks availability and allocates
// =====================================================
class Table_Manager implements Table_Allocator {
    Table[] tables = new Table[13]; // 13 total tables

    public Table_Manager() {
        // Tables 1-10 have capacity 4
        for (int i = 0; i < 10; i++) {
            tables[i] = new Dine_Table(i + 1, 4);
        }
        // Tables 11-13 have capacity 6
        for (int i = 10; i < 13; i++) {
            tables[i] = new Dine_Table(i + 1, 6);
        }
    }

    // Frees up tables that have been occupied for 2+ minutes (demo timer)
    private void updateTableStatus() {
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isOccupied) {
                long diff = currentTime - tables[i].occupiedTime;
                if (diff >= 2 * 60 * 1000) {
                    tables[i].isOccupied = false;
                }
            }
        }
    }

    @Override
    public boolean allocate(int members, Scanner sc) {
        updateTableStatus();

        // Look for a free table with enough capacity
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isOccupied && tables[i].capacity >= members) {
                tables[i].isOccupied = true;
                tables[i].occupiedTime = System.currentTimeMillis();
                System.out.println("Table " + tables[i].id + " allocated.");
                return true;
            }
        }

        // No table found
        System.out.println("All tables are occupied.");
        System.out.println("Switch to takeaway? (yes/no)");
        String choice = sc.next();

        if (choice.equalsIgnoreCase("yes")) {
            return true;
        } else {
            System.out.println("Please wait 20 minutes for a table to free up.");
            return false;
        }
    }
}

// =====================================================
// MAIN CLASS
// Entry point — handles all user interaction
// =====================================================
public class MainApp {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        Table_Allocator allocator = new Table_Manager();

        while (true) {

            // --- Show main menu ---
            System.out.println("\nPDEU CAFETERIA");
            System.out.println("1. Freezeland");
            System.out.println("2. Apna Adda");
            System.out.println("3. Mumbaiya");
            System.out.println("4. Nescafe");
            System.out.println("5. Exit");
            System.out.print("Enter choice (1-5): ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();
            if (choice == 5)
                break;

            // --- Map choice number to store name ---
            String store;
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
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                    continue;
            }

            // --- Dine-in / Takeaway choice (only for Freezeland) ---
            if (store.equals("Freezeland")) {
                boolean validType = false;

                while (!validType) {
                    System.out.println("\n1. Dine-in\n2. Takeaway");
                    System.out.print("Enter choice (1-2): ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid input. Please enter 1 for Dine-in or 2 for Takeaway.");
                        sc.next();
                        continue;
                    }

                    int type = sc.nextInt();

                    switch (type) {
                        case 1:
                            // --- Get number of members ---
                            boolean validMembers = false;
                            while (!validMembers) {
                                System.out.print("\nEnter number of members: ");
                                if (!sc.hasNextInt()) {
                                    System.out.println("Invalid input. Please enter a valid number.");
                                    sc.next();
                                    continue;
                                }
                                int members = sc.nextInt();
                                if (!allocator.allocate(members, sc)) {
                                    store = ""; // no table available, flag to go back to main menu
                                }
                                validMembers = true;
                            }
                            validType = true;
                            break;
                        case 2:
                            validType = true; // takeaway, nothing extra needed
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter 1 or 2.");
                            break;
                    }
                }
                // No table was available and user didn't switch to takeaway
                if (store.isEmpty())
                    continue;
            }

            // --- Load menu from database into an array ---
            Menu_Item[] menu = new Menu_Item[20]; // max 20 items per store
            int menuSize = Menu_Fetcher.getMenu(store, menu);

            if (menuSize == 0) {
                System.out.println("No items found in " + store + ".");
                continue;
            }

            Menu_Fetcher.displayMenu(menu, menuSize);
            sc.nextLine(); // clear leftover newline from previous nextInt()

            // --- Take order with validation loop ---
            Abstract_Order order = new Order();
            boolean orderDone = false;

            while (!orderDone) {
                System.out.println("\nEnter item IDs separated by space: ");
                String input = sc.nextLine();

                double result = order.calculateTotal(menu, menuSize, input);

                if (result >= 0) {
                    orderDone = true; // valid input, exit loop
                } else {
                    System.out.println("Please enter valid Item IDs from the menu above.");
                }
            }

            order.displayBill();

            // --- Comments and payment ---
            System.out.println("Enter comments: ");
            String comments = sc.nextLine();

            System.out.print("\nProceed to payment? (yes/no): ");
            String pay = sc.next();

            if (pay.equalsIgnoreCase("yes")) {
                System.out.println("Order Complete. Thank you for visiting " + store + "!");
            } else {
                System.out.println("Order Cancelled.");
            }
        }

        System.out.println("Exited Application.");
    }
}
