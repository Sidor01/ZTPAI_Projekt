package org.example.skillwheel.controller;

import jakarta.validation.Valid;
import org.example.skillwheel.model.Reservation;
import org.example.skillwheel.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
    public ResponseEntity<?> addReservation(@Valid @RequestBody Reservation reservation, BindingResult bindingResult) {
        // Walidacja istnienia ID
        if (reservation.getId() != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("error", "New reservation should not have an ID");
            return ResponseEntity.badRequest().body(response);
        }

        // Walidacja danych wejściowych
        if (bindingResult.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
            response.put("error", "Validation failed");
            return ResponseEntity.unprocessableEntity().body(response);
        }

        // Walidacja daty
        if (reservation.getReservationDate() == null || reservation.getReservationDate().isBefore(LocalDate.now())) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
            response.put("error", "Future date required");
            return ResponseEntity.unprocessableEntity().body(response);
        }

        Reservation savedReservation = reservationService.addReservation(reservation);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("reservation", savedReservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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