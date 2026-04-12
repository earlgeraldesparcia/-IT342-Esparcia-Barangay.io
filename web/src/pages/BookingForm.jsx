import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './BookingForm.css';

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

const CERTIFICATE_TYPES = [
  { value: 'barangay_clearance', label: 'Barangay Clearance' },
  { value: 'certificate_of_indigency', label: 'Certificate of Indigency' },
  { value: 'community_tax_certificate', label: 'Community Tax Certificate' },
  { value: 'solo_parent_certificate', label: 'Solo Parent Certificate' },
];

const PURPOSE_CHOICES = [
  { value: '', label: '---------', disabled: true },
  { value: 'employment', label: 'Employment' },
  { value: 'business_permit', label: 'Business Permit' },
  { value: 'government_benefits', label: 'Government Benefits' },
  { value: 'loan_application', label: 'Loan Application' },
  { value: 'travel', label: 'Travel' },
  { value: 'education', label: 'Education' },
  { value: 'others', label: 'Others (Please Specify)' },
];

const TIME_SLOTS = [
  { value: '08:00', label: '8:00 AM' },
  { value: '08:30', label: '8:30 AM' },
  { value: '09:00', label: '9:00 AM' },
  { value: '09:30', label: '9:30 AM' },
  { value: '10:00', label: '10:00 AM' },
  { value: '10:30', label: '10:30 AM' },
  { value: '11:00', label: '11:00 AM' },
  { value: '11:30', label: '11:30 AM' },
  { value: '13:00', label: '1:00 PM' },
  { value: '13:30', label: '1:30 PM' },
  { value: '14:00', label: '2:00 PM' },
  { value: '14:30', label: '2:30 PM' },
  { value: '15:00', label: '3:00 PM' },
  { value: '15:30', label: '3:30 PM' },
  { value: '16:00', label: '4:00 PM' },
  { value: '16:30', label: '4:30 PM' },
];

export default function BookingForm() {
  const [formData, setFormData] = useState({
    certificate_type: '',
    preferred_date: '',
    preferred_time: '',
    purpose: '',
    specify_purpose: '',
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showCustomPurpose, setShowCustomPurpose] = useState(false);

  const monthNames = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  ];

  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedSession, setSelectedSession] = useState(null);

  const getDaysInMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  };

  const getFirstDayOfMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth(), 1).getDay();
  };

  const renderCalendar = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const daysInMonth = getDaysInMonth(currentDate);
    const firstDay = getFirstDayOfMonth(currentDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const days = [];
    
    // Add empty cells for days before month starts
    for (let i = 0; i < firstDay; i++) {
      days.push(<div key={`empty-${i}`} className="calendar-date other-month"></div>);
    }

    // Add days of the month
    for (let day = 1; day <= daysInMonth; day++) {
      const dateObj = new Date(year, month, day);
      dateObj.setHours(0, 0, 0, 0);
      const isPast = dateObj < today;
      const isToday = dateObj.getTime() === today.getTime();
      const isSelected = selectedDate && dateObj.getTime() === selectedDate.getTime();

      days.push(
        <div
          key={day}
          className={`calendar-date ${isPast ? 'disabled' : ''} ${isToday ? 'today' : ''} ${isSelected ? 'selected' : ''}`}
          onClick={() => !isPast && selectDate(dateObj)}
        >
          {day}
        </div>
      );
    }

    return days;
  };

  const selectDate = (date) => {
    setSelectedDate(date);
    const formattedDate = date.toISOString().split('T')[0];
    setFormData(prev => ({ ...prev, preferred_date: formattedDate }));
  };

  const selectSession = (session) => {
    setSelectedSession(session);
    const timeValue = session === 'am' ? '08:00' : '13:00';
    setFormData(prev => ({ ...prev, preferred_time: timeValue }));
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    if (name === 'purpose') {
      setShowCustomPurpose(value === 'others');
      if (value !== 'others') {
        setFormData(prev => ({ ...prev, specify_purpose: '' }));
      }
    }

    // Clear error for this field when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.certificate_type) {
      newErrors.certificate_type = 'Please select a certificate type';
    }

    if (!formData.preferred_date) {
      newErrors.preferred_date = 'Please select a date';
    }

    if (!formData.preferred_time) {
      newErrors.preferred_time = 'Please select a time slot';
    }

    if (!formData.purpose) {
      newErrors.purpose = 'Please select a purpose';
    }

    if (formData.purpose === 'others' && !formData.specify_purpose.trim()) {
      newErrors.specify_purpose = 'Please specify your purpose';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      // Map form data to backend API format
      const appointmentData = {
        residentId: crypto.randomUUID(), // Generate a random UUID for now
        certificateType: formData.certificate_type.toUpperCase(),
        preferredDate: formData.preferred_date,
        preferredTime: formData.preferred_time,
        purpose: formData.purpose.toUpperCase(),
        specifyPurpose: formData.specify_purpose
      };

      const response = await fetch('http://localhost:8080/api/appointments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(appointmentData),
      });

      if (response.ok) {
        const createdAppointment = await response.json();
        alert('Appointment booked successfully!');
        
        // Reset form
        setFormData({
          certificate_type: '',
          preferred_date: '',
          preferred_time: '',
          purpose: '',
          specify_purpose: '',
        });
        setSelectedDate(null);
        setSelectedSession(null);
        setShowCustomPurpose(false);
        
        // Redirect to appointments page
        window.location.href = '/resident/appointments';
      } else {
        const errorData = await response.json();
        alert(`Error: ${errorData.message || 'Failed to book appointment'}`);
      }
    } catch (error) {
      console.error('Error booking appointment:', error);
      alert('Error: Failed to book appointment. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const changeMonth = (direction) => {
    setCurrentDate(prev => {
      const newDate = new Date(prev);
      newDate.setMonth(prev.getMonth() + direction);
      return newDate;
    });
  };

  return (
    <div className="booking-form-container">
      <div className="booking-form-wrapper">
        <div className="booking-form-card">
          <div className="form-header">
            <div className="header-content">
              <h2 className="form-title">Book a Certificate</h2>
              <p className="form-subtitle">Provide your appointment details.</p>
            </div>
          </div>

          <div className="form-body">
            {errors.submit && (
              <div className="alert alert-error">
                {errors.submit}
              </div>
            )}

            <form onSubmit={handleSubmit} className="appointment-form">
              <div className="form-grid">
                <div className="form-field">
                  <label htmlFor="certificate_type">Certificate Type</label>
                  <select
                    id="certificate_type"
                    name="certificate_type"
                    value={formData.certificate_type}
                    onChange={handleInputChange}
                    className={`form-control ${errors.certificate_type ? 'error' : ''}`}
                  >
                    <option value="">Select a certificate type</option>
                    {CERTIFICATE_TYPES.map(type => (
                      <option key={type.value} value={type.value}>
                        {type.label}
                      </option>
                    ))}
                  </select>
                  {errors.certificate_type && (
                    <span className="field-error">{errors.certificate_type}</span>
                  )}
                </div>

                <div className="form-field">
                  <label htmlFor="purpose">Purpose</label>
                  <select
                    id="purpose"
                    name="purpose"
                    value={formData.purpose}
                    onChange={handleInputChange}
                    className={`form-control ${errors.purpose ? 'error' : ''}`}
                  >
                    {PURPOSE_CHOICES.map(purpose => (
                      <option 
                        key={purpose.value} 
                        value={purpose.value}
                        disabled={purpose.disabled}
                      >
                        {purpose.label}
                      </option>
                    ))}
                  </select>
                  {errors.purpose && (
                    <span className="field-error">{errors.purpose}</span>
                  )}
                </div>

                {showCustomPurpose && (
                  <div className="form-field full-width">
                    <label htmlFor="specify_purpose">Please specify your purpose</label>
                    <input
                      type="text"
                      id="specify_purpose"
                      name="specify_purpose"
                      value={formData.specify_purpose}
                      onChange={handleInputChange}
                      placeholder="Please specify your purpose"
                      className={`form-control ${errors.specify_purpose ? 'error' : ''}`}
                    />
                    {errors.specify_purpose && (
                      <span className="field-error">{errors.specify_purpose}</span>
                    )}
                  </div>
                )}

                <div className="form-field full-width">
                  <label>Select Date and Time</label>
                  <div className="date-time-container">
                    {/* Calendar */}
                    <div className="calendar-container">
                      <div className="calendar-header">
                        <button type="button" className="nav-btn" onClick={() => changeMonth(-1)}>
                          ‹
                        </button>
                        <h3>{monthNames[currentDate.getMonth()]} {currentDate.getFullYear()}</h3>
                        <button type="button" className="nav-btn" onClick={() => changeMonth(1)}>
                          ›
                        </button>
                      </div>
                      <div className="calendar-grid">
                        {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
                          <div key={day} className="calendar-day-header">{day}</div>
                        ))}
                        {renderCalendar()}
                      </div>
                    </div>

                    {/* Time Selection */}
                    <div className="time-container">
                      <h3>Select Time Slot</h3>
                      <div className="time-slots">
                        {TIME_SLOTS.map(slot => (
                          <button
                            key={slot.value}
                            type="button"
                            className={`time-slot ${formData.preferred_time === slot.value ? 'selected' : ''}`}
                            onClick={() => setFormData(prev => ({ ...prev, preferred_time: slot.value }))}
                            disabled={!formData.preferred_date}
                          >
                            {slot.label}
                          </button>
                        ))}
                      </div>
                    </div>
                  </div>
                  {errors.preferred_date && (
                    <span className="field-error">{errors.preferred_date}</span>
                  )}
                  {errors.preferred_time && (
                    <span className="field-error">{errors.preferred_time}</span>
                  )}
                </div>
              </div>

              <div className="form-actions">
                <Link to="/resident/dashboard" className="btn btn-secondary">
                  Cancel
                </Link>
                <button 
                  type="submit" 
                  className="btn btn-primary"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Booking...' : 'Book Appointment'}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
