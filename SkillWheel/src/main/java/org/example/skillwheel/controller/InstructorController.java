package org.example.skillwheel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.*;
import org.example.skillwheel.model.Instructor;
import org.example.skillwheel.service.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    @Autowired
    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @Operation(summary = "Pobierz instruktora po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instruktor znaleziony",
                    content = @Content(schema = @Schema(implementation = Instructor.class))),
            @ApiResponse(responseCode = "404", description = "Instruktor nie znaleziony",
                    content = @Content(schema = @Schema(example = "{\"status\":404,\"error\":\"Instructor not found\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getInstructorById(
            @Parameter(description = "ID instruktora", required = true)
            @PathVariable Long id) {
        Optional<Instructor> instructor = instructorService.getInstructorById(id);
        if (instructor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Instructor not found"
                    ));
        }
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "instructor", instructor.get()
        ));
    }

    @Operation(summary = "Pobierz wszystkich instruktorów")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista instruktorów",
                    content = @Content(schema = @Schema(example = "{\"status\":200,\"instructors\":[]}")))
    })
    @GetMapping
    public ResponseEntity<?> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "instructors", instructors
        ));
    }

    @Operation(summary = "Dodaj nowego instruktora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Instruktor dodany",
                    content = @Content(schema = @Schema(implementation = Instructor.class))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowy format JSON lub pusty request body",
                    content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Invalid JSON format\"}"))),
            @ApiResponse(responseCode = "409", description = "Instruktor z tym emailem już istnieje",
                    content = @Content(schema = @Schema(example = "{\"status\":409,\"error\":\"Instructor with this email already exists\"}"))),
            @ApiResponse(responseCode = "422", description = "Błąd walidacji danych",
                    content = @Content(schema = @Schema(example = "{\"status\":422,\"errors\":{\"email\":\"must be a valid email\"}}")))
    })
    @PostMapping
    public ResponseEntity<?> addInstructor(
            @Parameter(description = "Dane instruktora w formacie JSON", required = true)
            @RequestBody String instructorJson) {
        if (instructorJson == null || instructorJson.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Request body cannot be empty"));
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Instructor instructor = objectMapper.readValue(instructorJson, Instructor.class);

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Instructor>> violations = validator.validate(instructor);

            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<Instructor> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of(
                                "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "errors", errors
                        ));
            }

            if (instructorService.existsByEmail(instructor.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        Map.of("status", HttpStatus.CONFLICT.value(),
                                "error", "Instructor with this email already exists"));
            }

            Instructor savedInstructor = instructorService.addInstructor(instructor);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("status", HttpStatus.CREATED.value(),
                            "instructor", savedInstructor));

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Invalid JSON format"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Aktualizuj dane instruktora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instruktor zaktualizowany",
                    content = @Content(schema = @Schema(implementation = Instructor.class))),
            @ApiResponse(responseCode = "404", description = "Instruktor nie znaleziony",
                    content = @Content(schema = @Schema(example = "{\"status\":404,\"error\":\"Instructor not found\"}"))),
            @ApiResponse(responseCode = "422", description = "Błąd walidacji danych",
                    content = @Content(schema = @Schema(example = "{\"status\":422,\"errors\":{\"firstName\":\"must not be blank\"}}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInstructor(
            @Parameter(description = "ID instruktora", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nowe dane instruktora", required = true,
                    content = @Content(schema = @Schema(implementation = Instructor.class)))
            @Valid @RequestBody Instructor updatedInstructor,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        Optional<Instructor> instructor = instructorService.updateInstructor(id, updatedInstructor);
        if (instructor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Instructor not found"
                    ));
        }
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "instructor", instructor.get()
        ));
    }

    @Operation(summary = "Usuń instruktora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instruktor usunięty",
                    content = @Content(schema = @Schema(example = "{\"status\":200,\"message\":\"Instructor deleted successfully\"}"))),
            @ApiResponse(responseCode = "404", description = "Instruktor nie znaleziony",
                    content = @Content(schema = @Schema(example = "{\"status\":404,\"error\":\"Instructor not found\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInstructor(
            @Parameter(description = "ID instruktora", required = true)
            @PathVariable Long id) {
        boolean isDeleted = instructorService.deleteInstructor(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Instructor not found"
                    ));
        }
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Instructor deleted successfully"
        ));
    }

    private ResponseEntity<Map<String, Object>> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "Validation error"
                ));

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of(
                        "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        "errors", errors
                ));
    }
}