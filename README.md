# MotorPH Employee App (GUI Version)

## Overview
This is a Java Swing application that manages Employees, Payroll, and Leave Requests with full Create/Read/Update/Delete (CRUD) functionality. A simple login screen secures access. Data is stored in plain CSV files: `employees.csv`, `payroll.csv`, and `leaverequests.csv`.

---

## Features

### 1. Login Screen
- **Hard-coded credentials:**
  - **Username:** `admin`
  - **Password:** `admin123`
- If login fails or is canceled, the application exits.

---

### 2. Employees Tab
- **Table:** lists all employees (ID, Name, Position) from `employees.csv`.
- **Add Employee:** form at the bottom (ID, Name, Position).
- **Update Employee:** edits the selected row (ID is read-only).
- **Delete Employee:** removes the selected employee after confirmation.

---

### 3. Payroll Tab
- **Table:** lists all payroll entries (Employee ID, Amount) from `payroll.csv`.
- **Add Payroll:** form at the bottom (Employee ID, Amount).
- **Update Payroll:** edits the selected row.
- **Delete Payroll:** removes the selected entry after confirmation.

---

### 4. Leave Requests Tab
- **Table:** lists all leave requests (Employee ID, Start Date, End Date, Reason) from `leaverequests.csv`.
- **Submit Leave Request:** form at the bottom (Employee ID, Start Date, End Date, Reason).
- **Update Leave Request:** edits the selected row.
- **Delete Leave Request:** removes the selected row after confirmation.

---

## Prerequisites
- Java Development Kit (JDK) 8 or higher installed.  
- A terminal/command prompt (PowerShell, CMD, or similar) on Windows, or a shell (bash/zsh) on macOS/Linux.

---

## How to Compile & Run

1. **Open a terminal** and navigate to the project root (where this `README.md` lives). For example:
   ```bash
   cd /path/to/Computer-Programming-Group-2


Compile all Java source files. In Windows PowerShell (or any shell), run:
javac -d . src\com\motorph\employeeapp*.java
If you’re on macOS/Linux, use forward slashes:
javac -d . src/com/motorph/employeeapp/*.java

The -d . flag tells javac to place compiled .class files into the matching package folders under the current directory.

Run the application by specifying the main class’s fully qualified name:
java com.motorph.employeeapp.AppGUI

The login dialog will appear first.

Enter the credentials:
Username: admin
Password: admin123

On successful login, the main window with three tabs appears.

CSV Files
employees.csv

Automatically created when you add the first employee.

Columns: ID,Name,Position

payroll.csv

Automatically created when you add the first payroll entry.

Columns: EmployeeID,Amount

leaverequests.csv

Automatically created when you submit the first leave request.

Columns: EmployeeID,StartDate,EndDate,Reason

If you manually edit any CSV (e.g., in Excel), the next CRUD operation in the app (Add/Update/Delete) will redraw the table to reflect your changes.

Notes & Validation
Employee ID: Must be unique. Attempting to add a duplicate ID shows a warning.

Amount (in Payroll): Must be a valid number (e.g., 1500.00).

Dates (in Leave Requests): Enter in YYYY-MM-DD format. The code does not enforce date ordering, so be sure to use valid dates.

If an invalid value is entered, a friendly JOptionPane error dialog informs you of the issue.

Project Structure
Computer-Programming-Group-2/
├─ src/
│ └─ com/
│ └─ motorph/
│ └─ employeeapp/
│ ├─ AppGUI.java
│ ├─ LoginDialog.java
│ ├─ Department.java
│ ├─ Employee.java
│ ├─ LeaveRequest.java
│ ├─ Payroll.java
│ ├─ ValidatedEmployeeGUI.java
│ ├─ ValidatedPayrollGUI.java
│ └─ ValidatedLeaveRequestGUI.java
├─ employees.csv # (auto-generated on first use)
├─ payroll.csv # (auto-generated on first use)
├─ leaverequests.csv # (auto-generated on first use)
├─ README.md
└─ .gitignore

Author / Contact
Name: Ghian Renzen Arboleda
Email: lr.grarboleda@mmdc.mcl.edu.ph

Name: Catherine Kate Plenos
Email: lr.ckplenos@mmdc.mcl.edu.ph

Feel free to reach out if you encounter any issues or have questions about setup and usage.