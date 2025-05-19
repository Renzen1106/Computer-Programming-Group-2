package com.motorph.employeeapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

/**
 * Panel for adding Payroll entries with basic validation.
 */
public class ValidatedPayrollGUI extends JPanel {
    private Department dept;
    private JTextField empIdField, amountField;
    private JButton addButton;

    public ValidatedPayrollGUI(Department dept) {
        this.dept = dept;
        setLayout(new GridLayout(3, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Add Payroll"));

        empIdField  = new JTextField();
        amountField = new JTextField();

        add(new JLabel("Employee ID:")); add(empIdField);
        add(new JLabel("Amount:"));      add(amountField);

        addButton = new JButton("Add");
        addButton.addActionListener(this::handleAdd);

        add(new JPanel());
        add(addButton);
    }

    private void handleAdd(ActionEvent e) {
        String id = empIdField.getText().trim();
        Employee emp = dept.getEmployees().stream()
                        .filter(x -> x.getEmployeeID().equalsIgnoreCase(id))
                        .findFirst().orElse(null);
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double amt = Double.parseDouble(amountField.getText().trim());
            new Payroll("P" + System.currentTimeMillis(), LocalDate.now(), amt, amt, emp);
            JOptionPane.showMessageDialog(this, "Payroll added!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        empIdField.setText(""); amountField.setText("");
    }
}