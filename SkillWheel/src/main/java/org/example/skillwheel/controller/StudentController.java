package org.example.skillwheel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.skillwheel.model.Student;
import org.example.skillwheel.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Pobierz studenta po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student znaleziony",
                    content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "404", description = "Student nie znaleziony",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":404,\"error\":\"Student not found\"}"))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowy format ID",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":400,\"error\":\"Invalid parameter\",\"message\":\"Failed to convert value of type 'String' to required type 'Long'\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(
            @Parameter(description = "ID studenta", required = true)
            @PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(student -> ResponseEntity.ok(Map.of(
                        "status", HttpStatus.OK.value(),
                        "student", student
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", HttpStatus.NOT_FOUND.value(),
                                "error", "Student not found"
                        )));
    }

    @Operation(summary = "Pobierz wszystkich studentów")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista studentów",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":200,\"students\":[{\"id\":1,\"name\":\"Jan Kowalski\"},{\"id\":2,\"name\":\"Anna Nowak\"}]}")))
    })
    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "students", students
        ));
    }

    @Operation(summary = "Dodaj nowego studenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student dodany",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":201,\"student\":{\"id\":1,\"name\":\"Jan Kowalski\",\"email\":\"jan@example.com\"}}"))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowy format JSON lub brak danych",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":400,\"error\":\"Request body cannot be empty\"}"))),
            @ApiResponse(responseCode = "400", description = "Błąd parsowania JSON",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":400,\"error\":\"Invalid JSON format\"}"))),
            @ApiResponse(responseCode = "422", description = "Błąd walidacji danych",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":422,\"errors\":{\"name\":\"Name cannot be empty\",\"email\":\"Invalid email format\"}}"))),
            @ApiResponse(responseCode = "500", description = "Wewnętrzny błąd serwera",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":500,\"error\":\"An unexpected error occurred\"}")))
    })
    @PostMapping
    public ResponseEntity<?> addStudent(
            @Parameter(description = "Dane studenta w formacie JSON", required = true)
            @RequestBody String studentJson) {

        if (studentJson == null || studentJson.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Request body cannot be empty"));
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Student student = objectMapper.readValue(studentJson, Student.class);

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Student>> violations = validator.validate(student);

            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<Student> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of(
                                "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "errors", errors
                        ));
            }

            Student savedStudent = studentService.addStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("status", HttpStatus.CREATED.value(),
                            "student", savedStudent));
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

    @Operation(summary = "Aktualizuj dane studenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student zaktualizowany",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":200,\"student\":{\"id\":1,\"name\":\"Jan Kowalski\",\"email\":\"jan@example.com\"}}"))),
            @ApiResponse(responseCode = "404", description = "Student nie znaleziony",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":404,\"error\":\"Student not found\"}"))),
            @ApiResponse(responseCode = "422", description = "Błąd walidacji danych",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":422,\"errors\":{\"name\":\"Name cannot be empty\",\"email\":\"Invalid email format\"}}"))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowy format danych",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":400,\"error\":\"Invalid JSON format\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @Parameter(description = "ID studenta", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nowe dane studenta", required = true,
                    content = @Content(schema = @Schema(implementation = Student.class)))
            @Valid @RequestBody Student updatedStudent,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        return studentService.updateStudent(id, updatedStudent)
                .map(student -> ResponseEntity.ok(Map.of(
                        "status", HttpStatus.OK.value(),
                        "student", student
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", HttpStatus.NOT_FOUND.value(),
                                "error", "Student not found"
                        )));
    }

    @Operation(summary = "Usuń studenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student usunięty",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":200,\"message\":\"Student deleted successfully\"}"))),
            @ApiResponse(responseCode = "404", description = "Student nie znaleziony",
                    content = @Content(schema = @Schema(
                            example = "{\"status\":404,\"error\":\"Student not found\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(
            @Parameter(description = "ID studenta", required = true)
            @PathVariable Long id) {
        boolean isDeleted = studentService.deleteStudent(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Student not found"
                    ));
        }
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Student deleted successfully"
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
