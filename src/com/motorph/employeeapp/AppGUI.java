package com.motorph.employeeapp;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Main GUI launcher for the MotorPH Employee App.
 */
public class AppGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // create the main window
            JFrame frame = new JFrame("MotorPH Employee App (GUI Version)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // shared domain model
            Department hrDept = new Department("D001", "Human Resources");

            // build the tabbed pane
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Employees",      new ValidatedEmployeeGUI(hrDept));
            tabs.addTab("Payroll",        new ValidatedPayrollGUI(hrDept));
            tabs.addTab("Leave Requests", new ValidatedLeaveRequestGUI(hrDept));

            // add to frame and display
            frame.add(tabs);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
