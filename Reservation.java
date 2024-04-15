package com.example.fx1;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private int customerId;
    private int roomId;
    private int employeeId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime reservationDateTime;

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }
    public boolean matchesKeyword(String keyword) {
        keyword = keyword.trim().toLowerCase();

        // Check if any relevant field contains the keyword
        return String.valueOf(reservationId).contains(keyword) ||
                String.valueOf(customerId).contains(keyword) ||
                String.valueOf(roomId).contains(keyword) ||
                String.valueOf(employeeId).contains(keyword) ||
                checkInDate.toString().contains(keyword) ||
                checkOutDate.toString().contains(keyword);

    }


}