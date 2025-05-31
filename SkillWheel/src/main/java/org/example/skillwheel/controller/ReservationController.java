package org.example.skillwheel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.skillwheel.exception.ResourceNotFoundException;
import org.example.skillwheel.model.Instructor;
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
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reservations", content = @Content(schema = @Schema(implementation = Reservation.class, type = "array")))
        })
        @GetMapping
        public ResponseEntity<?> getAllReservations() {
                List<Reservation> reservations = reservationService.getAllReservations();
                return ResponseEntity.ok(reservations);
        }

        @Operation(summary = "Pobierz rezerwację po ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reservation found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reservation.class))),
                        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"Reservation not found with ID: 1\",\"status\":404}")))
        })
        @GetMapping("/{id}")
        public ResponseEntity<?> getReservationById(
                        @Parameter(description = "ID rezerwacji", required = true) @PathVariable Long id) {
                return reservationService.getReservationById(id)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Reservation not found with ID: " + id));
        }

        @Operation(summary = "Pobierz rezerwacje dla studenta")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved student's reservations", content = @Content(schema = @Schema(implementation = Reservation.class, type = "array"))),
                        @ApiResponse(responseCode = "400", description = "Invalid student ID format", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid parameter\",\"message\":\"Failed to convert value of type 'String' to required type 'Long'\"}")))
        })
        @GetMapping("/student/{studentId}")
        public ResponseEntity<?> getReservationsByStudentId(
                        @Parameter(description = "ID studenta", required = true) @PathVariable Long studentId) {
                List<Reservation> studentReservations = reservationService.getReservationsByStudentId(studentId);
                Map<String, Object> response = Map.of(
                                "status", 200,
                                "reservations", studentReservations);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Pobierz rezerwacje dla instruktora")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved instructor's reservations", content = @Content(schema = @Schema(implementation = Reservation.class, type = "array"))),
                        @ApiResponse(responseCode = "400", description = "Invalid instructor ID format", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid parameter\",\"message\":\"Failed to convert value of type 'String' to required type 'Long'\"}")))
        })
        @GetMapping("/instructor/{instructorId}")
        public ResponseEntity<?> getReservationsByInstructorId(
                        @Parameter(description = "ID instruktora", required = true) @PathVariable Long instructorId) {
                List<Reservation> instructorReservations = reservationService
                                .getReservationsByInstructorId(instructorId);
                Map<String, Object> response = Map.of(
                                "status", 200,
                                "reservations", instructorReservations);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Dodaj nową rezerwację")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Reservation created successfully", content = @Content(schema = @Schema(implementation = Reservation.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input (e.g., reservation with ID provided)", content = @Content(schema = @Schema(example = "{\"error\":\"New reservation should not have an ID\"}"))),
                        @ApiResponse(responseCode = "422", description = "Validation error", content = @Content(schema = @Schema(example = "{\"date\":\"Date must be in the future\",\"time\":\"Time must be during working hours\"}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid JSON input", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid JSON\",\"message\":\"JSON parse error: Unexpected character ('X' (code 88)): was expecting double-quote to start field name\"}")))
        })
        @PostMapping
        public ResponseEntity<?> addReservation(
                        @Parameter(description = "Nowa rezerwacja", required = true, content = @Content(schema = @Schema(implementation = Reservation.class))) @Valid @RequestBody Reservation reservation) {

                if (reservation.getId() != null) {
                        throw new IllegalArgumentException("New reservation should not have an ID");
                }

                Reservation savedReservation = reservationService.addReservation(reservation);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
        }

        @Operation(summary = "Zaktualizuj godzinę rezerwacji")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reservation date updated successfully", content = @Content(schema = @Schema(implementation = Reservation.class))),
                        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(example = "{\"error\":\"Reservation not found with ID: 1\",\"status\":404}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid JSON\",\"message\":\"JSON parse error: Cannot deserialize value of type `java.time.LocalDate` from String \\\"2023-02-30\\\": Text '2023-02-30' could not be parsed: Invalid date 'FEBRUARY 30'\"}")))
        })
        @PutMapping("/{id}/time")
        public ResponseEntity<?> updateReservationTime(@PathVariable Long id, @RequestBody LocalTime newTime) {
                return reservationService.updateReservationTime(id, newTime)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Reservation not found with ID: " + id));
        }

        @Operation(summary = "Zaktualizuj datę rezerwacji")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reservation date updated successfully", content = @Content(schema = @Schema(implementation = Reservation.class))),
                        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(example = "{\"error\":\"Reservation not found with ID: 1\",\"status\":404}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid JSON\",\"message\":\"JSON parse error: Cannot deserialize value of type `java.time.LocalDate` from String \\\"2023-02-30\\\": Text '2023-02-30' could not be parsed: Invalid date 'FEBRUARY 30'\"}")))
        })
        @PutMapping("/{id}/date")
        public ResponseEntity<?> updateReservationDate(@PathVariable Long id, @RequestBody LocalDate newDate) {
                return reservationService.updateReservationDate(id, newDate)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Reservation not found with ID: " + id));
        }

        @Operation(summary = "Zaktualizuj instruktora w rezerwacji")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Instructor updated successfully", content = @Content(schema = @Schema(implementation = Reservation.class))),
                        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(example = "{\"error\":\"Reservation not found with ID: 1\",\"status\":404}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid instructor ID format", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid JSON\",\"message\":\"JSON parse error: Cannot deserialize value of type `java.lang.Long` from String \\\"abc\\\": not a valid Long value\"}")))
        })
        @PutMapping("/{id}/instructor")
        public ResponseEntity<?> updateInstructor(@PathVariable Long id, @RequestBody Long newInstructorId) {
                return reservationService.updateInstructor(id, newInstructorId)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Reservation not found with ID: " + id));
        }

        @Operation(summary = "Zaktualizuj studenta w rezerwacji")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Student updated successfully", content = @Content(schema = @Schema(implementation = Reservation.class))),
                        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(example = "{\"error\":\"Reservation not found with ID: 1\",\"status\":404}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid student ID format", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid JSON\",\"message\":\"JSON parse error: Cannot deserialize value of type `java.lang.Long` from String \\\"abc\\\": not a valid Long value\"}")))
        })
        @PutMapping("/{id}/student")
        public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Long newStudentId) {
                return reservationService.updateStudent(id, newStudentId)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Reservation not found with ID: " + id));
        }

        @Operation(summary = "Zaktualizuj miejsce rezerwacji")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Place updated successfully", content = @Content(schema = @Schema(implementation = Reservation.class))),
                        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(example = "{\"error\":\"Reservation not found with ID: 1\",\"status\":404}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid JSON\",\"message\":\"Required request body is missing\"}")))
        })
        @PutMapping("/{id}/place")
        public ResponseEntity<?> updateReservationPlace(@PathVariable Long id, @RequestBody String newPlace) {
                return reservationService.updateReservationPlace(id, newPlace)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Reservation not found with ID: " + id));
        }

        @Operation(summary = "Usuń rezerwację")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reservation deleted successfully", content = @Content(schema = @Schema(example = "{\"message\":\"Reservation deleted successfully\"}"))),
                        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema(example = "{\"error\":\"Reservation not found with ID: 1\",\"status\":404}")))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
                if (!reservationService.deleteReservation(id)) {
                        throw new ResourceNotFoundException("Reservation not found with ID: " + id);
                }
                return ResponseEntity.ok(Map.of("message", "Reservation deleted successfully"));
        }
}
