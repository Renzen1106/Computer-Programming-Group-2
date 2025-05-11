package com.motorph.employeeapp;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        Department hr = new Department("D001", "Human Resources");
        Employee alice = new Employee("E001", "Alice", "Manager", hr);
        new Payroll("P001", LocalDate.now(), 50000, 45000, alice);
        LeaveRequest lr1    = new LeaveRequest("L001", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "Vacation", alice);
        lr1.approve();

        System.out.println("Dept " + hr.getName() + " has " + hr.getEmployees().size() + " employee(s).\n"+
                           "Alice has " + alice.getPayrollHistory().size() + " payroll record(s).\n"+
                           "Alice's leave approved? " + lr1.isApproved());
    }
}
