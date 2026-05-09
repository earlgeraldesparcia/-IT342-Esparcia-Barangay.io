import { useMemo, useState, useEffect } from 'react';
import { getStoredRole } from '../utils/authDisplay';

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
    row.status === 'claimed' ? 'Claimed' : 
    row.status === 'completed' ? 'Completed' : 
    (row.status === 'no_show' || row.status === 'cancelled') ? 'Cancelled / No Show' : 
    row.status === 'pending' ? 'Pending' : 'Approved';

  const badgeStyle =
    row.status === 'claimed' ? { background: '#F59E0B' } : 
    row.status === 'completed' ? { background: '#10B981' } : 
    (row.status === 'no_show' || row.status === 'cancelled') ? { background: '#EF4444' } : 
    row.status === 'pending' ? { background: '#6B7280' } : 
    { background: '#3B82F6' };

  const isPast = new Date(row.date) < new Date(new Date().setHours(0, 0, 0, 0));

  const handleNoShow = () => {
    onStatusUpdate(row.id, 'no_show');
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
              {new Date(row.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })} at {row.time}
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
        {row.status === 'completed' || row.status === 'no_show' || row.status === 'cancelled' ? null : (
          isPast ? (
            <button type="button" className="appt-btn appt-btn-outline" onClick={handleNoShow}>
              Mark as No show
            </button>
          ) : (
            <button type="button" className="appt-btn appt-btn-primary" onClick={handleMarkCompleted}>
              Mark as completed
            </button>
          )
        )}
      </div>
    </article>
  );
}

function formatDisplayDate(d) {
  return d.toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' });
}

function AdminAppointments() {
  const role = getStoredRole();
  const [appointments, setAppointments] = useState([]);
  const [stats, setStats] = useState({ total: 0, completed: 0, residents: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  if (role !== 'admin') {
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

      // Fetch global stats
      const statsResponse = await fetch(`http://localhost:8080/api/stats/dashboard`, {
        headers: {
          'Cache-Control': 'no-cache, no-store, must-revalidate',
          'Pragma': 'no-cache',
          'Expires': '0'
        }
      });
      if (statsResponse.ok) {
        const statsData = await statsResponse.json();
        setStats({
          total: statsData.scheduledToday,
          completed: statsData.completedToday,
          residents: statsData.registeredResidents
        });
      }

      // Fetch all appointments
      const apptResponse = await fetch(`http://localhost:8080/api/appointments`, {
        headers: {
          'Cache-Control': 'no-cache, no-store, must-revalidate',
          'Pragma': 'no-cache',
          'Expires': '0'
        }
      });
      if (apptResponse.ok) {
        const rawAppts = await apptResponse.json();
        
        // Map raw appointments to UI format
        const formattedAppts = await Promise.all(rawAppts.map(async (apt) => {
          let userName = 'Unknown Resident';
          let userPhone = 'N/A';
          let userEmail = 'N/A';
          
          if (apt.userId) {
            try {
              const userRes = await fetch(`http://localhost:8080/api/stats/users/${apt.userId}`);
              if (userRes.ok) {
                const userData = await userRes.json();
                userName = `${userData.firstName} ${userData.lastName}`;
                userPhone = userData.phoneNumber || 'N/A';
                userEmail = userData.email || 'N/A';
              }
            } catch (e) {}
          }

          // Ensure time formatting if it comes as "09:00:00"
          let displayTime = apt.appointmentTime;
          if (displayTime && displayTime.includes(':')) {
            const [h, m] = displayTime.split(':');
            const hour = parseInt(h, 10);
            const ampm = hour >= 12 ? 'PM' : 'AM';
            const hour12 = hour % 12 || 12;
            displayTime = `${hour12}:${m} ${ampm}`;
          }

          const formatEnum = (str) => {
            if (!str) return '';
            return str.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
          };

          return {
            id: apt.id,
            name: userName,
            date: apt.appointmentDate,
            time: displayTime,
            cert: formatEnum(apt.certificateType),
            purpose: formatEnum(apt.purpose),
            phone: userPhone,
            email: userEmail,
            status: apt.status.toLowerCase(),
          };
        }));

        setAppointments(formattedAppts);
      }
    } catch (error) {
      console.error('Error fetching appointments:', error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const handleStatusUpdate = async (appointmentId, newStatus) => {
    try {
      let endpoint = '';
      if (newStatus === 'claimed') endpoint = 'claim';
      else if (newStatus === 'completed') endpoint = 'complete';
      else if (newStatus === 'no_show') endpoint = 'cancel';
      
      if (endpoint) {
        await fetch(`http://localhost:8080/api/appointments/${appointmentId}/${endpoint}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: newStatus === 'no_show' ? JSON.stringify({ reason: 'No show' }) : null
        });
      }
      fetchAppointments(); // Refresh data
    } catch (error) {
      console.error('Error updating appointment status:', error);
    }
  };

  const today = new Date();
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

  const activeAppointments = appointments.filter(apt => apt.status === 'approved' || apt.status === 'claimed' || apt.status === 'pending');
  const todaysAppointments = activeAppointments.filter(apt => apt.date === todayStr);
  const overdueAppointments = activeAppointments.filter(apt => apt.date < todayStr);
  const upcomingAppointments = activeAppointments.filter(apt => apt.date > todayStr);
  const completedAppointments = appointments.filter(apt => apt.status === 'completed' || apt.status === 'cancelled' || apt.status === 'no_show');

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
      </header>

      <div className="appt-stats-grid">
        <div className="appt-stat-card appt-stat-blue">
          <div className="appt-stat-icon">
            <IconCalendar />
          </div>
          <div>
            <p className="appt-stat-label">Scheduled today</p>
            <h3 className="appt-stat-value">{loading ? '...' : stats.total}</h3>
          </div>
        </div>
        <div className="appt-stat-card appt-stat-green">
          <div className="appt-stat-icon">
            <IconCheck />
          </div>
          <div>
            <p className="appt-stat-label">Completed today</p>
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

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', alignItems: 'start' }}>
        
        {/* Left Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <SessionCard title="Today's Appointments" count={todaysAppointments.length} emptyLabel="No appointments scheduled for today">
            {todaysAppointments.map((row) => (
              <AppointmentMgmtCard key={row.id} row={row} onStatusUpdate={handleStatusUpdate} />
            ))}
          </SessionCard>

          <SessionCard title="Completed / Cancelled" count={completedAppointments.length} emptyLabel="No completed or cancelled appointments">
            {completedAppointments.map((row) => (
              <AppointmentMgmtCard key={row.id} row={row} onStatusUpdate={handleStatusUpdate} />
            ))}
          </SessionCard>
        </div>

        {/* Right Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <SessionCard title="Overdue Appointments" count={overdueAppointments.length} emptyLabel="No overdue appointments">
            {overdueAppointments.map((row) => (
              <AppointmentMgmtCard key={row.id} row={row} onStatusUpdate={handleStatusUpdate} />
            ))}
          </SessionCard>

          <SessionCard title="Upcoming Appointments" count={upcomingAppointments.length} emptyLabel="No upcoming appointments">
            {upcomingAppointments.map((row) => (
              <AppointmentMgmtCard key={row.id} row={row} onStatusUpdate={handleStatusUpdate} />
            ))}
          </SessionCard>
        </div>
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
