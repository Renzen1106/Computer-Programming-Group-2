package com.motorph.employeeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * A panel that lets you view/create/update/delete employees
 * (backed by a simple employees.csv file).
 */
public class ValidatedEmployeeGUI extends JPanel {
    // Form fields for “Add Employee”
    private JTextField idField;
    private JTextField nameField;
    private JTextField positionField;
    private JButton addButton;

    // Table + model for displaying employees
    private DefaultTableModel employeeTableModel;
    private JTable employeeTable;

    // Domain model (not strictly used here, but passed in by AppGUI)
    private Department dept;

    public ValidatedEmployeeGUI(Department dept) {
        this.dept = dept;
        setLayout(new BorderLayout());

        // 1) Build the table model and JTable
        employeeTableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Position"}, 
            0
        );
        employeeTable = new JTable(employeeTableModel);
        JScrollPane scroll = new JScrollPane(employeeTable);
        add(scroll, BorderLayout.CENTER);

        // 2) Build the “Add / Update / Delete” form below
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: ID
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(15);
        form.add(idField, gbc);

        // Row 1: Name
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        form.add(nameField, gbc);

        // Row 2: Position
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        positionField = new JTextField(15);
        form.add(positionField, gbc);

        // Row 3: [ Add Employee ] button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addButton = new JButton("Add Employee");
        form.add(addButton, gbc);

        // Row 4: [ Update Employee ] and [ Delete Employee ] side by side
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 4;
        JButton updateButton = new JButton("Update Employee");
        form.add(updateButton, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        JButton deleteButton = new JButton("Delete Employee");
        form.add(deleteButton, gbc);

        // Place the form panel at the bottom (SOUTH)
        add(form, BorderLayout.SOUTH);

        // 3) Wire up the “Add Employee” logic
        addButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String position = positionField.getText().trim();

            // (a) Validate non-empty
            if (id.isEmpty() || name.isEmpty() || position.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "All fields must be filled.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // (b) Check for duplicate ID
            if (employeeExists(id)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Employee ID already exists.",
                    "Duplicate ID",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // (c) Append to CSV
            if (appendToCSV(new String[]{id, name, position})) {
                JOptionPane.showMessageDialog(
                    this,
                    "Employee added successfully.",
                    "Added",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadEmployees();                  // reload the table
                clearFormFields();                // clear the text fields
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to write to employees.csv.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // 4) Wire up “Update Employee”
        updateButton.addActionListener(e -> openUpdateEmployeeDialog());

        // 5) Wire up “Delete Employee”
        deleteButton.addActionListener(e -> deleteSelectedEmployee());

        // 6) Finally, load any existing employees into the table
        loadEmployees();
    }

    /** Reads employees.csv and populates the table model. */
    private void loadEmployees() {
        employeeTableModel.setRowCount(0); // clear existing rows
        File csvFile = new File("employees.csv");
        if (!csvFile.exists()) {
            return; // nothing to load yet
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    employeeTableModel.addRow(new Object[]{
                        parts[0], // ID
                        parts[1], // Name
                        parts[2]  // Position
                    });
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error reading employees.csv:\n" + ex.getMessage(),
                "Read Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Checks if an employee ID already exists in the table (or CSV). */
    private boolean employeeExists(String id) {
        for (int i = 0; i < employeeTableModel.getRowCount(); i++) {
            if (employeeTableModel.getValueAt(i, 0).equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends a new line (ID,Name,Position) to employees.csv.
     * If the file doesn’t exist, it creates it with a header row first.
     * Returns true on success.
     */
    private boolean appendToCSV(String[] data) {
        File file = new File("employees.csv");
        boolean newlyCreated = false;
        try {
            if (!file.exists()) {
                file.createNewFile();
                newlyCreated = true;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                if (newlyCreated) {
                    // Write header
                    bw.write("ID,Name,Position");
                    bw.newLine();
                }
                // Write the new line
                bw.write(String.join(",", data));
                bw.newLine();
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Clears the “Add Employee” text fields. */
    private void clearFormFields() {
        idField.setText("");
        nameField.setText("");
        positionField.setText("");
    }

    /** Opens a dialog to update the selected employee’s record. */
    private void openUpdateEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select an employee to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Read old values from the table
        String oldId = employeeTableModel.getValueAt(selectedRow, 0).toString();
        String oldName = employeeTableModel.getValueAt(selectedRow, 1).toString();
        String oldPosition = employeeTableModel.getValueAt(selectedRow, 2).toString();

        // Build a small form pre-filled with existing data
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Employee ID:"));
        JTextField idFld = new JTextField(oldId);
        idFld.setEditable(false);
        panel.add(idFld);

        panel.add(new JLabel("Name:"));
        JTextField nameFld = new JTextField(oldName);
        panel.add(nameFld);

        panel.add(new JLabel("Position:"));
        JTextField posFld = new JTextField(oldPosition);
        panel.add(posFld);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return; // user pressed Cancel
        }

        // Validate the new fields aren’t empty
        String newName = nameFld.getText().trim();
        String newPos = posFld.getText().trim();
        if (newName.isEmpty() || newPos.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "All fields must be filled.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Overwrite the CSV row for this ID
        String[] newData = new String[]{oldId, newName, newPos};
        if (overwriteEmployeeInCSV(oldId, newData)) {
            JOptionPane.showMessageDialog(
                this,
                "Employee updated successfully.",
                "Updated",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadEmployees(); // refresh table
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to update employee.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Rewrites employees.csv, replacing the line whose first column matches oldId
     * with newData[]; returns true if successful.
     */
    private boolean overwriteEmployeeInCSV(String oldId, String[] newData) {
        File inputFile = new File("employees.csv");
        File tempFile  = new File("employees_temp.csv");
        boolean replaced = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            // Copy header first
            String headerLine = reader.readLine();
            if (headerLine == null) return false;
            writer.write(headerLine);
            writer.newLine();

            // Copy all lines, swapping out the one that matches oldId
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && parts[0].equals(oldId)) {
                    // Replace this line
                    writer.write(String.join(",", newData));
                    replaced = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        // Overwrite the original file with the temp file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            return false;
        }
        return replaced;
    }

    /** Deletes the selected row (employee) from employees.csv. */
    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select an employee to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idToDelete = employeeTableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete Employee ID " + idToDelete + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (removeEmployeeFromCSV(idToDelete)) {
            JOptionPane.showMessageDialog(
                this,
                "Employee deleted successfully.",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadEmployees(); // refresh table
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to delete employee.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Reads employees.csv and writes all lines except the one whose ID == idToDelete
     * to a temp file, then replaces the original. Returns true if a line was removed.
     */
    private boolean removeEmployeeFromCSV(String idToDelete) {
        File inputFile = new File("employees.csv");
        File tempFile  = new File("employees_temp.csv");
        boolean removed = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            // Copy header
            String headerLine = reader.readLine();
            if (headerLine == null) return false;
            writer.write(headerLine);
            writer.newLine();

            // Copy all lines except the one matching idToDelete
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && parts[0].equals(idToDelete)) {
                    removed = true;
                    continue; // skip this line
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        // Overwrite the original file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            return false;
        }
        return removed;
    }
}
