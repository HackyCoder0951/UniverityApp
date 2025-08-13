package com.university.app.model;

import java.sql.Time;

public class TimeSlot {
    private int timeSlotId;
    private String day;
    private Time startTime;
    private Time endTime;

    public TimeSlot(int timeSlotId, String day, Time startTime, Time endTime) {
        this.timeSlotId = timeSlotId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public int getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(int timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return day + " " + startTime + " - " + endTime;
    }
} 