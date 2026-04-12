import { Link } from 'react-router-dom';

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
  return (
    <div className="dashboard-container resident-appt-page">
      <section className="section-card resident-appt-hero">
        <div className="resident-appt-hero-inner">
          <CalendarIllustration />
          <div>
            <h1 className="resident-appt-title">Your appointments</h1>
            <p className="resident-appt-desc">
              Book a certificate or clearance visit from your home dashboard. When your barangay confirms a slot, it will
              appear here (connect your backend to replace this placeholder).
            </p>
            <Link to="/resident/dashboard" className="view-all-link">
              ← Back to home
            </Link>
          </div>
        </div>
      </section>

      <section className="section-card">
        <h2 className="resident-appt-sub">Upcoming</h2>
        <div className="no-appointment">
          <p>You have no upcoming appointments.</p>
          <Link to="/resident/book" className="book-now-link">
            Browse services and book
          </Link>
        </div>
      </section>
    </div>
  );
}
