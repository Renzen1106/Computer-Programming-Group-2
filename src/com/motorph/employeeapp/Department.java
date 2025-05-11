package com.motorph.employeeapp;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String departmentID;
    private String name;
    private Employee manager;
    private List<Employee> employees = new ArrayList<>();

    public Department(String departmentID, String name) {
        this.departmentID = departmentID;
        this.name = name;
    }

    public String getDepartmentID() { return departmentID; }
    public String getName() { return name; }
    public Employee getManager() { return manager; }
    public List<Employee> getEmployees() { return employees; }

    public void setName(String name) { this.name = name; }
    public void assignManager(Employee manager) { this.manager = manager; }
    public void addEmployee(Employee e) { if (!employees.contains(e)) employees.add(e); }
    public void removeEmployee(Employee e) { employees.remove(e); }
}
