package com.motorph.employeeapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

/**
 * Panel for submitting leave requests. Validates input and shows feedback.
 */
public class ValidatedLeaveRequestGUI extends JPanel {
    private Department dept;
    private JTextField empIdField, startDateField, endDateField, reasonField;
    private JButton submitButton;

    public ValidatedLeaveRequestGUI(Department dept) {
        this.dept = dept;
        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Submit Leave Request"));

        empIdField      = new JTextField();
        startDateField  = new JTextField();
        endDateField    = new JTextField();
        reasonField     = new JTextField();

        add(new JLabel("Employee ID:"));            add(empIdField);
        add(new JLabel("Start Date (YYYY-MM-DD):"));add(startDateField);
        add(new JLabel("End Date (YYYY-MM-DD):"));  add(endDateField);
        add(new JLabel("Reason:"));                 add(reasonField);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this::handleSubmit);
        add(new JPanel()); add(submitButton);
    }

    private void handleSubmit(ActionEvent e) {
        String id = empIdField.getText().trim();
        Employee emp = dept.getEmployees().stream()
                          .filter(x -> x.getEmployeeID().equalsIgnoreCase(id))
                          .findFirst()
                          .orElse(null);
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            LocalDate end   = LocalDate.parse(endDateField.getText().trim());
            String reason   = reasonField.getText().trim();
            if (reason.isEmpty()) throw new IllegalArgumentException();

            new LeaveRequest("L" + System.currentTimeMillis(), start, end, reason, emp);
            JOptionPane.showMessageDialog(this, "Leave request submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);

            empIdField.setText(""); startDateField.setText("");
            endDateField.setText(""); reasonField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}