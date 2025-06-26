package com.university.app.model;

public class Section {
    private String courseId;
    private int secId;
    private String semester;
    private int year;
    private String building;
    private int roomNumber;
    private int timeSlotId;

    public Section(String courseId, int secId, String semester, int year, String building, int roomNumber, int timeSlotId) {
        this.courseId = courseId;
        this.secId = secId;
        this.semester = semester;
        this.year = year;
        this.building = building;
        this.roomNumber = roomNumber;
        this.timeSlotId = timeSlotId;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getSecId() {
        return secId;
    }

    public void setSecId(int secId) {
        this.secId = secId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

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

    public int getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(int timeSlotId) {
        this.timeSlotId = timeSlotId;
    }
} 