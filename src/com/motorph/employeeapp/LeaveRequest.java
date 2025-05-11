package com.motorph.employeeapp;

import java.time.LocalDate;

public class LeaveRequest {
    private String requestID;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private boolean approved;
    private Employee employee;

    public LeaveRequest(String requestID, LocalDate startDate, LocalDate endDate, String reason, Employee employee) {
        this.requestID = requestID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.employee = employee;
        this.approved = false;
        employee.submitLeaveRequest(this);
    }

    public String getRequestID() { return requestID; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public boolean isApproved() { return approved; }
    public Employee getEmployee() { return employee; }

    public void approve() { this.approved = true; }
    public void reject() { this.approved = false; }
}