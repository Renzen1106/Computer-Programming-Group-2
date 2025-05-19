package com.motorph.employeeapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ValidatedEmployeeGUI extends JPanel {
    private Department dept;
    private JTextField idField, nameField, positionField;
    private JButton addButton;

    public ValidatedEmployeeGUI(Department dept) {
        this.dept = dept;
        setLayout(new GridLayout(4, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Add Employee"));

        idField       = new JTextField();
        nameField     = new JTextField();
        positionField = new JTextField();

        add(new JLabel("ID:"));       add(idField);
        add(new JLabel("Name:"));     add(nameField);
        add(new JLabel("Position:")); add(positionField);

        addButton = new JButton("Add");
        addButton.addActionListener(this::handleAdd);

        add(new JPanel());
        add(addButton);
    }

    private void handleAdd(ActionEvent e) {
        String id   = idField.getText().trim();
        String name = nameField.getText().trim();
        String pos  = positionField.getText().trim();
        if (id.isEmpty() || name.isEmpty() || pos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new Employee(id, name, pos, dept);
        JOptionPane.showMessageDialog(this, "Employee added!");
        idField.setText(""); nameField.setText(""); positionField.setText("");
    }
}
