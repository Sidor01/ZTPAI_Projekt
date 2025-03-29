package org.example.skillwheel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private Map<Long, Student> students = new HashMap<>();

    public StudentController() {
        students.put(1L, new Student(1L, "Jan", "Kowalski", "jan@example.com", "janek123"));
        students.put(2L, new Student(2L, "Anna", "Nowak", "anna@example.com", "kotek"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        if (!students.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Student not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "student", students.get(id)));
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "students", students.values()));
    }

    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        if (students.containsKey(student.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "error", "Student already exists"));
        }
        students.put(student.getId(), student);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", HttpStatus.CREATED.value(), "student", student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        if (!students.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Student not found"));
        }
        students.put(id, updatedStudent);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "student", updatedStudent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        if (!students.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Student not found"));
        }
        students.remove(id);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Student deleted successfully"));
    }

    static class Student {
        public Long id;
        public String name;
        public String surname;
        public String email;
        public String password;

        public Student(Long id, String name, String surname, String email, String password) {
            this.id = id;
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.password = password;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getSurname() { return surname; }
        public String getPassword() { return password; }
    }
}