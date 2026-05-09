import { Link } from 'react-router-dom';
import { useMemo, useState, useEffect } from 'react';
import { getDisplayFirstName, getUserId } from '../../auth/utils/authDisplay';

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

const SERVICES = [
  { name: 'Barangay Clearance' },
  { name: 'Certificate of Indigency' },
  { name: 'Community Tax Certificate' },
  { name: 'Solo Parent Certificate' },
];

function DashboardHome() {
  const fullName = useMemo(() => getDisplayFirstName(), []);
  const [upcomingAppointments, setUpcomingAppointments] = useState([]);
  const [completedRequests, setCompletedRequests] = useState([]);
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
          const completed = data.filter(apt => apt.status === 'COMPLETED').slice(0, 5);
          setCompletedRequests(completed.map(apt => ({
            id: apt.id,
            name: apt.certificateType.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()),
            date: apt.appointmentDate,
            status: 'Completed'
          })));

          const upcoming = data.filter(apt => apt.status === 'PENDING' || apt.status === 'APPROVED');
          upcoming.sort((a, b) => {
            const dateA = new Date(a.appointmentDate + 'T' + a.appointmentTime);
            const dateB = new Date(b.appointmentDate + 'T' + b.appointmentTime);
            return dateA - dateB;
          });
          setUpcomingAppointments(upcoming);
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
            {loading ? (
              <div className="no-appointment"><p>Loading...</p></div>
            ) : upcomingAppointments.length > 0 ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', paddingTop: '1rem' }}>
                {upcomingAppointments.map((apt) => (
                  <div key={apt.id} className="appointment-card-inner" style={{ marginTop: 0 }}>
                    <div className="appointment-date-row">
                      <span className="appointment-date">{apt.appointmentDate}</span>
                      <span className={`status-badge ${apt.status.toLowerCase()}`}>{apt.status}</span>
                    </div>
                    <div className="appointment-session">Time: {apt.appointmentTime}</div>
                    <div className="appointment-details">
                      <div className="detail-item">
                        <span className="detail-label">Certificate type</span>
                        <span className="detail-value">{apt.certificateType.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase())}</span>
                      </div>
                      <div className="detail-item">
                        <span className="detail-label">Location</span>
                        <span className="detail-value location-value">
                          <MapPinIcon />
                          Barangay hall
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
                <div style={{ padding: '0 1.5rem 1.5rem' }}>
                  <Link to="/resident/appointments" className="view-all-link">
                    View all appointments →
                  </Link>
                </div>
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
            {loading ? (
              <div className="no-requests">Loading...</div>
            ) : completedRequests.length ? (
              completedRequests.map((row) => (
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
            <ul className="services-list" style={{ textAlign: 'left', paddingLeft: '1.5rem', marginBottom: '1.5rem', listStyleType: 'disc' }}>
              {SERVICES.map((s) => (
                <li key={s.name} style={{ color: 'var(--boacms-gray-800)', padding: '0.25rem 0', fontWeight: '500' }}>
                  {s.name}
                </li>
              ))}
            </ul>
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
            <p>Email: apas@barangay.io</p>
            <p>Phone: (032) 123-4567</p>
            <p>Address: UCMA Village, Apas, Cebu City, Cebu 6000</p>
          </div>
        </div>
      </section>
    </div>
  );
}

export default DashboardHome;
