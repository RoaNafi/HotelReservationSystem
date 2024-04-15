package com.example.fx1;


import java.math.BigDecimal;
import java.util.List;


public class Room {
    private int roomId;
    private boolean isAvailable;
    private String roomType;
    private List<String> roomAmenities;
    private int floorNumber;
    private int maximumOccupancy;
    private BigDecimal roomRate;

    public Room  ( int roomId , String roomType , List<String> roomAmenities,int floorNumber,int maximumOccupancy,BigDecimal roomRate,boolean isAvailable){
        this.roomId=roomId;
        this.roomType=roomType;
        this.roomAmenities=roomAmenities;
        this.floorNumber=floorNumber;
        this.maximumOccupancy=maximumOccupancy;
        this.roomRate=roomRate;
        this.isAvailable=isAvailable;
    }

    public Room() {

    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }



    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public List<String> getRoomAmenities() {
        return roomAmenities;
    }

    public void setRoomAmenities(List<String> roomAmenities) {
        this.roomAmenities = roomAmenities;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getMaximumOccupancy() {
        return maximumOccupancy;
    }

    public void setMaximumOccupancy(int maximumOccupancy) {
        this.maximumOccupancy = maximumOccupancy;
    }

    public BigDecimal getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(BigDecimal roomRate) {
        this.roomRate = roomRate;
    }

// Constructors, getters, setters, and other methods...

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    public boolean matchesKeyword(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();

        return (roomType != null && roomType.toLowerCase().contains(lowerCaseKeyword)) ||
                (roomAmenities != null && roomAmenities.stream().anyMatch(amenity -> amenity.toLowerCase().contains(lowerCaseKeyword))) ||
                (String.valueOf(floorNumber).contains(lowerCaseKeyword)) ||
                (String.valueOf(maximumOccupancy).contains(lowerCaseKeyword)) ||
                (String.valueOf(roomRate).contains(lowerCaseKeyword)) ||
                (Boolean.toString(isAvailable).toLowerCase().contains(lowerCaseKeyword));
    }


}