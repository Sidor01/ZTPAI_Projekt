package org.example.skillwheel.controller;

import org.example.skillwheel.model.Reservation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private Map<Long, Reservation> reservations = new HashMap<>();

    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", reservations.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        if (!reservations.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservations.get(id)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getReservationsByStudentId(@PathVariable Long studentId) {
        List<Reservation> result = reservations.values().stream()
                .filter(res -> res.getStudentID().equals(studentId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<?> getReservationsByInstructorId(@PathVariable Long instructorId) {
        List<Reservation> result = reservations.values().stream()
                .filter(res -> res.getInstructorID().equals(instructorId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    @PostMapping
    public ResponseEntity<?> addReservation(@RequestBody Reservation reservation) {
        if (reservations.containsKey(reservation.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "error", "Reservation already exists"));
        }
        reservations.put(reservation.getId(), reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", HttpStatus.CREATED.value(), "reservation", reservation));
    }

    @PutMapping("/{id}/time")
    public ResponseEntity<?> updateReservationTime(@PathVariable Long id, @RequestBody LocalTime newTime) {
        if (!reservations.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        reservations.get(id).setReservationTime(newTime);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservations.get(id)));
    }

    @PutMapping("/{id}/date")
    public ResponseEntity<?> updateReservationDate(@PathVariable Long id, @RequestBody LocalDate newDate) {
        if (!reservations.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        reservations.get(id).setReservationDate(newDate);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservations.get(id)));
    }

    @PutMapping("/{id}/instructor")
    public ResponseEntity<?> updateInstructor(@PathVariable Long id, @RequestBody Long newInstructorId) {
        if (!reservations.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        reservations.get(id).setInstructorID(newInstructorId);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservations.get(id)));
    }

    @PutMapping("/{id}/student")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Long newStudentId) {
        if (!reservations.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        reservations.get(id).setStudentID(newStudentId);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservations.get(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        if (!reservations.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        reservations.remove(id);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Reservation deleted successfully"));
    }
}