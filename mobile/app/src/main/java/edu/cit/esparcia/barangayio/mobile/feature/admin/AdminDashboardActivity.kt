package edu.cit.esparcia.barangayio.mobile.feature.admin
import edu.cit.esparcia.barangayio.mobile.core.api.RetrofitClient
import edu.cit.esparcia.barangayio.mobile.feature.auth.LoginActivity
import edu.cit.esparcia.barangayio.mobile.R
import edu.cit.esparcia.barangayio.mobile.feature.admin.AdminAppointmentDeskActivity
import edu.cit.esparcia.barangayio.mobile.feature.admin.AdminDashboardActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var tvScheduledToday: TextView
    private lateinit var tvCompletedToday: TextView
    private lateinit var tvRegisteredResidents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        tvScheduledToday = findViewById(R.id.tvScheduledToday)
        tvCompletedToday = findViewById(R.id.tvCompletedToday)
        tvRegisteredResidents = findViewById(R.id.tvRegisteredResidents)

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val sharedPref = getSharedPreferences("BarangayIO_Prefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnAppointmentDesk).setOnClickListener {
            startActivity(Intent(this, AdminAppointmentDeskActivity::class.java))
        }

        fetchAdminStats()
    }

    override fun onResume() {
        super.onResume()
        fetchAdminStats()
    }

    private fun fetchAdminStats() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getDashboardStats()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val stats = response.body()!!
                        tvScheduledToday.text = stats.scheduledToday.toString()
                        tvCompletedToday.text = stats.completedToday.toString()
                        tvRegisteredResidents.text = stats.registeredResidents.toString()
                    } else {
                        Toast.makeText(this@AdminDashboardActivity, "Failed to load stats", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminDashboardActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
