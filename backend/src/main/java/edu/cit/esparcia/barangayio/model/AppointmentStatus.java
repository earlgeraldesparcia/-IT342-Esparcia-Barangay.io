package edu.cit.esparcia.barangayio.model;

public enum AppointmentStatus {
    PENDING("pending", "Pending"),
    APPROVED("approved", "Approved"),
    COMPLETED("completed", "Completed"),
    CLAIMED("claimed", "Claimed"),
    CANCELLED("cancelled", "Cancelled"),
    NO_SHOW("no_show", "No Show");

    private final String value;
    private final String displayName;

    AppointmentStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}
