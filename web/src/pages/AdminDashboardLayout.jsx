import { useEffect, useRef, useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { clearAuthStorage, getDisplayFirstName } from '../utils/authDisplay';
import './Dashboard.css';

function UserIcon() {
  return (
    <svg className="user-menu-icon" width="22" height="22" viewBox="0 0 24 24" aria-hidden="true">
      <path
        fill="currentColor"
        d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
      />
    </svg>
  );
}

function BrandMark() {
  return (
    <div className="d-nav-logo-fallback d-nav-logo-fallback--admin" aria-hidden="true">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
      </svg>
    </div>
  );
}

export default function AdminDashboardLayout() {
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);
  const [firstName] = useState(() => getDisplayFirstName());

  useEffect(() => {
    if (!menuOpen) return;
    const handlePointerDown = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handlePointerDown);
    return () => document.removeEventListener('mousedown', handlePointerDown);
  }, [menuOpen]);

  const handleLogout = () => {
    clearAuthStorage();
    setMenuOpen(false);
    navigate('/login');
  };

  const navClass = ({ isActive }) => `d-appbar-link${isActive ? ' active' : ''}`;

  return (
    <div className="dashboard-page dashboard-page--admin">
      <nav className="dashboard-navbar">
        <div className="nav-container">
          <div className="nav-brand">
            <BrandMark />
            <div className="brand-text">
              <span className="brand-title">Barangay.io</span>
              <span className="brand-subtitle">
                Admin console
                <span className="d-role-pill d-role-pill--admin">Admin</span>
              </span>
            </div>
          </div>

          <div className="nav-menu">
            <NavLink to="/admin/dashboard" end className={navClass}>
              Overview
            </NavLink>
            <NavLink to="/admin/appointments" className={navClass}>
              Appointment desk
            </NavLink>
          </div>

          <div className="d-navbar-tools">
            <input
              type="search"
              className="d-navbar-search"
              placeholder="Search…"
              aria-label="Search"
            />
            <div className="user-menu" ref={menuRef}>
              <button
                type="button"
                className="d-user-trigger"
                aria-expanded={menuOpen}
                aria-haspopup="true"
                onClick={() => setMenuOpen((o) => !o)}
              >
                <span className="user-info">{firstName}</span>
                <UserIcon />
              </button>
              {menuOpen && (
                <div className="dashboard-user-menu" role="menu">
                  <button
                    type="button"
                    className="dashboard-menu-item"
                    role="menuitem"
                    onClick={() => {
                      setMenuOpen(false);
                      navigate('/settings');
                    }}
                  >
                    Settings
                  </button>
                  <button type="button" className="dashboard-menu-item" role="menuitem" onClick={handleLogout}>
                    Logout
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </nav>

      <main className="main-content">
        <Outlet />
      </main>

      <footer className="dashboard-footer">
        <p>© {new Date().getFullYear()} Barangay.io — Admin / staff tools (layout inspired by BOACMS).</p>
      </footer>
    </div>
  );
}
