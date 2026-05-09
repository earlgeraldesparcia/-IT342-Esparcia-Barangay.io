import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { getUserId } from '../utils/authDisplay';

function CalendarIllustration() {
  return (
    <svg className="resident-appt-illus" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" aria-hidden="true">
      <rect x="3" y="4" width="18" height="18" rx="2" />
      <path d="M16 2v4M8 2v4M3 10h18" />
      <path d="M8 14h.01M12 14h.01M16 14h.01M8 18h.01M12 18h.01" />
    </svg>
  );
}

export default function ResidentAppointments() {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchAppointments() {
      const userId = getUserId();
      if (!userId) {
        setLoading(false);
        return;
      }
      try {
        const response = await fetch(`http://localhost:8080/api/appointments/user/${userId}`, {
          headers: {
            'Cache-Control': 'no-cache, no-store, must-revalidate',
            'Pragma': 'no-cache',
            'Expires': '0'
          }
        });
        if (response.ok) {
          const data = await response.json();
          data.sort((a, b) => {
            const dateA = new Date(a.appointmentDate + 'T' + a.appointmentTime);
            const dateB = new Date(b.appointmentDate + 'T' + b.appointmentTime);
            return dateB - dateA;
          });
          setAppointments(data);
        }
      } catch (error) {
        console.error("Error fetching appointments:", error);
      } finally {
        setLoading(false);
      }
    }
    fetchAppointments();
  }, []);

  return (
    <div className="dashboard-container resident-appt-page">
      <section className="section-card resident-appt-hero">
        <div className="resident-appt-hero-inner">
          <CalendarIllustration />
          <div>
            <h1 className="resident-appt-title">Your appointments</h1>
            <p className="resident-appt-desc">
              Book a certificate or clearance visit from your home dashboard. When your barangay confirms a slot, it will
              appear here.
            </p>
            <Link to="/resident/dashboard" className="view-all-link">
              ← Back to home
            </Link>
          </div>
        </div>
      </section>

      <section className="section-card">
        <h2 className="resident-appt-sub">All Appointments</h2>
        {loading ? (
          <div className="no-appointment"><p>Loading...</p></div>
        ) : appointments.length > 0 ? (
          <div className="appointments-list" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {appointments.map(apt => (
              <div key={apt.id} className="appointment-card-inner" style={{ border: '1px solid #eaeaea', borderRadius: '8px', padding: '1.5rem' }}>
                <div className="appointment-date-row" style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                  <span className="appointment-date" style={{ fontWeight: 'bold' }}>{apt.appointmentDate}</span>
                  <span className={`status-badge ${apt.status?.toLowerCase()}`}>{apt.status}</span>
                </div>
                <div className="appointment-session" style={{ marginBottom: '1rem', color: '#666' }}>Time: {apt.appointmentTime}</div>
                <div className="appointment-details">
                  <div className="detail-item" style={{ marginBottom: '0.5rem' }}>
                    <span className="detail-label" style={{ fontWeight: '500', marginRight: '0.5rem' }}>Certificate type:</span>
                    <span className="detail-value">{apt.certificateType.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase())}</span>
                  </div>
                  {apt.purpose && (
                    <div className="detail-item">
                      <span className="detail-label" style={{ fontWeight: '500', marginRight: '0.5rem' }}>Purpose:</span>
                      <span className="detail-value">{apt.purpose.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase())}</span>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="no-appointment">
            <p>You have no appointments.</p>
            <Link to="/resident/book" className="book-now-link">
              Browse services and book
            </Link>
          </div>
        )}
      </section>
    </div>
  );
}
