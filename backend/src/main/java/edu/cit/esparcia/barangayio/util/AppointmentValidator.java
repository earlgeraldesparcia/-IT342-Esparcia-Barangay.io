package edu.cit.esparcia.barangayio.util;

import edu.cit.esparcia.barangayio.model.PurposeEnum;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentValidator {

    public static void validateAppointmentDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Preferred date is required");
        }
        
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book appointments in the past");
        }
    }

    public static void validateAppointmentTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Preferred time is required");
        }
        
        if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(17, 0))) {
            throw new IllegalArgumentException("Appointments are only available between 8:00 AM and 5:00 PM");
        }
    }

    public static void validatePurpose(PurposeEnum purpose, String specifyPurpose) {
        if (purpose == null) {
            throw new IllegalArgumentException("Purpose is required");
        }
        
        if (purpose == PurposeEnum.OTHERS) {
            if (specifyPurpose == null || specifyPurpose.trim().isEmpty()) {
                throw new IllegalArgumentException("Please specify your purpose when selecting 'Others'");
            }
        }
    }

    public static void validateCancellationReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }

    public static void validateRescheduleReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reschedule reason is required");
        }
    }

    public static boolean isBusinessHours(LocalTime time) {
        return !time.isBefore(LocalTime.of(8, 0)) && !time.isAfter(LocalTime.of(17, 0));
    }

    public static int calculateAvailableSlots(long bookedSlots) {
        return Math.max(0, 30 - (int) bookedSlots);
    }
}
