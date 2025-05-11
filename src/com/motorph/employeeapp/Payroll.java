package com.motorph.employeeapp;

import java.time.LocalDate;

public class Payroll {
    private String payrollID;
    private LocalDate date;
    private double baseSalary;
    private double netPay;
    private Employee employee;

    public Payroll(String payrollID, LocalDate date, double baseSalary, double netPay, Employee employee) {
        this.payrollID = payrollID;
        this.date = date;
        this.baseSalary = baseSalary;
        this.netPay = netPay;
        this.employee = employee;
        employee.addPayroll(this);
    }

    public String getPayrollID() { return payrollID; }
    public LocalDate getDate() { return date; }
    public double getBaseSalary() { return baseSalary; }
    public double getNetPay() { return netPay; }
    public Employee getEmployee() { return employee; }

    public void setNetPay(double netPay) { this.netPay = netPay; }
}