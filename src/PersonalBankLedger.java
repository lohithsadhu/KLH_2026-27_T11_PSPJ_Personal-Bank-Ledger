import java.io.*;
import java.util.*;
public class PersonalBankLedger {
    static Scanner sc = new Scanner(System.in);
    static File dataFile = new File("data/transactions.csv");
    static File reportFile = new File("reports/bank_statement.txt");
    ArrayList<Transaction> transactions = new ArrayList<>();
    int nextId = 1;
    public static void main(String[] args) {
        PersonalBankLedger app = new PersonalBankLedger();
        try {
            new File("data").mkdirs();
            new File("reports").mkdirs();
            if (dataFile.createNewFile()) {
                System.out.println("Transaction file created.");
            }
            if (reportFile.createNewFile()) {
                System.out.println("Report file created.");
            }
            app.loadTransactions();
            app.run();
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
        sc.close();
    }
    void run() {
        int choice;
        do {
            System.out.println("\n===== PERSONAL BANK LEDGER =====");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. View Current Balance");
            System.out.println("6. View Transaction History");
            System.out.println("7. Search Transactions");
            System.out.println("8. Filter Transactions");
            System.out.println("9. Sort Transactions");
            System.out.println("10. Generate Bank Statement");
            System.out.println("11. View Financial Summary");
            System.out.println("12. Exit");
            choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: addTransaction("Income"); break;
                case 2: addTransaction("Expense"); break;
                case 3: addTransaction("Deposit"); break;
                case 4: addTransaction("Withdrawal"); break;
                case 5: viewBalance(); break;
                case 6: displayTransactions(transactions); break;
                case 7: searchTransactions(); break;
                case 8: filterTransactions(); break;
                case 9: sortTransactions(); break;
                case 10: generateStatement(); break;
                case 11: financialSummary(); break;
                case 12:
                    System.out.println("Thank you for using the application!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 12);
    }
    static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }
    static double readAmount(String message) {
        while (true) {
            System.out.print(message);
            try {
                double amount = Double.parseDouble(sc.nextLine().trim());

                if (Double.isFinite(amount) && amount > 0) {
                    return amount;
                }

                System.out.println("Amount must be a positive, finite number.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount.");
            }
        }
    }
    static String todayDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
    void addTransaction(String type) {
        double amount = readAmount("Enter amount: ");
        System.out.print("Enter description: ");
        String description = sc.nextLine();
        if ((type.equals("Expense") || type.equals("Withdrawal")) && amount > getBalance()) {
            System.out.println("Insufficient balance.");
            return;
        }
        Transaction t = new Transaction(nextId, todayDate(), type, amount, description);
        transactions.add(t);
        nextId++;
        updateBalances();
        try {
            saveTransactions();
            System.out.println(type + " added successfully.");
            viewBalance();
        } catch (IOException e) {
            System.out.println("Unable to save transaction.");
        }
    }
    void updateBalances() {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t.type.equals("Income") || t.type.equals("Deposit")) {
                balance += t.amount;
            } else {
                balance -= t.amount;
            }
            t.balanceAfter = balance;
        }
    }
    double getBalance() {
        if (transactions.isEmpty()) {
            return 0;
        }
        return transactions.get(transactions.size() - 1).balanceAfter;
    }
    void viewBalance() {
        System.out.printf("Current balance: %.2f%n", getBalance());
    }
    void displayTransactions(ArrayList<Transaction> list) {
        if (list.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.printf("%-5s %-12s %-12s %10s %10s %-20s%n",  "ID", "Date", "Type", "Amount", "Balance", "Description");
        for (Transaction t : list) {
            System.out.printf("%-5d %-12s %-12s %10.2f %10.2f %-20s%n",t.id, t.date, t.type, t.amount,t.balanceAfter, t.description);
        }
    }
    void searchTransactions() {
        System.out.println("1. Search by ID");
        System.out.println("2. Search by description");
        System.out.println("3. Search by type");        
        int choice = readInt("Choose option: ");
        ArrayList<Transaction> result = new ArrayList<>();
        if (choice == 1) {
            int id = readInt("Enter transaction ID: ");
            for (Transaction t : transactions) {
                if (t.id == id) {
                    result.add(t);
                }
            }
        } else if (choice == 2) {
            System.out.print("Enter description: ");
            String query = sc.nextLine().toLowerCase();
            for (Transaction t : transactions) {
                if (t.description.toLowerCase().contains(query)) {
                    result.add(t);
                }
            }
        } else if (choice == 3) {
            String type = chooseType();
            if (type != null) {
                for (Transaction t : transactions) {
                    if (t.type.equals(type)) {
                        result.add(t);
                    }
                }
            }
        } else {
            System.out.println("Invalid search option.");
            return;
        }
        displayTransactions(result);
    }
    String chooseType() {
        System.out.println("1. Income");
        System.out.println("2. Expense");
        System.out.println("3. Deposit");
        System.out.println("4. Withdrawal");
        int choice = readInt("Choose type: ");
        String[] types = {
            "Income", "Expense", "Deposit", "Withdrawal"
        };
        if (choice < 1 || choice > 4) {
            System.out.println("Invalid type.");
            return null;
        }
        return types[choice - 1];
    }
    void filterTransactions() {
        System.out.println("1. Filter by type");
        System.out.println("2. Filter by minimum amount");
        System.out.println("3. Filter by date range");
        int choice = readInt("Choose filter: ");
        ArrayList<Transaction> result = new ArrayList<>();
        if (choice == 1) {
            String type = chooseType();
            if (type == null) {
                return;
            }
            for (Transaction t : transactions) {
                if (t.type.equals(type)) {
                    result.add(t);
                }
            }
        } else if (choice == 2) {
            double minimum = readAmount("Minimum amount: ");
            for (Transaction t : transactions) {
                if (t.amount >= minimum) {
                    result.add(t);
                }
            }
        } else if (choice == 3) {
            System.out.print("Start date: ");
            String start = sc.nextLine();
            System.out.print("End date: ");
            String end = sc.nextLine();
            if (end.compareTo(start) < 0) {
                System.out.println("Invalid date range.");
                return;
            }
            for (Transaction t : transactions) {
                if (t.date.compareTo(start) >= 0
                        && t.date.compareTo(end) <= 0) {
                    result.add(t);
                }
            }
        } else {
            System.out.println("Invalid filter option.");
            return;
        }
        displayTransactions(result);
    }
    void sortTransactions() {
        System.out.println("1. Amount: Low to High");
        System.out.println("2. Amount: High to Low");
        System.out.println("3. Date: Oldest First");
        System.out.println("4. Date: Newest First");
        System.out.println("5. Transaction ID");
        int choice = readInt("Choose sorting method: ");
        ArrayList<Transaction> sorted = new ArrayList<>(transactions);
        if (choice == 1) {
            sorted.sort(Comparator.comparingDouble(t -> t.amount));
        } else if (choice == 2) {
            sorted.sort(Comparator.comparingDouble(
                    (Transaction t) -> t.amount).reversed());
        } else if (choice == 3) {
            sorted.sort(Comparator.comparing(t -> t.date));
        } else if (choice == 4) {
            sorted.sort(Comparator.comparing((Transaction t) -> t.date).reversed());
        } else if (choice == 5) {
            sorted.sort(Comparator.comparingInt(t -> t.id));
        } else {
            System.out.println("Invalid sorting option.");
            return;
        }
        displayTransactions(sorted);
    }
    void generateStatement() {
        System.out.print("Start date (YYYY-MM-DD): ");
        String start = sc.nextLine();
        System.out.print("End date (YYYY-MM-DD): ");
        String end = sc.nextLine();
        if (end.compareTo(start) < 0) {
            System.out.println("Invalid date range.");
            return;
        }
        double credits = 0;
        double debits = 0;
        int count = 0;
        try {
            FileWriter fw = new FileWriter(reportFile);
            fw.write("PERSONAL BANK STATEMENT\n");
            fw.write("Period: " + start + " to " + end + "\n\n");
            for (Transaction t : transactions) {
                if (t.date.compareTo(start) >= 0 && t.date.compareTo(end) <= 0) {
                    fw.write(t.toString() + "\n");
                    count++;
                    if (t.type.equals("Income")
                            || t.type.equals("Deposit")) {
                        credits += t.amount;
                    } else {
                        debits += t.amount;
                    }
                }
            }
            fw.write("\nTotal credits: " + credits);
            fw.write("\nTotal debits: " + debits);
            fw.write("\nNet movement: " + (credits - debits));
            fw.write("\nTransaction count: " + count);
            fw.close();
            System.out.println("Bank statement generated successfully.");
            System.out.println("Saved to reports/bank_statement.txt");
        } catch (IOException e) {
            System.out.println("Error writing statement: " + e.getMessage());
        }
    }
    void financialSummary() {
        double income = 0;
        double expense = 0;
        double deposit = 0;
        double withdrawal = 0;
        for (Transaction t : transactions) {
            if (t.type.equals("Income")) {
                income += t.amount;
            } else if (t.type.equals("Expense")) {
                expense += t.amount;
            } else if (t.type.equals("Deposit")) {
                deposit += t.amount;
            } else {
                withdrawal += t.amount;
            }
        }
        System.out.println("\n===== FINANCIAL SUMMARY =====");
        System.out.printf("Total income: %.2f%n", income);
        System.out.printf("Total expenses: %.2f%n", expense);
        System.out.printf("Total deposits: %.2f%n", deposit);
        System.out.printf("Total withdrawals: %.2f%n", withdrawal);
        System.out.printf("Current balance: %.2f%n", getBalance());
        System.out.println("Transaction count: " + transactions.size());
    }
    void saveTransactions() throws IOException {
        FileWriter fw = new FileWriter(dataFile);
        fw.write("ID|DATE|TYPE|AMOUNT|DESCRIPTION\n");
        for (Transaction t : transactions) {
            String description = t.description.replace("|", "/");
            fw.write(t.id + "|" + t.date + "|" + t.type + "|" + t.amount + "|" + description + "\n");
        }
        fw.close();
    }
    void loadTransactions() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.startsWith("ID|")) {
                        continue;
                    }
                }
                try {
                    String[] fields = line.split("\\|", 5);
                    if (fields.length == 5) {
                        int id = Integer.parseInt(fields[0]);
                        String date = fields[1];
                        String type = fields[2];
                        double amount = Double.parseDouble(fields[3]);
                        String description = fields[4];
                        Transaction t = new Transaction(id, date, type, amount, description);
                        transactions.add(t);
                        if (id >= nextId) {
                            nextId = id + 1;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid record.");
                }
            }
            br.close();
            transactions.sort(Comparator.comparingInt(t -> t.id));
            updateBalances();
            System.out.println("Loaded " + transactions.size() + " transaction(s).");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    static class Transaction {
        int id;
        String date;
        String type;
        double amount;
        String description;
        double balanceAfter;
        Transaction(int id, String date, String type, double amount, String description) {
            this.id = id;
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.description = description;
        }
        public String toString() {
            return "ID: " + id + " | Date: " + date + " | Type: " + type + " | Amount: " + amount + " | Balance: " + balanceAfter + " | Description: " + description;
        }
    }
}