function decodeJwtPayload(token) {
  try {
    const part = token.split('.')[1];
    if (!part) return null;
    const base64 = part.replace(/-/g, '+').replace(/_/g, '/');
    const pad = base64.length % 4 ? '='.repeat(4 - (base64.length % 4)) : '';
    return JSON.parse(atob(base64 + pad));
  } catch {
    return null;
  }
}

/**
 * @param {unknown} raw
 * @returns {'admin' | 'resident'}
 */
export function normalizeRole(raw) {
  if (raw == null) return 'resident';
  const r = String(raw).toLowerCase().trim();
  if (r === 'admin' || r === 'role_admin') {
    return 'admin';
  }
  return 'resident';
}

/**
 * @returns {'admin' | 'resident'}
 */
export function getStoredRole() {
  const token = localStorage.getItem('token');
  if (token) {
    const payload = decodeJwtPayload(token);
    if (payload?.role != null) {
      const n = normalizeRole(payload.role);
      localStorage.setItem('role', n);
      return n;
    }
  }
  return normalizeRole(localStorage.getItem('role'));
}

/**
 * @returns {'admin' | 'resident'}
 */
export function persistAuthFromLogin(data) {
  if (data.token) {
    localStorage.setItem('token', data.token);
  }
  if (data.firstName) {
    localStorage.setItem('firstName', data.firstName);
  }
  const role = normalizeRole(data.role);
  localStorage.setItem('role', role);
  return role;
}

export function clearAuthStorage() {
  localStorage.removeItem('token');
  localStorage.removeItem('firstName');
  localStorage.removeItem('role');
}

/**
 * @param {'admin' | 'resident'} role
 */
export function homePathForRole(role) {
  return role === 'admin' ? '/admin/dashboard' : '/resident/dashboard';
}

/**
 * Resolves the user's first name for UI: JWT claim (preferred), then localStorage from login.
 */
export function getDisplayFirstName() {
  const token = localStorage.getItem('token');
  if (token) {
    const payload = decodeJwtPayload(token);
    const fromJwt =
      typeof payload?.firstName === 'string' ? payload.firstName.trim() : '';
    if (fromJwt) {
      localStorage.setItem('firstName', fromJwt);
      return fromJwt;
    }
  }
  const stored = localStorage.getItem('firstName')?.trim();
  if (stored) return stored;
  return 'User';
}
