package edu.cit.esparcia.barangayio.feature.stats;

import edu.cit.esparcia.barangayio.feature.appointment.AppointmentStatus;
import edu.cit.esparcia.barangayio.feature.appointment.AppointmentRepository;
import edu.cit.esparcia.barangayio.feature.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class StatsController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@RequestParam(required = false) String date) {
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        Map<String, Object> stats = new HashMap<>();
        
        // Pending approvals removed as per new logic
                
        long scheduledTodayCount = appointmentRepository.countByDateAndStatusNotIn(
            targetDate, 
            Arrays.asList(AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED)
        );

        long completedCount = appointmentRepository.findByStatus(AppointmentStatus.COMPLETED).stream()
                .filter(a -> a.getAppointmentDate().equals(targetDate))
                .count();
                
        long residentCount = userRepository.count();

        // stats.put("pendingApprovals", pendingCount); // Removed
        stats.put("scheduledToday", scheduledTodayCount);
        stats.put("completedToday", completedCount);
        stats.put("registeredResidents", residentCount);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable java.util.UUID id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
