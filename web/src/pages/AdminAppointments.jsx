import { useMemo, useState, useEffect } from 'react';
import { appointmentService } from '../services/appointmentService';
import { useAuth } from '../contexts/AuthContext';

function IconCalendar() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="22" height="22" aria-hidden="true">
      <rect x="3" y="4" width="18" height="18" rx="2" />
      <path d="M16 2v4M8 2v4M3 10h18" />
    </svg>
  );
}

function IconCheck() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="22" height="22" aria-hidden="true">
      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
      <polyline points="22 4 12 14.01 9 11.01" />
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

function IconClock() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18" aria-hidden="true">
      <circle cx="12" cy="12" r="10" />
      <path d="M12 6v6l4 2" />
    </svg>
  );
}

function IconFile() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16" aria-hidden="true">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <path d="M14 2v6h6" />
    </svg>
  );
}

function IconInfo() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16" aria-hidden="true">
      <circle cx="12" cy="12" r="10" />
      <path d="M12 16v-4M12 8h.01" />
    </svg>
  );
}

function IconPhone() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16" aria-hidden="true">
      <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.44 12.44 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.44 12.44 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
    </svg>
  );
}

function IconMail() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16" aria-hidden="true">
      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
      <polyline points="22,6 12,13 2,6" />
    </svg>
  );
}

function IconMapPin() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16" aria-hidden="true">
      <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
      <circle cx="12" cy="10" r="3" />
    </svg>
  );
}


function SessionCard({ title, count, children, emptyLabel }) {
  return (
    <div className="appt-session-section">
      <div className="appt-session-header">
        <div className="appt-session-title">
          <IconClock />
          <h3>{title}</h3>
        </div>
        <span className="appt-session-badge">{count} appointments</span>
      </div>
      <div className="appt-appointments-list">
        {count === 0 ? (
          <div className="appt-empty-state">
            <IconCalendar />
            <p>{emptyLabel}</p>
          </div>
        ) : (
          children
        )}
      </div>
    </div>
  );
}

function AppointmentMgmtCard({ row, onStatusUpdate }) {
  const statusLabel =
    row.status === 'claimed' ? 'Claimed - Awaiting confirmation' : 
    row.status === 'completed' ? 'Completed' : 
    row.status === 'no_show' ? 'No Show' : 
    row.status === 'pending' ? 'Pending' : 'Confirmed';
  const badgeStyle =
    row.status === 'claimed'
      ? { background: '#F59E0B' }
      : row.status === 'completed'
        ? { background: '#10B981' }
        : row.status === 'no_show'
          ? { background: '#EF4444' }
          : row.status === 'pending'
            ? { background: '#6B7280' }
            : {};

  const handleNoShow = () => {
    onStatusUpdate(row.id, 'no_show');
  };

  const handleMarkClaimed = () => {
    onStatusUpdate(row.id, 'claimed');
  };

  const handleMarkCompleted = () => {
    onStatusUpdate(row.id, 'completed');
  };

  return (
    <article className="appt-mgmt-card">
      <div className="appt-mgmt-card-head">
        <div className="appt-resident-row">
          <div className="appt-resident-avatar" aria-hidden="true">
            {row.name
              .split(' ')
              .map((p) => p[0])
              .join('')
              .slice(0, 2)
              .toUpperCase()}
          </div>
          <div>
            <h4>{row.name}</h4>
            <span className="appt-time">
              <IconClock />
              {row.time}
            </span>
          </div>
        </div>
        <span className="appt-status-pill" style={badgeStyle}>
          {statusLabel}
        </span>
      </div>
      <div className="appt-mgmt-card-body">
        <div className="appt-detail-grid">
          <div className="appt-detail-cell">
            <IconFile />
            <div>
              <span className="appt-detail-lbl">Certificate type</span>
              <span className="appt-detail-val">{row.cert}</span>
            </div>
          </div>
          <div className="appt-detail-cell">
            <IconInfo />
            <div>
              <span className="appt-detail-lbl">Purpose</span>
              <span className="appt-detail-val">{row.purpose}</span>
            </div>
          </div>
        </div>
        <div className="appt-contact-row">
          <span className="appt-contact-item">
            <IconPhone />
            {row.phone}
          </span>
          <span className="appt-contact-item">
            <IconMail />
            {row.email}
          </span>
        </div>
      </div>
      <div className="appt-mgmt-actions">
        {row.status !== 'no_show' && (
          <button type="button" className="appt-btn appt-btn-outline" onClick={handleNoShow}>
            No show
          </button>
        )}
        {row.status === 'confirmed' && (
          <button type="button" className="appt-btn appt-btn-primary" onClick={handleMarkClaimed}>
            Mark as claimed
          </button>
        )}
        {row.status === 'claimed' && (
          <button type="button" className="appt-btn appt-btn-primary" onClick={handleMarkCompleted}>
            Mark as completed
          </button>
        )}
        {row.status === 'completed' && (
          <button type="button" className="appt-btn appt-btn-primary" disabled>
            Completed
          </button>
        )}
        {row.status === 'no_show' && (
          <button type="button" className="appt-btn appt-btn-outline" disabled>
            No show
          </button>
        )}
      </div>
    </article>
  );
}

function formatDisplayDate(d) {
  return d.toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' });
}

function AdminAppointments() {
  const { userProfile, isAdministrator } = useAuth();
  const [cursor, setCursor] = useState(() => new Date());
  const [appointments, setAppointments] = useState([]);
  const [stats, setStats] = useState({ total: 0, completed: 0, residents: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const label = useMemo(() => formatDisplayDate(cursor), [cursor]);

  // Redirect if not administrator
  if (!userProfile) {
    return <div>Loading...</div>;
  }

  if (!isAdministrator()) {
    return (
      <div className="unauthorized-container">
        <h2>Access Denied</h2>
        <p>You must be an administrator to access this page.</p>
      </div>
    );
  }

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      setError(null);
      const dateStr = cursor.toISOString().split('T')[0];
      const [appointmentsData, statsData, residentsCount] = await Promise.all([
        appointmentService.getAppointmentsByDate(dateStr),
        appointmentService.getAppointmentStats(dateStr),
        appointmentService.getTotalResidents()
      ]);

      setAppointments(appointmentsData);
      setStats({
        total: statsData.total,
        completed: statsData.completed,
        residents: residentsCount
      });
    } catch (error) {
      console.error('Error fetching appointments:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, [cursor]);

  const shiftDay = (delta) => {
    setCursor((prev) => {
      const n = new Date(prev);
      n.setDate(n.getDate() + delta);
      return n;
    });
  };

  const handleStatusUpdate = async (appointmentId, newStatus) => {
    try {
      await appointmentService.updateAppointmentStatus(appointmentId, newStatus);
      fetchAppointments(); // Refresh data
    } catch (error) {
      console.error('Error updating appointment status:', error);
    }
  };

  // Separate appointments into AM and PM sessions
  const amAppointments = appointments.filter(apt => {
    const hour = parseInt(apt.time.split(':')[0]);
    const period = apt.time.includes('AM') || apt.time.includes('PM');
    if (!period) return hour < 12;
    return apt.time.includes('AM');
  });

  const pmAppointments = appointments.filter(apt => {
    const hour = parseInt(apt.time.split(':')[0]);
    const period = apt.time.includes('AM') || apt.time.includes('PM');
    if (!period) return hour >= 12;
    return apt.time.includes('PM');
  });

  return (
    <div className="appt-mgmt">
      <header className="appt-mgmt-header">
        <div className="appt-mgmt-header-left">
          <h1>
            <IconCalendar />
            Appointment management
          </h1>
          <div className="appt-meta-row">
            <span className="appt-meta-item">
              <IconMapPin />
              Barangay hall
            </span>
          </div>
        </div>
        <div className="appt-date-nav">
          <button type="button" className="appt-date-btn" onClick={() => shiftDay(-1)} aria-label="Previous day">
            ‹
          </button>
          <div className="appt-date-label">{label}</div>
          <button type="button" className="appt-date-btn" onClick={() => shiftDay(1)} aria-label="Next day">
            ›
          </button>
        </div>
      </header>

      <div className="appt-stats-grid">
        <div className="appt-stat-card appt-stat-blue">
          <div className="appt-stat-icon">
            <IconCalendar />
          </div>
          <div>
            <p className="appt-stat-label">Today&apos;s appointments</p>
            <h3 className="appt-stat-value">{loading ? '...' : stats.total}</h3>
          </div>
        </div>
        <div className="appt-stat-card appt-stat-green">
          <div className="appt-stat-icon">
            <IconCheck />
          </div>
          <div>
            <p className="appt-stat-label">Completed</p>
            <h3 className="appt-stat-value">{loading ? '...' : stats.completed}</h3>
          </div>
        </div>
        <div className="appt-stat-card appt-stat-orange">
          <div className="appt-stat-icon">
            <IconUsers />
          </div>
          <div>
            <p className="appt-stat-label">Total residents</p>
            <h3 className="appt-stat-value">{loading ? '...' : stats.residents}</h3>
          </div>
        </div>
      </div>

      <div className="appt-two-col">
        <SessionCard title="AM Session (8:00 AM – 12:00 PM)" count={amAppointments.length} emptyLabel="No appointments for AM session">
          {amAppointments.map((row) => (
            <AppointmentMgmtCard key={row.id} row={row} onStatusUpdate={handleStatusUpdate} />
          ))}
        </SessionCard>
        <SessionCard title="PM Session (1:00 PM – 5:00 PM)" count={pmAppointments.length} emptyLabel="No appointments for PM session">
          {pmAppointments.map((row) => (
            <AppointmentMgmtCard key={row.id} row={row} onStatusUpdate={handleStatusUpdate} />
          ))}
        </SessionCard>
      </div>

      {error && (
        <div className="appt-error">
          <p>Error: {error}</p>
          <button onClick={fetchAppointments} className="appt-btn appt-btn-primary">
            Retry
          </button>
        </div>
      )}
      
      {loading && !error && (
        <div className="appt-loading">
          <p>Loading appointments...</p>
        </div>
      )}
    </div>
  );
}

export default AdminAppointments;
