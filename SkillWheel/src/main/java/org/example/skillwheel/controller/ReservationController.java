package org.example.skillwheel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Pobierz wszystkie rezerwacje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista rezerwacji")
    })
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", reservations));
    }

    @Operation(summary = "Pobierz rezerwację po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rezerwacja znaleziona"),
            @ApiResponse(responseCode = "404", description = "Rezerwacja nie znaleziona")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(
            @Parameter(description = "ID rezerwacji", required = true)
            @PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @Operation(summary = "Pobierz rezerwacje dla studenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista rezerwacji studenta")
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getReservationsByStudentId(
            @Parameter(description = "ID studenta", required = true)
            @PathVariable Long studentId) {
        List<Reservation> result = reservationService.getReservationsByStudentId(studentId);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    @Operation(summary = "Pobierz rezerwacje dla instruktora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista rezerwacji instruktora")
    })
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<?> getReservationsByInstructorId(
            @Parameter(description = "ID instruktora", required = true)
            @PathVariable Long instructorId) {
        List<Reservation> result = reservationService.getReservationsByInstructorId(instructorId);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservations", result));
    }

    @Operation(summary = "Dodaj nową rezerwację")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rezerwacja utworzona"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane (ID nie powinno być podane)"),
            @ApiResponse(responseCode = "422", description = "Błąd walidacji lub data w przeszłości")
    })
    @PostMapping
    public ResponseEntity<?> addReservation(
            @Parameter(description = "Nowa rezerwacja", required = true,
                    content = @Content(schema = @Schema(implementation = Reservation.class)))
            @Valid @RequestBody Reservation reservation,
            BindingResult bindingResult) {

        if (reservation.getId() != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("error", "New reservation should not have an ID");
            return ResponseEntity.badRequest().body(response);
        }

        if (bindingResult.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
            response.put("error", "Validation failed");
            return ResponseEntity.unprocessableEntity().body(response);
        }

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

    @Operation(summary = "Zaktualizuj godzinę rezerwacji")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Godzina rezerwacji zaktualizowana"),
            @ApiResponse(responseCode = "404", description = "Rezerwacja nie znaleziona")
    })
    @PutMapping("/{id}/time")
    public ResponseEntity<?> updateReservationTime(
            @Parameter(description = "ID rezerwacji", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nowa godzina rezerwacji", required = true)
            @RequestBody LocalTime newTime) {
        Optional<Reservation> reservation = reservationService.updateReservationTime(id, newTime);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @Operation(summary = "Zaktualizuj datę rezerwacji")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data rezerwacji zaktualizowana"),
            @ApiResponse(responseCode = "404", description = "Rezerwacja nie znaleziona")
    })
    @PutMapping("/{id}/date")
    public ResponseEntity<?> updateReservationDate(
            @Parameter(description = "ID rezerwacji", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nowa data rezerwacji", required = true)
            @RequestBody LocalDate newDate) {
        Optional<Reservation> reservation = reservationService.updateReservationDate(id, newDate);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @Operation(summary = "Zaktualizuj instruktora w rezerwacji")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instruktor zaktualizowany w rezerwacji"),
            @ApiResponse(responseCode = "404", description = "Rezerwacja nie znaleziona")
    })
    @PutMapping("/{id}/instructor")
    public ResponseEntity<?> updateInstructor(
            @Parameter(description = "ID rezerwacji", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID nowego instruktora", required = true)
            @RequestBody Long newInstructorId) {
        Optional<Reservation> reservation = reservationService.updateInstructor(id, newInstructorId);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @Operation(summary = "Zaktualizuj studenta w rezerwacji")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student zaktualizowany w rezerwacji"),
            @ApiResponse(responseCode = "404", description = "Rezerwacja nie znaleziona")
    })
    @PutMapping("/{id}/student")
    public ResponseEntity<?> updateStudent(
            @Parameter(description = "ID rezerwacji", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID nowego studenta", required = true)
            @RequestBody Long newStudentId) {
        Optional<Reservation> reservation = reservationService.updateStudent(id, newStudentId);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @Operation(summary = "Zaktualizuj miejsce rezerwacji")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Miejsce rezerwacji zaktualizowane"),
            @ApiResponse(responseCode = "404", description = "Rezerwacja nie znaleziona")
    })
    @PutMapping("/{id}/place")
    public ResponseEntity<?> updateReservationPlace(
            @Parameter(description = "ID rezerwacji", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nowe miejsce rezerwacji", required = true)
            @RequestBody String newPlace) {
        Optional<Reservation> reservation = reservationService.updateReservationPlace(id, newPlace);
        if (reservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "reservation", reservation.get()));
    }

    @Operation(summary = "Usuń rezerwację")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rezerwacja usunięta"),
            @ApiResponse(responseCode = "404", description = "Rezerwacja nie znaleziona")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(
            @Parameter(description = "ID rezerwacji", required = true)
            @PathVariable Long id) {
        boolean isDeleted = reservationService.deleteReservation(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Reservation not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Reservation deleted successfully"));
    }
}
