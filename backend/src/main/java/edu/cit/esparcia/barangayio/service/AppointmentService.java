package edu.cit.esparcia.barangayio.service;

import edu.cit.esparcia.barangayio.model.Appointment;
import edu.cit.esparcia.barangayio.model.AppointmentStatus;
import edu.cit.esparcia.barangayio.model.CertificateTypeEnum;
import edu.cit.esparcia.barangayio.model.PurposeEnum;
import edu.cit.esparcia.barangayio.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getResidentAppointments(UUID residentId) {
        return appointmentRepository.findByResidentIdOrderByAppointmentDateAsc(residentId);
    }

    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.PENDING);
    }

    public List<Appointment> getApprovedAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.APPROVED);
    }

    public List<Appointment> getCompletedAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.COMPLETED);
    }

    public List<Appointment> getCancelledAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.CANCELLED);
    }

    public Appointment getAppointmentById(UUID id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment createAppointment(Appointment appointment) {
        // Validate appointment date is not in the past
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book appointments in the past");
        }

        // Validate appointment time is within business hours (8:00 AM - 5:00 PM)
        if (appointment.getAppointmentTime().isBefore(LocalTime.of(8, 0)) || 
            appointment.getAppointmentTime().isAfter(LocalTime.of(17, 0))) {
            throw new IllegalArgumentException("Appointments are only available between 8:00 AM and 5:00 PM");
        }

        // Check if time slot is available (max 30 appointments per time slot)
        List<AppointmentStatus> excludedStatuses = Arrays.asList(
            AppointmentStatus.CANCELLED, 
            AppointmentStatus.NO_SHOW
        );
        
        long existingAppointments = appointmentRepository.countByDateAndTimeAndStatusNotIn(
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime(),
            excludedStatuses
        );

        if (existingAppointments >= 30) {
            throw new IllegalArgumentException("This time slot is fully booked. Please select another time.");
        }

        // Validate purpose if "Others" is selected
        if (appointment.getPurpose() == PurposeEnum.OTHERS && 
            (appointment.getSpecifyPurpose() == null || appointment.getSpecifyPurpose().trim().isEmpty())) {
            throw new IllegalArgumentException("Please specify your purpose when selecting 'Others'");
        }

        appointment.setStatus(AppointmentStatus.PENDING);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(UUID id, Appointment appointmentDetails) {
        Appointment existingAppointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Update allowed fields
        if (appointmentDetails.getCertificateType() != null) {
            existingAppointment.setCertificateType(appointmentDetails.getCertificateType());
        }
        if (appointmentDetails.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
        }
        if (appointmentDetails.getAppointmentTime() != null) {
            existingAppointment.setAppointmentTime(appointmentDetails.getAppointmentTime());
        }
        if (appointmentDetails.getPurpose() != null) {
            existingAppointment.setPurpose(appointmentDetails.getPurpose());
        }
        if (appointmentDetails.getSpecifyPurpose() != null) {
            existingAppointment.setSpecifyPurpose(appointmentDetails.getSpecifyPurpose());
        }

        return appointmentRepository.save(existingAppointment);
    }

    public Appointment approveAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        appointment.setStatus(AppointmentStatus.APPROVED);
        return appointmentRepository.save(appointment);
    }

    public Appointment completeAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    public Appointment claimAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Only completed appointments can be claimed");
        }
        
        appointment.setStatus(AppointmentStatus.CLAIMED);
        return appointmentRepository.save(appointment);
    }

    public Appointment cancelAppointment(UUID id, String cancellationReason) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || 
            appointment.getStatus() == AppointmentStatus.CLAIMED) {
            throw new IllegalArgumentException("Cannot cancel completed or claimed appointments");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancellationReason);
        return appointmentRepository.save(appointment);
    }

    public Appointment rescheduleAppointment(UUID id, LocalDate newDate, LocalTime newTime, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || 
            appointment.getStatus() == AppointmentStatus.CLAIMED) {
            throw new IllegalArgumentException("Cannot reschedule completed or claimed appointments");
        }

        // Validate new date and time
        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot reschedule to a past date");
        }

        if (newTime.isBefore(LocalTime.of(8, 0)) || newTime.isAfter(LocalTime.of(17, 0))) {
            throw new IllegalArgumentException("Appointments are only available between 8:00 AM and 5:00 PM");
        }

        // Check if new time slot is available
        List<AppointmentStatus> excludedStatuses = Arrays.asList(
            AppointmentStatus.CANCELLED, 
            AppointmentStatus.NO_SHOW
        );
        
        long existingAppointments = appointmentRepository.countByDateAndTimeAndStatusNotIn(
            newDate, newTime, excludedStatuses
        );

        if (existingAppointments >= 30) {
            throw new IllegalArgumentException("This time slot is fully booked. Please select another time.");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);
        appointment.setRescheduleReason(reason);
        appointment.setRescheduledAt(LocalDateTime.now());
        
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        appointmentRepository.delete(appointment);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<AppointmentStatus> excludedStatuses = Arrays.asList(
            AppointmentStatus.CANCELLED, 
            AppointmentStatus.NO_SHOW
        );
        return appointmentRepository.findByDateAndStatusNotInOrderByTime(date, excludedStatuses);
    }

    public long getAppointmentCountByDate(LocalDate date) {
        List<AppointmentStatus> excludedStatuses = Arrays.asList(
            AppointmentStatus.CANCELLED, 
            AppointmentStatus.NO_SHOW
        );
        return appointmentRepository.countByDateAndStatusNotIn(date, excludedStatuses);
    }

    public boolean isTimeSlotAvailable(LocalDate date, LocalTime time) {
        List<AppointmentStatus> excludedStatuses = Arrays.asList(
            AppointmentStatus.CANCELLED, 
            AppointmentStatus.NO_SHOW
        );
        
        long existingAppointments = appointmentRepository.countByDateAndTimeAndStatusNotIn(
            date, time, excludedStatuses
        );
        
        return existingAppointments < 30;
    }
}
