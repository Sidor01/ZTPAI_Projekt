package org.example.skillwheel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private Map<Long, User> users = new HashMap<>();

    public UserController() {
        users.put(1L, new User(1L, "Jan","Kowalski" ,"jan@example.com","janek123"));
        users.put(2L, new User(2L, "Anna", "Nowak","anna@example.com","kotek"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = Optional.ofNullable(users.get(id));
        return user.map(value -> ResponseEntity.ok(value))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((User) Map.of("error", "User not found")));
    }

    static class User {
        public Long id;
        public String name;
        public String surname;
        public String email;
        public String password;

        public User(Long id, String name, String surname, String email, String password) {
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
