# System Workflow

The system workflow describes how the Personal Bank Ledger & Statement Generator operates, from starting the application to storing transactions and generating reports.

### Step 1: Start the Application
- The user runs the Java program in the VS Code terminal.
- The system loads previously saved transaction records from the CSV file, if available.
- The main menu is displayed.

### Step 2: Display the Main Menu
The system displays the following options:

1. Add Income
2. Add Expense
3. Deposit Money
4. Withdraw Money
5. View Current Balance
6. View Transaction History
7. Search Transactions
8. Filter Transactions
9. Sort Transactions
10. Generate Bank Statement
11. View Financial Summary
12. Exit

### Step 3: Select an Operation
- The user selects an option from the main menu.
- The system checks whether the selected menu option is valid.
- If the selection is invalid, the system displays an appropriate message and allows the user to try again.

### Step 4: Enter Transaction Details
For income, expenses, deposits, and withdrawals, the user enters the required details.

- Transaction date
- Transaction type
- Transaction amount
- Transaction description

The system validates the entered information before processing the transaction.

### Step 5: Process the Transaction
- The system creates a transaction record with a unique transaction ID.
- Income and deposits increase the current balance.
- Expenses and withdrawals decrease the current balance.
- The updated balance is calculated and associated with the transaction.

### Step 6: Save Transaction Data
- The system saves transaction records to `data/transactions.csv`.
- Previously stored transactions are retained.
- The saved records can be loaded when the application starts again.

### Step 7: Perform Ledger Operations
Depending on the user's selection, the system can:

- Display the current balance.
- View the complete transaction history.
- Search for specific transactions.
- Filter transactions by type, amount, or date range.
- Sort transactions by date, amount, or transaction ID.

### Step 8: Generate Bank Statement
- The user selects the bank statement option.
- The system obtains the required date range.
- Transactions within the selected period are retrieved and organized.
- The statement includes transaction details and relevant balance information.
- The generated report is saved as `reports/bank_statement.txt`.

### Step 9: View Financial Summary
- The system calculates total income and expenses.
- It summarizes deposits and withdrawals separately.
- The financial summary is displayed in the terminal to help the user review recorded transactions.

### Step 10: Exit the Application
- The user selects the Exit option.
- The application closes after completing the required operations.
- Saved transaction records remain available for the next session.

---

# Workflow Benefits

- Provides a structured process for managing personal financial records.
- Automatically updates balances after transactions.
- Preserves transaction records using file handling.
- Makes transaction searching, filtering, and sorting easier.
- Generates organized bank statements and financial summaries.
