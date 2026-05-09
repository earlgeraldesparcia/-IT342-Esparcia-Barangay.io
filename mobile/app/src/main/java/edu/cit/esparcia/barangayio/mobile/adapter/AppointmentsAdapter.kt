package edu.cit.esparcia.barangayio.mobile.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.esparcia.barangayio.mobile.R
import edu.cit.esparcia.barangayio.mobile.model.Appointment

class AppointmentsAdapter(private var appointments: List<Appointment>) : 
    RecyclerView.Adapter<AppointmentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvCertificateType: TextView = view.findViewById(R.id.tvCertificateType)
        val tvPurpose: TextView = view.findViewById(R.id.tvPurpose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appointments[position]
        
        holder.tvDate.text = item.appointmentDate
        
        // Format time (e.g. 14:30:00 to 2:30 PM) simple logic or just raw string
        holder.tvTime.text = "Time: ${item.appointmentTime}"
        
        // Format Enum String like CERTIFICATE_OF_INDIGENCY -> Certificate Of Indigency
        holder.tvCertificateType.text = item.certificateType.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        holder.tvPurpose.text = item.purpose?.replace("_", " ")?.lowercase()?.split(" ")?.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } } ?: "N/A"
        
        // Set Status logic
        val statusStr = item.status.lowercase().replaceFirstChar { it.uppercase() }
        holder.tvStatus.text = statusStr
        
        when (item.status.lowercase()) {
            "approved" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#1D4ED8"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#DBEAFE"))
            }
            "completed" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#065F46"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#D1FAE5"))
            }
            "cancelled", "no_show" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#991B1B"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FEE2E2"))
            }
            else -> {
                holder.tvStatus.setTextColor(Color.parseColor("#374151"))
                holder.tvStatus.setBackgroundColor(Color.parseColor("#F3F4F6"))
            }
        }
    }

    override fun getItemCount() = appointments.size
    
    fun updateData(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
}
