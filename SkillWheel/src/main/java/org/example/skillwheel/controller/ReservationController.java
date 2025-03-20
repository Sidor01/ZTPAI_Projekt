package org.example.skillwheel.controller;

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
                .filter(res -> res.studentID.equals(studentId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<?> getReservationsByInstructorId(@PathVariable Long instructorId) {
        List<Reservation> result = reservations.values().stream()
                .filter(res -> res.instructorID.equals(instructorId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    static class Reservation {
        public Long id;
        public Boolean isReserved;
        public Long studentID;
        public Long instructorID;
        public LocalDate reservationDate;
        public LocalTime reservationTime;
    }
}