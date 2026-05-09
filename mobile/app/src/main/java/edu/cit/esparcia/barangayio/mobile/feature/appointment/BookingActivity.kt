package edu.cit.esparcia.barangayio.mobile.feature.appointment


import edu.cit.esparcia.barangayio.mobile.R
import edu.cit.esparcia.barangayio.mobile.core.api.RetrofitClient
import edu.cit.esparcia.barangayio.mobile.core.api.CreateAppointmentRequest

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var spinnerCertificate: Spinner
    private lateinit var spinnerPurpose: Spinner
    private lateinit var etSpecifyPurpose: EditText
    private lateinit var btnPickDate: Button
    private lateinit var spinnerTimeSlot: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button

    private var selectedDate: String = ""

    private val certificateTypes = arrayOf(
        "Barangay Clearance",
        "Certificate of Indigency",
        "Community Tax Certificate",
        "Solo Parent Certificate"
    )

    private val dbCertificateTypes = arrayOf(
        "BARANGAY_CLEARANCE",
        "CERTIFICATE_OF_INDIGENCY",
        "COMMUNITY_TAX_CERTIFICATE",
        "SOLO_PARENT_CERTIFICATE"
    )

    private val purposes = arrayOf(
        "Employment",
        "Business Permit",
        "Government Benefits",
        "Loan Application",
        "Travel",
        "Education",
        "Others"
    )

    private val dbPurposes = arrayOf(
        "EMPLOYMENT",
        "BUSINESS_PERMIT",
        "GOVERNMENT_BENEFITS",
        "LOAN_APPLICATION",
        "TRAVEL",
        "EDUCATION",
        "OTHERS"
    )

    private val timeSlots = arrayOf(
        "08:00:00", "08:30:00", "09:00:00", "09:30:00",
        "10:00:00", "10:30:00", "11:00:00", "11:30:00",
        "13:00:00", "13:30:00", "14:00:00", "14:30:00",
        "15:00:00", "15:30:00", "16:00:00", "16:30:00"
    )

    private val displayTimeSlots = arrayOf(
        "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM",
        "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
        "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
        "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        spinnerCertificate = findViewById(R.id.spinnerCertificate)
        spinnerPurpose = findViewById(R.id.spinnerPurpose)
        etSpecifyPurpose = findViewById(R.id.etSpecifyPurpose)
        btnPickDate = findViewById(R.id.btnPickDate)
        spinnerTimeSlot = findViewById(R.id.spinnerTimeSlot)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnCancel = findViewById(R.id.btnCancel)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }

        setupSpinners()

        btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, y, m, d ->
                val formattedMonth = String.format("%02d", m + 1)
                val formattedDay = String.format("%02d", d)
                selectedDate = "$y-$formattedMonth-$formattedDay"
                btnPickDate.text = selectedDate
                refreshTimeSlots()
            }, year, month, day)

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        btnSubmit.setOnClickListener {
            submitBooking()
        }
    }

    private fun isTimePast(timeString: String): Boolean {
        if (selectedDate.isEmpty()) return false
        
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdfDate.format(Date())
        
        if (selectedDate != todayStr) {
            return false // Future date
        }
        
        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = sdfTime.format(Date())
        
        return timeString < currentTime
    }

    private fun refreshTimeSlots() {
        val timeAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, displayTimeSlots) {
            override fun isEnabled(position: Int): Boolean {
                return !isTimePast(timeSlots[position])
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (isTimePast(timeSlots[position])) {
                    view.setTextColor(android.graphics.Color.LTGRAY)
                } else {
                    view.setTextColor(android.graphics.Color.BLACK)
                }
                return view
            }
        }
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeSlot.adapter = timeAdapter

        // Select first available time
        for (i in timeSlots.indices) {
            if (!isTimePast(timeSlots[i])) {
                spinnerTimeSlot.setSelection(i)
                break
            }
        }
    }

    private fun setupSpinners() {
        val certAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, certificateTypes)
        certAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCertificate.adapter = certAdapter

        val purposeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, purposes)
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPurpose.adapter = purposeAdapter

        spinnerPurpose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (purposes[position] == "Others") {
                    etSpecifyPurpose.visibility = View.VISIBLE
                } else {
                    etSpecifyPurpose.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        refreshTimeSlots()
    }

    private fun submitBooking() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        val certType = dbCertificateTypes[spinnerCertificate.selectedItemPosition]
        val purposeType = dbPurposes[spinnerPurpose.selectedItemPosition]
        val specifyPurposeStr = if (purposes[spinnerPurpose.selectedItemPosition] == "Others") {
            val text = etSpecifyPurpose.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Please specify your purpose", Toast.LENGTH_SHORT).show()
                return
            }
            text
        } else {
            null
        }
        val timeSlot = timeSlots[spinnerTimeSlot.selectedItemPosition]

        val sharedPref = getSharedPreferences("BarangayIO_Prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", "")

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User ID missing. Please log out and log in again.", Toast.LENGTH_LONG).show()
            return
        }

        val request = CreateAppointmentRequest(
            userId = userId,
            certificateType = certType,
            purpose = purposeType,
            specifyPurpose = specifyPurposeStr,
            preferredDate = selectedDate,
            preferredTime = timeSlot
        )

        btnSubmit.isEnabled = false
        btnSubmit.text = "Booking..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.createAppointment(request)
                withContext(Dispatchers.Main) {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Book Appointment"
                    if (response.isSuccessful) {
                        Toast.makeText(this@BookingActivity, "Appointment booked successfully!", Toast.LENGTH_LONG).show()
                        finish() // Go back to dashboard
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody ?: "").getString("message")
                        } catch (e: Exception) {
                            "Failed to book appointment"
                        }
                        Toast.makeText(this@BookingActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Book Appointment"
                    Toast.makeText(this@BookingActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
