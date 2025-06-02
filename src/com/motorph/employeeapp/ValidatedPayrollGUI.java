package com.motorph.employeeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * A panel that lets you view/create/update/delete payroll entries
 * (backed by a simple payroll.csv file).
 */
public class ValidatedPayrollGUI extends JPanel {
    // Form fields for “Add Payroll”
    private JTextField employeeIdField;
    private JTextField amountField;
    private JButton addButton;

    // Table + model for displaying payroll entries
    private DefaultTableModel payrollTableModel;
    private JTable payrollTable;

    // Domain model (passed in by AppGUI; not strictly used here)
    private Department dept;

    public ValidatedPayrollGUI(Department dept) {
        this.dept = dept;
        setLayout(new BorderLayout());

        // 1) Build the table model and JTable
        payrollTableModel = new DefaultTableModel(
            new Object[]{"Employee ID", "Amount"}, 
            0
        );
        payrollTable = new JTable(payrollTableModel);
        JScrollPane scroll = new JScrollPane(payrollTable);
        add(scroll, BorderLayout.CENTER);

        // 2) Build the “Add / Update / Delete” form below
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Employee ID
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        employeeIdField = new JTextField(15);
        form.add(employeeIdField, gbc);

        // Row 1: Amount
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        form.add(amountField, gbc);

        // Row 2: [ Add Payroll ] button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addButton = new JButton("Add Payroll");
        form.add(addButton, gbc);

        // Row 3: [ Update Payroll ] and [ Delete Payroll ]
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 3;
        JButton updateButton = new JButton("Update Payroll");
        form.add(updateButton, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JButton deleteButton = new JButton("Delete Payroll");
        form.add(deleteButton, gbc);

        // Add the form panel to the bottom
        add(form, BorderLayout.SOUTH);

        // 3) Wire up the “Add Payroll” logic
        addButton.addActionListener(e -> {
            String empId = employeeIdField.getText().trim();
            String amountText = amountField.getText().trim();

            // (a) Validate non‐empty
            if (empId.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Both Employee ID and Amount must be filled.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // (b) Optionally validate that Amount is numeric
            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Amount must be a number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // (c) Append to CSV
            if (appendToCSV(new String[]{empId, String.valueOf(amount)})) {
                JOptionPane.showMessageDialog(
                    this,
                    "Payroll entry added successfully.",
                    "Added",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadPayroll();       // reload the table
                clearFormFields();   // clear the text fields
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to write to payroll.csv.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // 4) Wire up “Update Payroll”
        updateButton.addActionListener(e -> openUpdatePayrollDialog());

        // 5) Wire up “Delete Payroll”
        deleteButton.addActionListener(e -> deleteSelectedPayroll());

        // 6) Load existing payroll entries into the table
        loadPayroll();
    }

    /** Reads payroll.csv and populates the table model. */
    private void loadPayroll() {
        payrollTableModel.setRowCount(0); // clear existing rows
        File csvFile = new File("payroll.csv");
        if (!csvFile.exists()) {
            return; // nothing to load yet
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    payrollTableModel.addRow(new Object[]{
                        parts[0], // Employee ID
                        parts[1]  // Amount
                    });
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error reading payroll.csv:\n" + ex.getMessage(),
                "Read Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Appends a new line (EmployeeID,Amount) to payroll.csv. */
    private boolean appendToCSV(String[] data) {
        File file = new File("payroll.csv");
        boolean newlyCreated = false;
        try {
            if (!file.exists()) {
                file.createNewFile();
                newlyCreated = true;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                if (newlyCreated) {
                    // Write header
                    bw.write("EmployeeID,Amount");
                    bw.newLine();
                }
                // Write the new payroll line
                bw.write(String.join(",", data));
                bw.newLine();
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Clears the “Add Payroll” text fields. */
    private void clearFormFields() {
        employeeIdField.setText("");
        amountField.setText("");
    }

    /** Opens a dialog to update the selected payroll entry. */
    private void openUpdatePayrollDialog() {
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select a payroll entry to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Read old values from the selected row
        String oldEmpId = payrollTableModel.getValueAt(selectedRow, 0).toString();
        String oldAmount = payrollTableModel.getValueAt(selectedRow, 1).toString();

        // Build a small form pre-filled with existing data
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Employee ID:"));
        JTextField empIdFld = new JTextField(oldEmpId);
        empIdFld.setEditable(false);
        panel.add(empIdFld);

        panel.add(new JLabel("Amount:"));
        JTextField amtFld = new JTextField(oldAmount);
        panel.add(amtFld);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Update Payroll", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return; // user pressed Cancel
        }

        // Validate the new amount
        String newAmtText = amtFld.getText().trim();
        if (newAmtText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Amount must be filled.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double newAmt;
        try {
            newAmt = Double.parseDouble(newAmtText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Amount must be a number.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Overwrite the CSV row for this payroll entry
        String[] newData = new String[]{oldEmpId, String.valueOf(newAmt)};
        if (overwritePayrollInCSV(oldEmpId, oldAmount, newData)) {
            JOptionPane.showMessageDialog(
                this,
                "Payroll updated successfully.",
                "Updated",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadPayroll(); // refresh table
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to update payroll.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Rewrites payroll.csv, replacing the line whose columns match (oldEmpId, oldAmount)
     * with newData[]; returns true if successful.
     */
    private boolean overwritePayrollInCSV(String oldEmpId, String oldAmount, String[] newData) {
        File inputFile = new File("payroll.csv");
        File tempFile  = new File("payroll_temp.csv");
        boolean replaced = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            // Copy header first
            String headerLine = reader.readLine();
            if (headerLine == null) return false;
            writer.write(headerLine);
            writer.newLine();

            // Copy all lines, swapping out the one that matches oldEmpId & oldAmount
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2 &&
                    parts[0].equals(oldEmpId) && parts[1].equals(oldAmount)) {
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

    /** Deletes the selected payroll entry from payroll.csv. */
    private void deleteSelectedPayroll() {
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select a payroll entry to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String empIdToDelete = payrollTableModel.getValueAt(selectedRow, 0).toString();
        String amtToDelete   = payrollTableModel.getValueAt(selectedRow, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete payroll entry:\nEmployee ID = " 
                + empIdToDelete + ", Amount = " + amtToDelete + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (removePayrollFromCSV(empIdToDelete, amtToDelete)) {
            JOptionPane.showMessageDialog(
                this,
                "Payroll entry deleted successfully.",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadPayroll(); // refresh table
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to delete payroll entry.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Reads payroll.csv and writes all lines except the one whose columns match (empId, amount)
     * to a temp file, then replaces the original. Returns true if a line was removed.
     */
    private boolean removePayrollFromCSV(String empId, String amount) {
        File inputFile = new File("payroll.csv");
        File tempFile  = new File("payroll_temp.csv");
        boolean removed = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            // Copy header
            String headerLine = reader.readLine();
            if (headerLine == null) return false;
            writer.write(headerLine);
            writer.newLine();

            // Copy all lines except the one matching empId & amount
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2 &&
                    parts[0].equals(empId) && parts[1].equals(amount)) {
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
