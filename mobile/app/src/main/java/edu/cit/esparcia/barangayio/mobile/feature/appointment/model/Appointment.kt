package edu.cit.esparcia.barangayio.mobile.feature.appointment.model

data class Appointment(
    val id: String,
    val userId: String,
    val certificateType: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val purpose: String?,
    val specifyPurpose: String?,
    val status: String,
    val createdAt: String
)
