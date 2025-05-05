package org.example.skillwheel.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "admins")
public class Admin implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    // Konstruktory
    public Admin() {}

    public Admin(String firstName, String lastName, String email, String password, Long schoolId, String schoolName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.schoolId = schoolId;
        this.schoolName = schoolName;
    }

    // Metody z UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getUsername() {
        return email; // Używamy email jako nazwy użytkownika
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Konto nigdy nie wygasa
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Konto nigdy nie jest zablokowane
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Dane uwierzytelniające nigdy nie wygasają
    }

    @Override
    public boolean isEnabled() {
        return true; // Konto zawsze aktywne
    }

    // Standardowe gettery i settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", schoolId=" + schoolId +
                ", schoolName='" + schoolName + '\'' +
                '}';
    }
}