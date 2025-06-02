package com.motorph.employeeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * A panel that lets you view/create/update/delete leave requests
 * (backed by a simple leaverequests.csv file).
 */
public class ValidatedLeaveRequestGUI extends JPanel {
    // Form fields for “Submit Leave Request”
    private JTextField employeeIdField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField reasonField;
    private JButton submitButton;

    // Table + model for displaying leave requests
    private DefaultTableModel leaveTableModel;
    private JTable leaveTable;

    // Domain model (passed in by AppGUI)
    private Department dept;

    public ValidatedLeaveRequestGUI(Department dept) {
        this.dept = dept;
        setLayout(new BorderLayout());

        // 1) Build the table model and JTable
        leaveTableModel = new DefaultTableModel(
            new Object[]{"Employee ID", "Start Date", "End Date", "Reason"}, 
            0
        );
        leaveTable = new JTable(leaveTableModel);
        JScrollPane scroll = new JScrollPane(leaveTable);
        add(scroll, BorderLayout.CENTER);

        // 2) Build the “Submit / Update / Delete” form below
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

        // Row 1: Start Date (YYYY-MM-DD)
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(15);
        form.add(startDateField, gbc);

        // Row 2: End Date (YYYY-MM-DD)
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(15);
        form.add(endDateField, gbc);

        // Row 3: Reason
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        reasonField = new JTextField(15);
        form.add(reasonField, gbc);

        // Row 4: [ Submit Leave Request ] button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        submitButton = new JButton("Submit Leave Request");
        form.add(submitButton, gbc);

        // Row 5: [ Update Leave Request ] and [ Delete Leave Request ]
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 5;
        JButton updateButton = new JButton("Update Leave Request");
        form.add(updateButton, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        JButton deleteButton = new JButton("Delete Leave Request");
        form.add(deleteButton, gbc);

        // Add the form panel to the bottom
        add(form, BorderLayout.SOUTH);

        // 3) Wire up “Submit Leave Request”
        submitButton.addActionListener(e -> {
            String empId = employeeIdField.getText().trim();
            String startDate = startDateField.getText().trim();
            String endDate   = endDateField.getText().trim();
            String reason    = reasonField.getText().trim();

            // (a) Validate non‐empty
            if (empId.isEmpty() || startDate.isEmpty() ||
                endDate.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "All fields must be filled.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // (b) Optional: Validate date formats, or that endDate >= startDate
            // For simplicity, we only check non-empty here.

            // (c) Append to CSV
            if (appendToCSV(new String[]{empId, startDate, endDate, reason})) {
                JOptionPane.showMessageDialog(
                    this,
                    "Leave request submitted successfully.",
                    "Submitted",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadLeaveRequests();   // reload the table
                clearFormFields();     // clear the text fields
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to write to leaverequests.csv.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // 4) Wire up “Update Leave Request”
        updateButton.addActionListener(e -> openUpdateLeaveDialog());

        // 5) Wire up “Delete Leave Request”
        deleteButton.addActionListener(e -> deleteSelectedLeave());

        // 6) Load existing leave requests into the table
        loadLeaveRequests();
    }

    /** Reads leaverequests.csv and populates the table model. */
    private void loadLeaveRequests() {
        leaveTableModel.setRowCount(0); // clear existing rows
        File csvFile = new File("leaverequests.csv");
        if (!csvFile.exists()) {
            return; // nothing to load yet
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    leaveTableModel.addRow(new Object[]{
                        parts[0], // Employee ID
                        parts[1], // Start Date
                        parts[2], // End Date
                        parts[3]  // Reason
                    });
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error reading leaverequests.csv:\n" + ex.getMessage(),
                "Read Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Appends a new line (EmployeeID,StartDate,EndDate,Reason) to leaverequests.csv. */
    private boolean appendToCSV(String[] data) {
        File file = new File("leaverequests.csv");
        boolean newlyCreated = false;
        try {
            if (!file.exists()) {
                file.createNewFile();
                newlyCreated = true;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                if (newlyCreated) {
                    // Write header
                    bw.write("EmployeeID,StartDate,EndDate,Reason");
                    bw.newLine();
                }
                // Write the new leave request line
                bw.write(String.join(",", data));
                bw.newLine();
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Clears the “Submit Leave Request” text fields. */
    private void clearFormFields() {
        employeeIdField.setText("");
        startDateField.setText("");
        endDateField.setText("");
        reasonField.setText("");
    }

    /** Opens a dialog to update the selected leave request. */
    private void openUpdateLeaveDialog() {
        int selectedRow = leaveTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select a leave request to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Read old values from the selected row
        String oldEmpId    = leaveTableModel.getValueAt(selectedRow, 0).toString();
        String oldStart    = leaveTableModel.getValueAt(selectedRow, 1).toString();
        String oldEnd      = leaveTableModel.getValueAt(selectedRow, 2).toString();
        String oldReason   = leaveTableModel.getValueAt(selectedRow, 3).toString();

        // Build a small form pre-filled with existing data
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Employee ID:"));
        JTextField empIdFld = new JTextField(oldEmpId);
        empIdFld.setEditable(false);
        panel.add(empIdFld);

        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        JTextField startFld = new JTextField(oldStart);
        panel.add(startFld);

        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        JTextField endFld = new JTextField(oldEnd);
        panel.add(endFld);

        panel.add(new JLabel("Reason:"));
        JTextField reasonFld = new JTextField(oldReason);
        panel.add(reasonFld);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Update Leave Request", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return; // user pressed Cancel
        }

        // Validate non‐empty
        String newStart = startFld.getText().trim();
        String newEnd   = endFld.getText().trim();
        String newReason= reasonFld.getText().trim();
        if (newStart.isEmpty() || newEnd.isEmpty() || newReason.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "All fields must be filled.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Overwrite the CSV row for this leave request
        String[] newData = new String[]{oldEmpId, newStart, newEnd, newReason};
        if (overwriteLeaveInCSV(oldEmpId, oldStart, oldEnd, oldReason, newData)) {
            JOptionPane.showMessageDialog(
                this,
                "Leave request updated successfully.",
                "Updated",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadLeaveRequests(); // refresh table
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to update leave request.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Rewrites leaverequests.csv, replacing the line whose columns match
     * (oldEmpId, oldStart, oldEnd, oldReason) with newData[]; returns true if successful.
     */
    private boolean overwriteLeaveInCSV(String oldEmpId,
                                       String oldStart,
                                       String oldEnd,
                                       String oldReason,
                                       String[] newData) {
        File inputFile = new File("leaverequests.csv");
        File tempFile  = new File("leaverequests_temp.csv");
        boolean replaced = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            // Copy header first
            String headerLine = reader.readLine();
            if (headerLine == null) return false;
            writer.write(headerLine);
            writer.newLine();

            // Copy all lines, swapping out the one that matches all four fields
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4 &&
                    parts[0].equals(oldEmpId) &&
                    parts[1].equals(oldStart) &&
                    parts[2].equals(oldEnd) &&
                    parts[3].equals(oldReason)) {
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

    /** Deletes the selected leave request from leaverequests.csv. */
    private void deleteSelectedLeave() {
        int selectedRow = leaveTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select a leave request to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String empIdToDelete = leaveTableModel.getValueAt(selectedRow, 0).toString();
        String startToDelete = leaveTableModel.getValueAt(selectedRow, 1).toString();
        String endToDelete   = leaveTableModel.getValueAt(selectedRow, 2).toString();
        String reasonToDelete= leaveTableModel.getValueAt(selectedRow, 3).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this leave request?\n"
            + "Employee ID = " + empIdToDelete
            + ", Start = " + startToDelete
            + ", End = " + endToDelete
            + ", Reason = " + reasonToDelete + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (removeLeaveFromCSV(empIdToDelete, startToDelete, endToDelete, reasonToDelete)) {
            JOptionPane.showMessageDialog(
                this,
                "Leave request deleted successfully.",
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadLeaveRequests(); // refresh table
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to delete leave request.",
                "File Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Reads leaverequests.csv and writes all lines except the one whose columns match
     * (empId, start, end, reason) to a temp file, then replaces the original.
     * Returns true if a line was removed.
     */
    private boolean removeLeaveFromCSV(String empId, String start, String end, String reason) {
        File inputFile = new File("leaverequests.csv");
        File tempFile  = new File("leaverequests_temp.csv");
        boolean removed = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            // Copy header
            String headerLine = reader.readLine();
            if (headerLine == null) return false;
            writer.write(headerLine);
            writer.newLine();

            // Copy all lines except the one matching all four fields
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4 &&
                    parts[0].equals(empId) &&
                    parts[1].equals(start) &&
                    parts[2].equals(end) &&
                    parts[3].equals(reason)) {
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
