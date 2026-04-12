package edu.cit.esparcia.barangayio.repository;

import edu.cit.esparcia.barangayio.model.Appointment;
import edu.cit.esparcia.barangayio.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    List<Appointment> findByResidentId(UUID residentId);
    
    List<Appointment> findByResidentIdOrderByAppointmentDateAsc(UUID residentId);
    
    List<Appointment> findByStatus(AppointmentStatus status);
    
    List<Appointment> findByResidentIdAndStatus(UUID residentId, AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.status NOT IN :excludedStatuses")
    List<Appointment> findByDateAndStatusNotIn(@Param("date") LocalDate date, 
                                           @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date AND a.appointmentTime = :time AND a.status NOT IN :excludedStatuses")
    long countByDateAndTimeAndStatusNotIn(@Param("date") LocalDate date, 
                                        @Param("time") LocalTime time, 
                                        @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.status NOT IN :excludedStatuses")
    List<Appointment> findByDateAndStatusNotInOrderByTime(@Param("date") LocalDate date, 
                                                    @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date AND a.status NOT IN :excludedStatuses")
    long countByDateAndStatusNotIn(@Param("date") LocalDate date, 
                                  @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);
}
