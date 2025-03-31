package org.example.skillwheel.controller;

import org.example.skillwheel.model.Reservation;
import org.example.skillwheel.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", reservations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getReservationsByStudentId(@PathVariable Long studentId) {
        List<Reservation> result = reservationService.getReservationsByStudentId(studentId);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<?> getReservationsByInstructorId(@PathVariable Long instructorId) {
        List<Reservation> result = reservationService.getReservationsByInstructorId(instructorId);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    @PostMapping
    public ResponseEntity<?> addReservation(@RequestBody Reservation reservation) {
        if (reservation.getId() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "error", "New reservation should not have an ID"));
        }
        Reservation savedReservation = reservationService.addReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", HttpStatus.CREATED.value(), "reservation", savedReservation));
    }

    @PutMapping("/{id}/time")
    public ResponseEntity<?> updateReservationTime(@PathVariable Long id, @RequestBody LocalTime newTime) {
        Optional<Reservation> reservation = reservationService.updateReservationTime(id, newTime);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @PutMapping("/{id}/date")
    public ResponseEntity<?> updateReservationDate(@PathVariable Long id, @RequestBody LocalDate newDate) {
        Optional<Reservation> reservation = reservationService.updateReservationDate(id, newDate);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @PutMapping("/{id}/instructor")
    public ResponseEntity<?> updateInstructor(@PathVariable Long id, @RequestBody Long newInstructorId) {
        Optional<Reservation> reservation = reservationService.updateInstructor(id, newInstructorId);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @PutMapping("/{id}/student")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Long newStudentId) {
        Optional<Reservation> reservation = reservationService.updateStudent(id, newStudentId);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @PutMapping("/{id}/place")
    public ResponseEntity<?> updateReservationPlace(@PathVariable Long id, @RequestBody String newPlace) {
        Optional<Reservation> reservation = reservationService.updateReservationPlace(id, newPlace);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        boolean isDeleted = reservationService.deleteReservation(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Reservation deleted successfully"));
    }
}