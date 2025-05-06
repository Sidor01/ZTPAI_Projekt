package org.example.skillwheel.service;

import lombok.extern.slf4j.Slf4j;
import org.example.skillwheel.auth.JWTUtil;
import org.example.skillwheel.exception.GlobalExceptionHandler;
import org.example.skillwheel.exception.LoginExceptions;
import org.example.skillwheel.model.Instructor;
import org.example.skillwheel.repository.InstructorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class AuthService implements UserDetailsService {
    private final JWTUtil jwtUtil;
    private final InstructorRepository instructorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(InstructorRepository instructorRepository, JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Instructor instructor = instructorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                instructor.getEmail(),
                instructor.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR")));
    }

    public String login(String email, String password) {
        log.info("Login attempt for email: {}", email); // Add this line

        Instructor instructor = instructorRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Instructor not found for email: {}", email); // Add this
                    return new GlobalExceptionHandler.UserNotFoundException("Invalid credentials");
                });

        log.debug("Found instructor: {}", instructor.getEmail()); // Add this

        if (!passwordEncoder.matches(password, instructor.getPassword())) {
            log.warn("Password mismatch for email: {}", email); // Add this
            throw new LoginExceptions.WrongPasswordException("Invalid credentials");
        }

        log.info("Login successful for email: {}", email); // Add this
        return jwtUtil.generateToken(email, "INSTRUCTOR");
    }

    public void register(String name, String surname, String email, String password, String nameOfSchool) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (instructorRepository.findByEmail(email).isPresent()) {
            throw new GlobalExceptionHandler.EmailTakenException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(password);
        Instructor instructor = new Instructor(name, surname, email, hashedPassword, nameOfSchool);

        instructorRepository.save(instructor);
    }

    public Instructor getInstructorFromToken(String token) {
        String email = jwtUtil.extractUsername(token);
        return instructorRepository.findByEmail(email).orElse(null);
    }
}
