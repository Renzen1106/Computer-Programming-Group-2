package com.motorph.employeeapp;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI launcher for the MotorPH Employee App,
 * now with a simple login screen.
 */
public class AppGUI {

    public static void main(String[] args) {
        // 1) Show Login Dialog first
        // ----------------------------------------
        // Create an invisible frame just to parent the login dialog
        JFrame dummy = new JFrame();
        dummy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dummy.setSize(0, 0);
        dummy.setLocationRelativeTo(null);

        // Show the login dialog
        LoginDialog loginDlg = new LoginDialog(dummy);
        loginDlg.setVisible(true);

        // If login failed or the user pressed Cancel → exit immediately
        if (!loginDlg.isSucceeded()) {
            System.exit(0);
        }

        // 2) If login succeeded, show the main application window
        // ----------------------------------------
        SwingUtilities.invokeLater(() -> {
            // Create the main window
            JFrame frame = new JFrame("MotorPH Employee App (GUI Version)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Shared domain model (department, etc.)
            Department hrDept = new Department("D001", "Human Resources");

            // Build the tabbed pane exactly as before
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Employees",      new ValidatedEmployeeGUI(hrDept));
            tabs.addTab("Payroll",        new ValidatedPayrollGUI(hrDept));
            tabs.addTab("Leave Requests", new ValidatedLeaveRequestGUI(hrDept));

            // Add the tabbed pane to the frame
            frame.add(tabs);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


/**
 * A simple modal login dialog.
 * Hard‐coded to accept "admin" / "admin123" but you can change those credentials here.
 */
class LoginDialog extends JDialog {
    private JTextField userField;
    private JPasswordField passField;
    private boolean succeeded;

    // Hard‐coded credential (you can replace these or load from a file if you want)
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);

        // Build the center panel (username + password)
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Username:"));
        userField = new JTextField(20);
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        passField = new JPasswordField(20);
        panel.add(passField);

        // Build the button panel (Login, Cancel)
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");

        JPanel buttons = new JPanel();
        buttons.add(loginBtn);
        buttons.add(cancelBtn);

        // Wire up the “Login” button
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
                succeeded = true;
                dispose();  // close the dialog
            } else {
                JOptionPane.showMessageDialog(
                    LoginDialog.this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                userField.setText("");
                passField.setText("");
                succeeded = false;
            }
        });

        // Wire up the “Cancel” button
        cancelBtn.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        // Lay everything out
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    /**
     * Returns true if the login was successful (user clicked “Login” with correct credentials).
     */
    public boolean isSucceeded() {
        return succeeded;
    }
}
