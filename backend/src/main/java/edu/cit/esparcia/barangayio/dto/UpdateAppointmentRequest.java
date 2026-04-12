package edu.cit.esparcia.barangayio.dto;

import edu.cit.esparcia.barangayio.model.CertificateTypeEnum;
import edu.cit.esparcia.barangayio.model.PurposeEnum;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateAppointmentRequest {

    private CertificateTypeEnum certificateType;
    
    @Future(message = "Preferred date must be in the future")
    private LocalDate preferredDate;
    
    private LocalTime preferredTime;
    
    private PurposeEnum purpose;
    
    private String specifyPurpose;

    // Constructors
    public UpdateAppointmentRequest() {}

    public UpdateAppointmentRequest(CertificateTypeEnum certificateType, LocalDate preferredDate, 
                               LocalTime preferredTime, PurposeEnum purpose, 
                               String specifyPurpose) {
        this.certificateType = certificateType;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.purpose = purpose;
        this.specifyPurpose = specifyPurpose;
    }

    // Getters and Setters
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
