package edu.cit.esparcia.barangayio.feature.appointment.dto;

import edu.cit.esparcia.barangayio.feature.auth.User;

import edu.cit.esparcia.barangayio.feature.certificate.CertificateTypeEnum;
import edu.cit.esparcia.barangayio.feature.certificate.PurposeEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class CreateAppointmentRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Certificate type is required")
    private CertificateTypeEnum certificateType;

    @NotNull(message = "Preferred date is required")
    @FutureOrPresent(message = "Preferred date must be in the future or present")
    private LocalDate preferredDate;

    @NotNull(message = "Preferred time is required")
    private LocalTime preferredTime;

    @NotNull(message = "Purpose is required")
    private PurposeEnum purpose;

    private String specifyPurpose;

    // Constructors
    public CreateAppointmentRequest() {}

    public CreateAppointmentRequest(UUID userId, CertificateTypeEnum certificateType, 
                               LocalDate preferredDate, LocalTime preferredTime, 
                               PurposeEnum purpose, String specifyPurpose) {
        this.userId = userId;
        this.certificateType = certificateType;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.purpose = purpose;
        this.specifyPurpose = specifyPurpose;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public CertificateTypeEnum getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateTypeEnum certificateType) {
        this.certificateType = certificateType;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
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
}
