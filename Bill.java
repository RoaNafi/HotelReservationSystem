package com.example.fx1;


import java.math.BigDecimal;
import java.time.LocalDate;

public class Bill {
    private static int billId;
    private int customerId;
    private LocalDate billDate;
    private String paymentMethod;
    private BigDecimal billAmount;

    public Bill() {
    }

    public Bill(int billId, int customerId, LocalDate billDate, String paymentMethod, BigDecimal billAmount) {
        this.billId = billId;
        this.customerId = customerId;
        this.billDate = billDate;
        this.paymentMethod = paymentMethod;
        this.billAmount = billAmount;
    }

    public static int getBillId() {
        return billId;
    }

    public static void setBillId(int billId) {
        Bill.billId = billId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }
}