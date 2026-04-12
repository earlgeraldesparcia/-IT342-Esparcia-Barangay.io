package edu.cit.esparcia.barangayio.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "resident_id", nullable = false)
    private UUID residentId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private CertificateTypeEnum certificateType;
    
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;
    
    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurposeEnum purpose;
    
    @Column(name = "specify_purpose")
    private String specifyPurpose;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "reschedule_reason")
    private String rescheduleReason;
    
    @Column(name = "rescheduled_at")
    private LocalDateTime rescheduledAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Appointment() {}
    
    public Appointment(UUID residentId, CertificateTypeEnum certificateType, LocalDate appointmentDate, 
                   LocalTime appointmentTime, PurposeEnum purpose, AppointmentStatus status, String specifyPurpose) {
        this.residentId = residentId;
        this.certificateType = certificateType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.purpose = purpose;
        this.status = status != null ? status : AppointmentStatus.PENDING;
        this.specifyPurpose = specifyPurpose;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getResidentId() {
        return residentId;
    }
    
    public void setResidentId(UUID residentId) {
        this.residentId = residentId;
    }
    
    public CertificateTypeEnum getCertificateType() {
        return certificateType;
    }
    
    public void setCertificateType(CertificateTypeEnum certificateType) {
        this.certificateType = certificateType;
    }
    
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public PurposeEnum getPurpose() {
        return purpose;
    }
    
    public void setPurpose(PurposeEnum purpose) {
        this.purpose = purpose;
    }
    
    public String getSpecifyPurpose() {
        return specifyPurpose;
    }
    
    public void setSpecifyPurpose(String specifyPurpose) {
        this.specifyPurpose = specifyPurpose;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public String getRescheduleReason() {
        return rescheduleReason;
    }
    
    public void setRescheduleReason(String rescheduleReason) {
        this.rescheduleReason = rescheduleReason;
    }
    
    public LocalDateTime getRescheduledAt() {
        return rescheduledAt;
    }
    
    public void setRescheduledAt(LocalDateTime rescheduledAt) {
        this.rescheduledAt = rescheduledAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
