package motorphemployeeapp;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI launcher for the MotorPH Employee App,
 * now with a simple login screen.
 */
public class AppGUI {

    public static void main(String[] args) {
        // 1) Show Login Dialog first
        JFrame dummy = new JFrame();
        dummy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dummy.setSize(0, 0);
        dummy.setLocationRelativeTo(null);

        LoginDialog loginDlg = new LoginDialog(dummy);
        loginDlg.setVisible(true);

        if (!loginDlg.isSucceeded()) {
            System.exit(0);
        }

        // 2) Show main application window
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH Employee App (GUI Version)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Department hrDept = new Department("D001", "Human Resources");

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Employees", new ValidatedEmployeeGUI(hrDept));
            tabs.addTab("Payroll", new ValidatedPayrollGUI(hrDept));
            tabs.addTab("Leave Requests", new ValidatedLeaveRequestGUI(hrDept));

            frame.add(tabs);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static class Department {
        private String code;
        private String name;

        public Department(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
    }

    private static class ValidatedEmployeeGUI extends JPanel {
        public ValidatedEmployeeGUI(Department hrDept) {
            setLayout(new BorderLayout());
            add(new JLabel("Employee Management - Dept: " + hrDept.getName()), BorderLayout.CENTER);
        }
    }

    private static class ValidatedLeaveRequestGUI extends JPanel {
        public ValidatedLeaveRequestGUI(Department hrDept) {
            setLayout(new BorderLayout());
            add(new JLabel("Leave Requests - Dept: " + hrDept.getName()), BorderLayout.CENTER);
        }
    }

    private static class ValidatedPayrollGUI extends JPanel {
        public ValidatedPayrollGUI(Department hrDept) {
            setLayout(new BorderLayout());
            add(new JLabel("Payroll Management - Dept: " + hrDept.getName()), BorderLayout.CENTER);
        }
    }

    /**
     * A simple modal login dialog.
     * Hard‐coded to accept "admin" / "admin123" but you can change those credentials here.
     */
    static final class LoginDialog extends JDialog {
        private JTextField userField;
        private JPasswordField passField;
        private boolean succeeded;

        private static final String VALID_USERNAME = "admin";
        private static final String VALID_PASSWORD = "admin123";

        public LoginDialog(Frame parent) {
            super(parent, "Login", true);

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            panel.add(new JLabel("Username:"));
            userField = new JTextField(20);
            panel.add(userField);

            panel.add(new JLabel("Password:"));
            passField = new JPasswordField(20);
            panel.add(passField);

            JButton loginBtn = new JButton("Login");
            JButton cancelBtn = new JButton("Cancel");

            JPanel buttons = new JPanel();
            buttons.add(loginBtn);
            buttons.add(cancelBtn);

            loginBtn.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());

                if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
                    succeeded = true;
                    dispose();
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

            cancelBtn.addActionListener(e -> {
                succeeded = false;
                dispose();
            });

            getContentPane().add(panel, BorderLayout.CENTER);
            getContentPane().add(buttons, BorderLayout.SOUTH);
            pack();
            setResizable(false);
            setLocationRelativeTo(parent);
        }

        public boolean isSucceeded() {
            return succeeded;
        }
    }
}
