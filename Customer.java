package com.example.fx1;



import java.time.LocalDate;
import java.time.Period;

public class Customer {

    private int customerId;
    private String name;
    private LocalDate dateOfBirth;
    private int age;
    private String phone;
    private String address;

    // Constructor
    public Customer(int customerId, String name, LocalDate dateOfBirth, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.address = address;
    }
    // Method to calculate and update age
    void updateAge() {
        if (this.dateOfBirth != null) {
            this.age = Period.between(this.dateOfBirth, LocalDate.now()).getYears();
        }
    }
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public boolean matchesKeyword(String keyword) {
        keyword = keyword.trim().toLowerCase();

        return String.valueOf(customerId).contains(keyword) ||
                name.toLowerCase().contains(keyword) ||
                dateOfBirth.toString().contains(keyword) ||
                phone.toLowerCase().contains(keyword) ||
                address.toLowerCase().contains(keyword);
        // You can add more fields to be checked based on your requirements.
    }
}