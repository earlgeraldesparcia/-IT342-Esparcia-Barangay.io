package edu.cit.esparcia.barangayio.controller;

import edu.cit.esparcia.barangayio.dto.CancelAppointmentRequest;
import edu.cit.esparcia.barangayio.dto.CreateAppointmentRequest;
import edu.cit.esparcia.barangayio.dto.RescheduleAppointmentRequest;
import edu.cit.esparcia.barangayio.dto.UpdateAppointmentRequest;
import edu.cit.esparcia.barangayio.model.Appointment;
import edu.cit.esparcia.barangayio.model.AppointmentStatus;
import edu.cit.esparcia.barangayio.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173", "http://localhost:3000"})
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<Appointment>> getResidentAppointments(@PathVariable UUID residentId) {
        List<Appointment> appointments = appointmentService.getResidentAppointments(residentId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable UUID id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return ResponseEntity.ok(appointment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        List<Appointment> appointments = switch (status) {
            case PENDING -> appointmentService.getPendingAppointments();
            case APPROVED -> appointmentService.getApprovedAppointments();
            case COMPLETED -> appointmentService.getCompletedAppointments();
            case CANCELLED -> appointmentService.getCancelledAppointments();
            default -> List.of();
        };
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/availability/{date}")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        long appointmentCount = appointmentService.getAppointmentCountByDate(date);
        int availableSlots = Math.max(0, 30 - (int) appointmentCount);
        
        AvailabilityResponse response = new AvailabilityResponse(
            date.toString(),
            availableSlots,
            appointmentCount,
            availableSlots > 0
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/availability/{date}/time/{time}")
    public ResponseEntity<TimeSlotAvailabilityResponse> getTimeSlotAvailability(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
        boolean isAvailable = appointmentService.isTimeSlotAvailable(date, time);
        
        TimeSlotAvailabilityResponse response = new TimeSlotAvailabilityResponse(
            date.toString(),
            time.toString(),
            isAvailable
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody CreateAppointmentRequest request) {
        try {
            Appointment appointment = new Appointment();
            appointment.setResidentId(request.getResidentId());
            appointment.setCertificateType(request.getCertificateType());
            appointment.setAppointmentDate(request.getPreferredDate());
            appointment.setAppointmentTime(request.getPreferredTime());
            appointment.setPurpose(request.getPurpose());
            appointment.setSpecifyPurpose(request.getSpecifyPurpose());

            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable UUID id, 
            @RequestBody UpdateAppointmentRequest request) {
        try {
            Appointment appointmentDetails = new Appointment();
            appointmentDetails.setCertificateType(request.getCertificateType());
            appointmentDetails.setAppointmentDate(request.getPreferredDate());
            appointmentDetails.setAppointmentTime(request.getPreferredTime());
            appointmentDetails.setPurpose(request.getPurpose());
            appointmentDetails.setSpecifyPurpose(request.getSpecifyPurpose());

            Appointment updatedAppointment = appointmentService.updateAppointment(id, appointmentDetails);
            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveAppointment(@PathVariable UUID id) {
        try {
            Appointment appointment = appointmentService.approveAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable UUID id) {
        try {
            Appointment appointment = appointmentService.completeAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}/claim")
    public ResponseEntity<?> claimAppointment(@PathVariable UUID id) {
        try {
            Appointment appointment = appointmentService.claimAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable UUID id, 
            @RequestBody CancelAppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id, request.getReason());
            return ResponseEntity.ok(appointment);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable UUID id, 
            @RequestBody RescheduleAppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.rescheduleAppointment(
                id, 
                request.getNewDate(), 
                request.getNewTime(), 
                request.getReason()
            );
            return ResponseEntity.ok(appointment);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), "NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Response classes
    public static class AvailabilityResponse {
        private String date;
        private int availableSlots;
        private long totalBookings;
        private boolean isAvailable;

        public AvailabilityResponse(String date, int availableSlots, long totalBookings, boolean isAvailable) {
            this.date = date;
            this.availableSlots = availableSlots;
            this.totalBookings = totalBookings;
            this.isAvailable = isAvailable;
        }

        // Getters
        public String getDate() { return date; }
        public int getAvailableSlots() { return availableSlots; }
        public long getTotalBookings() { return totalBookings; }
        public boolean getIsAvailable() { return isAvailable; }
    }

    public static class TimeSlotAvailabilityResponse {
        private String date;
        private String time;
        private boolean isAvailable;

        public TimeSlotAvailabilityResponse(String date, String time, boolean isAvailable) {
            this.date = date;
            this.time = time;
            this.isAvailable = isAvailable;
        }

        // Getters
        public String getDate() { return date; }
        public String getTime() { return time; }
        public boolean getIsAvailable() { return isAvailable; }
    }

    public static class ErrorResponse {
        private String message;
        private String errorType;

        public ErrorResponse(String message, String errorType) {
            this.message = message;
            this.errorType = errorType;
        }

        // Getters
        public String getMessage() { return message; }
        public String getErrorType() { return errorType; }
    }
}
