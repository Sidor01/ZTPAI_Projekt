package org.example.skillwheel.controller;

import jakarta.validation.Valid;
import org.example.skillwheel.model.Instructor;
import org.example.skillwheel.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public ResponseEntity<?> addInstructor(@Valid @RequestBody Instructor instructor, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new LinkedHashMap<>();

            bindingResult.getFieldErrors().forEach(error -> {
                if (error.getRejectedValue() != null &&
                        error.getRejectedValue().toString().isEmpty() &&
                        error.getCode().equals("NotBlank")) {
                    errors.put(error.getField(), error.getDefaultMessage());
                } else if (!errors.containsKey(error.getField())) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
            });

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of(
                            "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            "errors", errors
                    ));
        }

        if (instructor == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Instructor data cannot be null"));
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
}