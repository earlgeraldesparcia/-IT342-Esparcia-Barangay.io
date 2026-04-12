package edu.cit.esparcia.barangayio.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelAppointmentRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    // Constructors
    public CancelAppointmentRequest() {}

    public CancelAppointmentRequest(String reason) {
        this.reason = reason;
    }

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
