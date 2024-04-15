package com.example.fx1;
import java.math.BigDecimal;

public class Employee {
    private int employeeId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private BigDecimal salary;
    private String password;
    private String role;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public boolean matchesKeyword(String keyword) {
        // You can modify this method based on how you want to perform the search
        return String.valueOf(employeeId).contains(keyword) ||
                name.toLowerCase().contains(keyword) ||
                phone.contains(keyword) ||
                email.toLowerCase().contains(keyword) ||
                address.toLowerCase().contains(keyword) ||
                String.valueOf(salary).contains(keyword);
    }
}