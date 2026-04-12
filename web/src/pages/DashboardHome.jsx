import { Link } from 'react-router-dom';
import { useMemo } from 'react';
import { getDisplayFirstName } from '../utils/authDisplay';

function SparkleIcon() {
  return (
    <svg className="sparkle-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
    </svg>
  );
}

function ClockIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <circle cx="12" cy="12" r="10" />
      <path d="M12 6v6l4 2" />
    </svg>
  );
}

function CalendarIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <rect x="3" y="4" width="18" height="18" rx="2" />
      <path d="M16 2v4M8 2v4M3 10h18" />
    </svg>
  );
}

function FileIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <path d="M14 2v6h6M16 13H8M16 17H8M10 9H8" />
    </svg>
  );
}

function MapPinIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
      <circle cx="12" cy="10" r="3" />
    </svg>
  );
}

function ChevronRightIcon() {
  return (
    <svg className="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M9 18l6-6-6-6" />
    </svg>
  );
}

const MOCK_COMPLETED = [
  { id: 1, name: 'Barangay Clearance', date: '2025-03-12', status: 'Completed' },
  { id: 2, name: 'Community Tax Certificate', date: '2025-02-28', status: 'Completed' },
];

const SERVICES = [
  { name: 'Barangay Clearance', sub: 'View requirements' },
  { name: 'Certificate of Indigency', sub: 'View requirements' },
  { name: 'Community Tax Certificate', sub: 'View requirements' },
  { name: 'Solo Parent Certificate', sub: 'View requirements' },
];

function DashboardHome() {
  const fullName = useMemo(() => getDisplayFirstName(), []);
  const showNextAppointment = false;

  return (
    <div className="dashboard-container">
      <section className="hero-banner">
        <div className="hero-content">
          <div className="hero-greeting">
            <SparkleIcon />
            <h1>Good day, {fullName}!</h1>
          </div>
          <div className="office-hours-badge">
            <ClockIcon />
            Office Hours: Monday–Friday, 8:00 AM – 5:00 PM (except holidays)
          </div>
        </div>
      </section>

      <div className="main-grid">
        <div className="left-column">
          <section className="section-card">
            <div className="section-header">
              <CalendarIcon />
              <h2>Next appointment</h2>
            </div>
            {showNextAppointment ? (
              <div className="appointment-card-inner">
                <div className="appointment-date-row">
                  <span className="appointment-date">Sample date</span>
                  <span className="status-badge confirmed">Confirmed</span>
                </div>
                <div className="appointment-session">AM Session (8:00 AM – 12:00 PM)</div>
                <div className="appointment-details">
                  <div className="detail-item">
                    <span className="detail-label">Certificate type</span>
                    <span className="detail-value">Barangay Clearance</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Location</span>
                    <span className="detail-value location-value">
                      <MapPinIcon />
                      Barangay hall (connect your data later)
                    </span>
                  </div>
                </div>
                <Link to="/resident/appointments" className="view-all-link">
                  View all appointments →
                </Link>
              </div>
            ) : (
              <div className="no-appointment">
                <p>No upcoming appointments.</p>
                <Link to="/resident/book" className="book-now-link">
                  Book an appointment now
                </Link>
              </div>
            )}
          </section>

          <section className="section-card">
            <div className="section-header">
              <FileIcon />
              <h2>Completed requests</h2>
            </div>
            {MOCK_COMPLETED.length ? (
              MOCK_COMPLETED.map((row) => (
                <div key={row.id} className="request-item">
                  <div className="request-icon">
                    <FileIcon />
                  </div>
                  <div className="request-info">
                    <span className="request-name">{row.name}</span>
                    <div className="request-meta">
                      <span className="request-date">{row.date}</span>
                      <span className="status-badge completed">{row.status}</span>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="no-requests">No completed requests yet.</div>
            )}
          </section>
        </div>

        <aside className="right-column">
          <div className="services-card">
            <h2>Barangay services</h2>
            <div className="services-list">
              {SERVICES.map((s) => (
                <Link key={s.name} to="/resident/book" className="service-item">
                  <div className="service-icon">
                    <FileIcon />
                  </div>
                  <div className="service-info">
                    <span className="service-name">{s.name}</span>
                    <span className="service-link">{s.sub}</span>
                  </div>
                  <ChevronRightIcon />
                </Link>
              ))}
            </div>
            <Link to="/resident/book" className="book-appointment-btn">
              Book appointment
            </Link>
          </div>
        </aside>
      </div>

      <section className="resources-section">
        <h2>Contact information</h2>
        <div className="resources-grid">
          <div className="resource-card no-highlight">
            <h3>Barangay office</h3>
            <p>Email: barangay.example@mail.gov</p>
            <p>Phone: (032) 000-0000</p>
            <p>Address: Your barangay address line</p>
          </div>
        </div>
      </section>
    </div>
  );
}

export default DashboardHome;
