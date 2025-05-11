package com.motorph.employeeapp;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String employeeID;
    private String name;
    private String position;
    private Department dept;
    private List<Payroll> payrollHistory = new ArrayList<>();
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    public Employee(String employeeID, String name, String position, Department dept) {
        this.employeeID = employeeID;
        this.name = name;
        this.position = position;
        this.dept = dept;
        dept.addEmployee(this);
    }

    public String getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public Department getDept() { return dept; }
    public List<Payroll> getPayrollHistory() { return payrollHistory; }
    public List<LeaveRequest> getLeaveRequests() { return leaveRequests; }

    public void setPosition(String position) { this.position = position; }
    public void setDept(Department dept) {
        if (this.dept != null) this.dept.removeEmployee(this);
        this.dept = dept;
        dept.addEmployee(this);
    }

    public void addPayroll(Payroll p) { payrollHistory.add(p); }
    public void submitLeaveRequest(LeaveRequest lr) { leaveRequests.add(lr); }
}