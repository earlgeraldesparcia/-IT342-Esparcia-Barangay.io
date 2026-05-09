package edu.cit.esparcia.barangayio.mobile.feature.appointment
import edu.cit.esparcia.barangayio.mobile.core.api.RetrofitClient
import edu.cit.esparcia.barangayio.mobile.feature.appointment.adapter.AppointmentsAdapter
import edu.cit.esparcia.barangayio.mobile.R
import edu.cit.esparcia.barangayio.mobile.feature.appointment.AppointmentsActivity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var rvAppointments: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoAppointments: TextView
    private lateinit var adapter: AppointmentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        rvAppointments = findViewById(R.id.rvAppointments)
        progressBar = findViewById(R.id.progressBar)
        tvNoAppointments = findViewById(R.id.tvNoAppointments)
        
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvAppointments.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentsAdapter(emptyList())
        rvAppointments.adapter = adapter

        fetchAppointments()
    }

    private fun fetchAppointments() {
        progressBar.visibility = View.VISIBLE
        
        val sharedPref = getSharedPreferences("BarangayIO_Prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", "")

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getUserAppointments(userId)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        val appointmentsList = response.body()!!
                        if (appointmentsList.isEmpty()) {
                            tvNoAppointments.visibility = View.VISIBLE
                            rvAppointments.visibility = View.GONE
                        } else {
                            tvNoAppointments.visibility = View.GONE
                            rvAppointments.visibility = View.VISIBLE
                            // Sort by date descending
                            val sortedList = appointmentsList.sortedByDescending { it.appointmentDate }
                            adapter.updateData(sortedList)
                        }
                    } else {
                        Toast.makeText(this@AppointmentsActivity, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@AppointmentsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
