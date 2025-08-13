package com.university.app.model;

public class Classroom {
    private String building;
    private int roomNumber;
    private int capacity;

    public Classroom(String building, int roomNumber, int capacity) {
        this.building = building;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
    }

    // Getters and Setters
    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return building + " - Room " + roomNumber + " (Capacity: " + capacity + ")";
    }
} 