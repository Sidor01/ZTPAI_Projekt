package org.example.skillwheel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;



@Entity
@Table(name = "instructors")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between {min} and {max} characters")
    private String name;

    @NotBlank(message = "Surname is mandatory")
    @Size(min = 2, max = 50, message = "Surname must be between {min} and {max} characters")
    private String surname;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least {min} characters long")
    private String password;

    @Size(max = 100, message = "School name must be less than {max} characters")
    private String nameOfSchool;

    public Instructor() {
    }

    public Instructor(String name, String surname, String email, String password, String nameOfSchool) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.nameOfSchool = nameOfSchool;
    }

    public String getNameOfSchool() {
        return nameOfSchool;
    }

    public void setNameOfSchool(String nameOfSchool) {
        this.nameOfSchool = nameOfSchool;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
