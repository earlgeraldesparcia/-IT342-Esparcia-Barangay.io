package edu.cit.esparcia.barangayio.feature.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

public class RescheduleAppointmentRequest {

    @FutureOrPresent(message = "New date must be in the future or present")
    private LocalDate newDate;
    
    private LocalTime newTime;
    
    @NotBlank(message = "Reschedule reason is required")
    private String reason;

    // Constructors
    public RescheduleAppointmentRequest() {}

    public RescheduleAppointmentRequest(LocalDate newDate, LocalTime newTime, String reason) {
        this.newDate = newDate;
        this.newTime = newTime;
        this.reason = reason;
    }

    // Getters and Setters
    public LocalDate getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDate newDate) {
        this.newDate = newDate;
    }

    public LocalTime getNewTime() {
        return newTime;
    }

    public void setNewTime(LocalTime newTime) {
        this.newTime = newTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
