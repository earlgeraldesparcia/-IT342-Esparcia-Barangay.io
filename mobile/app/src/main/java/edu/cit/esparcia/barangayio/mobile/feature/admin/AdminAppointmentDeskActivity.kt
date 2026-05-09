package edu.cit.esparcia.barangayio.mobile.feature.admin
import edu.cit.esparcia.barangayio.mobile.core.api.RetrofitClient
import edu.cit.esparcia.barangayio.mobile.feature.appointment.model.Appointment
import edu.cit.esparcia.barangayio.mobile.R
import edu.cit.esparcia.barangayio.mobile.feature.admin.AdminAppointmentDeskActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminAppointmentDeskActivity : AppCompatActivity() {

    private lateinit var tvDeskScheduledToday: TextView
    private lateinit var tvDeskCompletedToday: TextView
    private lateinit var tvDeskTotalResidents: TextView

    private lateinit var listToday: LinearLayout
    private lateinit var listUpcoming: LinearLayout
    private lateinit var listOverdue: LinearLayout
    private lateinit var listCompleted: LinearLayout

    private lateinit var emptyToday: TextView
    private lateinit var emptyUpcoming: TextView
    private lateinit var emptyOverdue: TextView
    private lateinit var emptyCompleted: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_appointment_desk)

        tvDeskScheduledToday = findViewById(R.id.tvDeskScheduledToday)
        tvDeskCompletedToday = findViewById(R.id.tvDeskCompletedToday)
        tvDeskTotalResidents = findViewById(R.id.tvDeskTotalResidents)

        listToday = findViewById(R.id.listToday)
        listUpcoming = findViewById(R.id.listUpcoming)
        listOverdue = findViewById(R.id.listOverdue)
        listCompleted = findViewById(R.id.listCompleted)

        emptyToday = findViewById(R.id.emptyToday)
        emptyUpcoming = findViewById(R.id.emptyUpcoming)
        emptyOverdue = findViewById(R.id.emptyOverdue)
        emptyCompleted = findViewById(R.id.emptyCompleted)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        fetchDashboardStats()
        fetchAllAppointments()
    }

    private fun fetchDashboardStats() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getDashboardStats()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val stats = response.body()!!
                        tvDeskScheduledToday.text = stats.scheduledToday.toString()
                        tvDeskCompletedToday.text = stats.completedToday.toString()
                        tvDeskTotalResidents.text = stats.registeredResidents.toString()
                    }
                }
            } catch (e: Exception) {
                // Ignore silent failure for stats
            }
        }
    }

    private fun fetchAllAppointments() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllAppointments()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        categorizeAppointments(response.body()!!)
                    } else {
                        Toast.makeText(this@AdminAppointmentDeskActivity, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminAppointmentDeskActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun categorizeAppointments(appointments: List<Appointment>) {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdfDate.format(Date())

        val todayList = mutableListOf<Appointment>()
        val upcomingList = mutableListOf<Appointment>()
        val overdueList = mutableListOf<Appointment>()
        val completedList = mutableListOf<Appointment>()

        for (appt in appointments) {
            val status = appt.status.uppercase()
            if (status == "COMPLETED" || status == "CANCELLED") {
                completedList.add(appt)
            } else {
                if (appt.appointmentDate == todayStr) {
                    todayList.add(appt)
                } else if (appt.appointmentDate > todayStr) {
                    upcomingList.add(appt)
                } else {
                    overdueList.add(appt)
                }
            }
        }

        renderList(listToday, emptyToday, todayList)
        renderList(listUpcoming, emptyUpcoming, upcomingList)
        renderList(listOverdue, emptyOverdue, overdueList)
        renderList(listCompleted, emptyCompleted, completedList)
    }

    private fun renderList(container: LinearLayout, emptyView: TextView, items: List<Appointment>) {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdfDate.format(Date())

        // Clear previous except emptyView
        container.removeAllViews()
        container.addView(emptyView)

        if (items.isEmpty()) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            for (appt in items) {
                val view = LayoutInflater.from(this).inflate(R.layout.item_admin_appointment, container, false)
                
                val tvResidentName = view.findViewById<TextView>(R.id.tvResidentName)
                val tvInitials = view.findViewById<TextView>(R.id.tvInitials)
                val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
                val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
                
                view.findViewById<TextView>(R.id.tvDateTime).text = "${appt.appointmentDate} at ${appt.appointmentTime}"
                view.findViewById<TextView>(R.id.tvCertificateType).text = appt.certificateType.replace("_", " ").lowercase().capitalize()
                view.findViewById<TextView>(R.id.tvPurpose).text = appt.purpose?.replace("_", " ")?.lowercase()?.capitalize() ?: "N/A"
                
                val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
                tvStatus.text = appt.status
                if (appt.status.equals("COMPLETED", true)) {
                    tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#10B981"))
                } else if (appt.status.equals("CANCELLED", true)) {
                    tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#EF4444"))
                } else {
                    tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#3B82F6"))
                }

                // Initial loading state
                tvResidentName.text = "Loading resident..."
                
                // Fetch resident details
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.instance.getUserDetails(appt.userId)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful && response.body() != null) {
                                val user = response.body()!!
                                val fullName = "${user.firstName} ${user.lastName}"
                                tvResidentName.text = fullName
                                tvInitials.text = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}".uppercase()
                                tvPhone.text = user.phoneNumber
                                tvEmail.text = user.email
                            } else {
                                tvResidentName.text = "Unknown Resident"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            tvResidentName.text = "Error loading name"
                        }
                    }
                }
                
                val btnAction = view.findViewById<Button>(R.id.btnAction)
                if (appt.status.equals("COMPLETED", true) || appt.status.equals("CANCELLED", true) || appt.status.equals("NO_SHOW", true)) {
                    btnAction.visibility = View.GONE
                } else {
                    btnAction.visibility = View.VISIBLE
                    btnAction.isEnabled = true
                    btnAction.alpha = 1.0f
                    if (appt.appointmentDate < todayStr) {
                        btnAction.text = "Mark as No show"
                        btnAction.setBackgroundColor(android.graphics.Color.parseColor("#EF4444"))
                        btnAction.setOnClickListener {
                            updateAppointmentStatus(appt.id, "no_show")
                        }
                    } else {
                        btnAction.text = "Mark as completed"
                        btnAction.setBackgroundColor(android.graphics.Color.parseColor("#155EAF"))
                        btnAction.setOnClickListener {
                            updateAppointmentStatus(appt.id, "completed")
                        }
                    }
                }

                container.addView(view)
            }
        }
    }

    private fun updateAppointmentStatus(id: String, action: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (action == "completed") {
                    RetrofitClient.instance.completeAppointment(id)
                } else {
                    RetrofitClient.instance.cancelAppointment(id, mapOf("reason" to "No show"))
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminAppointmentDeskActivity, "Status updated!", Toast.LENGTH_SHORT).show()
                        fetchDashboardStats()
                        fetchAllAppointments()
                    } else {
                        Toast.makeText(this@AdminAppointmentDeskActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminAppointmentDeskActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
