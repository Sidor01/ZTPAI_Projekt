package org.example.skillwheel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.skillwheel.model.Student;
import org.example.skillwheel.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Pobierz studenta po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student znaleziony"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @Parameter(description = "ID studenta do pobrania", required = true)
            @PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Pobierz wszystkich studentów")
    @ApiResponse(responseCode = "200", description = "Lista studentów")
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @Operation(summary = "Dodaj nowego studenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student utworzony"),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe")
    })
    @PostMapping
    public ResponseEntity<Student> addStudent(
            @Parameter(description = "Dane nowego studenta", required = true)
            @Valid @RequestBody Student student) {
        Student savedStudent = studentService.addStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @Operation(summary = "Aktualizuj istniejącego studenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @Parameter(description = "ID studenta do aktualizacji", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nowe dane studenta", required = true)
            @RequestBody Student updatedStudent) {
        Optional<Student> student = studentService.updateStudent(id, updatedStudent);
        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Usuń studenta po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Student usunięty"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "ID studenta do usunięcia", required = true)
            @PathVariable Long id) {
        boolean deleted = studentService.deleteStudent(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
