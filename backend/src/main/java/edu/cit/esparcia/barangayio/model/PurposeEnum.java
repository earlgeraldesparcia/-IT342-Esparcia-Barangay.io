package edu.cit.esparcia.barangayio.model;

public enum PurposeEnum {
    EMPLOYMENT("employment", "Employment"),
    BUSINESS_PERMIT("business_permit", "Business Permit"),
    GOVERNMENT_BENEFITS("government_benefits", "Government Benefits"),
    LOAN_APPLICATION("loan_application", "Loan Application"),
    TRAVEL("travel", "Travel"),
    EDUCATION("education", "Education"),
    OTHERS("others", "Others (Please Specify)");

    private final String value;
    private final String displayName;

    PurposeEnum(String value, String displayName) {
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
