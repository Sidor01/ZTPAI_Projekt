package org.example.skillwheel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private Map<Long, Instructor> instructors = new HashMap<>();

    public InstructorController() {
        instructors.put(1L, new Instructor(1L, "Lech", "Kat", "lechkat123@gmail.com", "leszek"));
        instructors.put(2L, new Instructor(2L, "Katarzyna", "Smutek", "kasieczka@gmail.com", "smutnomi"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInstructorById(@PathVariable Long id) {
        if (!instructors.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Instructor not found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "instructor", instructors.get(id)));
    }

    @GetMapping
    public ResponseEntity<?> getAllInstructors() {
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "instructors", instructors.values()));
    }

    @PostMapping
    public ResponseEntity<?> addInstructor(@RequestBody Instructor instructor) {
        if (instructors.containsKey(instructor.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "error", "Instructor already exists"));
        }
        instructors.put(instructor.getId(), instructor);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", HttpStatus.CREATED.value(), "instructor", instructor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInstructor(@PathVariable Long id, @RequestBody Instructor updatedInstructor) {
        if (!instructors.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Instructor not found"));
        }
        instructors.put(id, updatedInstructor);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "instructor", updatedInstructor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInstructor(@PathVariable Long id) {
        if (!instructors.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "error", "Instructor not found"));
        }
        instructors.remove(id);
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Instructor deleted successfully"));
    }

    static class Instructor {
        public Long id;
        public String name;
        public String surname;
        public String email;
        public String password;

        public Instructor(Long id, String name, String surname, String email, String password) {
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