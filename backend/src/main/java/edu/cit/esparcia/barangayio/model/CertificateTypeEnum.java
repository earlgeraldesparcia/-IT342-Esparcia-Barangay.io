package edu.cit.esparcia.barangayio.model;

public enum CertificateTypeEnum {
    BARANGAY_CLEARANCE("barangay_clearance", "Barangay Clearance"),
    CERTIFICATE_OF_INDIGENCY("certificate_of_indigency", "Certificate of Indigency"),
    COMMUNITY_TAX_CERTIFICATE("community_tax_certificate", "Community Tax Certificate"),
    SOLO_PARENT_CERTIFICATE("solo_parent_certificate", "Solo Parent Certificate");

    private final String value;
    private final String displayName;

    CertificateTypeEnum(String value, String displayName) {
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
