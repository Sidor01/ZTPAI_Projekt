package org.example.skillwheel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.*;
import org.example.skillwheel.model.Instructor;
import org.example.skillwheel.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getInstructorById(@PathVariable Long id) {
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

    @GetMapping
    public ResponseEntity<?> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "instructors", instructors
        ));
    }


    @PostMapping
    public ResponseEntity<?> addInstructor(@RequestBody String instructorJson) {
        // First check if the JSON string is empty
        if (instructorJson == null || instructorJson.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Request body cannot be empty"));
        }

        try {
            // Parse JSON manually to catch parsing errors early
            ObjectMapper objectMapper = new ObjectMapper();
            Instructor instructor = objectMapper.readValue(instructorJson, Instructor.class);

            // Now validate the instructor object
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInstructor(@PathVariable Long id,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInstructor(@PathVariable Long id) {
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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return buildValidationErrorResponse(ex.getBindingResult());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Invalid JSON format")
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Invalid parameter type: " + ex.getMessage())
        );
    }
}