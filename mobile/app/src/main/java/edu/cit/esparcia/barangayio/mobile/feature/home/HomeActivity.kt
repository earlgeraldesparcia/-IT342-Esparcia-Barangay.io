package edu.cit.esparcia.barangayio.mobile.feature.home


import edu.cit.esparcia.barangayio.mobile.R
import edu.cit.esparcia.barangayio.mobile.core.api.RetrofitClient
import edu.cit.esparcia.barangayio.mobile.feature.appointment.AppointmentsActivity
import edu.cit.esparcia.barangayio.mobile.feature.appointment.BookingActivity
import edu.cit.esparcia.barangayio.mobile.feature.auth.LoginActivity
import edu.cit.esparcia.barangayio.mobile.feature.appointment.model.Appointment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set Welcome Name
        val sharedPref = getSharedPreferences("BarangayIO_Prefs", Context.MODE_PRIVATE)
        val firstName = sharedPref.getString("FIRST_NAME", "Resident")
        val tvWelcomeName = findViewById<TextView>(R.id.tvWelcomeName)
        tvWelcomeName.text = "Good day, $firstName!"

        // Logout
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Clear preferences
            sharedPref.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Open Booking
        findViewById<Button>(R.id.btnBookAppointment).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }
        findViewById<TextView>(R.id.tvBookNow).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }

        // Open Appointments List
        findViewById<View>(R.id.cardNextAppointment).setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }
        findViewById<View>(R.id.cardCompletedRequests).setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }

        fetchDashboardData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data every time we return to dashboard
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        val sharedPref = getSharedPreferences("BarangayIO_Prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", "")
        if (userId.isNullOrEmpty()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getUserAppointments(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val allAppointments = response.body()!!
                        
                        // Current Date string for comparison (format: yyyy-MM-dd)
                        val currentDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        
                        // Next Upcoming Appointment (Status pending/approved, Date >= today)
                        val upcoming = allAppointments
                            .filter { (it.status.equals("pending", true) || it.status.equals("approved", true)) && it.appointmentDate >= currentDateStr }
                            .minByOrNull { it.appointmentDate + it.appointmentTime }
                            
                        // Latest Completed Request
                        val completed = allAppointments
                            .filter { it.status.equals("completed", true) }
                            .maxByOrNull { it.appointmentDate }
                            
                        updateUpcomingUI(upcoming)
                        updateCompletedUI(completed)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Could not load dashboard data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun formatType(rawType: String): String {
        return rawType.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun updateUpcomingUI(upcoming: Appointment?) {
        val layoutNoUpcoming = findViewById<LinearLayout>(R.id.layoutNoUpcoming)
        val layoutUpcomingAppt = findViewById<LinearLayout>(R.id.layoutUpcomingAppt)
        val tvUpcomingType = findViewById<TextView>(R.id.tvUpcomingType)
        val tvUpcomingDate = findViewById<TextView>(R.id.tvUpcomingDate)

        if (upcoming == null) {
            layoutNoUpcoming.visibility = View.VISIBLE
            layoutUpcomingAppt.visibility = View.GONE
        } else {
            layoutNoUpcoming.visibility = View.GONE
            layoutUpcomingAppt.visibility = View.VISIBLE
            tvUpcomingType.text = formatType(upcoming.certificateType)
            tvUpcomingDate.text = "${upcoming.appointmentDate} • ${upcoming.appointmentTime}"
        }
    }

    private fun updateCompletedUI(completed: Appointment?) {
        val tvNoCompleted = findViewById<TextView>(R.id.tvNoCompleted)
        val layoutCompletedRequest = findViewById<LinearLayout>(R.id.layoutCompletedRequest)
        val tvCompletedType = findViewById<TextView>(R.id.tvCompletedType)
        val tvCompletedDate = findViewById<TextView>(R.id.tvCompletedDate)

        if (completed == null) {
            tvNoCompleted.visibility = View.VISIBLE
            layoutCompletedRequest.visibility = View.GONE
        } else {
            tvNoCompleted.visibility = View.GONE
            layoutCompletedRequest.visibility = View.VISIBLE
            tvCompletedType.text = formatType(completed.certificateType)
            tvCompletedDate.text = completed.appointmentDate
        }
    }
}
