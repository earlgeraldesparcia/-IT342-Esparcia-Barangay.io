import { useEffect, useRef } from 'react';
import './GoogleLoginButton.css';

function GoogleLoginButton({ onSuccess }) {
  const buttonRef = useRef(null);

  useEffect(() => {
    // Load Google Sign-In API script
    const loadGoogleScript = () => {
      const existingScript = document.getElementById('google-signin-script');
      if (existingScript) {
        initializeGoogleButton();
        return;
      }

      const script = document.createElement('script');
      script.id = 'google-signin-script';
      script.src = 'https://accounts.google.com/gsi/client';
      script.async = true;
      script.defer = true;
      script.onload = initializeGoogleButton;
      document.body.appendChild(script);
    };

    const initializeGoogleButton = () => {
      if (window.google && buttonRef.current) {
        window.google.accounts.id.initialize({
          client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID || '',
          callback: onSuccess,
          auto_select: false,
          cancel_on_tap_outside: true,
        });

        window.google.accounts.id.renderButton(buttonRef.current, {
          theme: 'outline',
          size: 'large',
          width: '100%',
          text: 'signin_with',
          shape: 'rectangular',
        });
      }
    };

    loadGoogleScript();

    return () => {
      // Cleanup if needed
    };
  }, [onSuccess]);

  return (
    <div className="google-login-container">
      <div ref={buttonRef} className="google-login-button"></div>
    </div>
  );
}

export default GoogleLoginButton;
