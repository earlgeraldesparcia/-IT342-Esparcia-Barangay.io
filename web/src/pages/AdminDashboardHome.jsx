import { Link } from 'react-router-dom';

function IconCalendar() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="22" height="22" aria-hidden="true">
      <rect x="3" y="4" width="18" height="18" rx="2" />
      <path d="M16 2v4M8 2v4M3 10h18" />
    </svg>
  );
}

function IconInbox() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="22" height="22" aria-hidden="true">
      <path d="M22 12h-6l-2 3H10L8 12H2" />
      <path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z" />
    </svg>
  );
}

function IconUsers() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="22" height="22" aria-hidden="true">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" />
    </svg>
  );
}

export default function AdminDashboardHome() {
  return (
    <div className="admin-home">
      <header className="appt-mgmt-header admin-home-hero">
        <div className="appt-mgmt-header-left">
          <h1>
            <IconCalendar />
            Barangay operations
          </h1>
          <p className="admin-home-lead">
            Use the appointment desk to review schedules and resident visits—similar to the staff dashboard in{' '}
            <a
              className="admin-home-link"
              href="https://github.com/adamxparx/CSIT327-G2-BOACMS"
              target="_blank"
              rel="noreferrer"
            >
              BOACMS
            </a>
            .
          </p>
        </div>
      </header>

      <div className="appt-stats-grid">
        <div className="appt-stat-card appt-stat-blue">
          <div className="appt-stat-icon">
            <IconInbox />
          </div>
          <div>
            <p className="appt-stat-label">Pending approvals</p>
            <h3 className="appt-stat-value">0</h3>
          </div>
        </div>
        <div className="appt-stat-card appt-stat-green">
          <div className="appt-stat-icon">
            <IconCalendar />
          </div>
          <div>
            <p className="appt-stat-label">Scheduled today</p>
            <h3 className="appt-stat-value">—</h3>
          </div>
        </div>
        <div className="appt-stat-card appt-stat-orange">
          <div className="appt-stat-icon">
            <IconUsers />
          </div>
          <div>
            <p className="appt-stat-label">Registered residents</p>
            <h3 className="appt-stat-value">—</h3>
          </div>
        </div>
      </div>

      <section className="section-card admin-home-panel">
        <h2 className="admin-home-panel-title">Quick actions</h2>
        <p className="admin-home-panel-text">
          Open the appointment desk for day-view sessions, resident cards, and action buttons (preview data until APIs are
          connected).
        </p>
        <Link to="/admin/appointments" className="book-appointment-btn admin-home-cta">
          Go to appointment desk
        </Link>
      </section>
    </div>
  );
}
