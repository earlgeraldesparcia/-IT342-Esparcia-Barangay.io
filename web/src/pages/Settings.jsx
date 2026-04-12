import { Link } from 'react-router-dom';
import { getStoredRole, homePathForRole } from '../utils/authDisplay';
import './Settings.css';

function Settings() {
  const backTo = homePathForRole(getStoredRole());

  return (
    <div className="settings-page">
      <p>
        <Link to={backTo}>← Back to dashboard</Link>
      </p>
      <h1>Settings</h1>
      <p className="settings-placeholder">Configure your account here later.</p>
    </div>
  );
}

export default Settings;
