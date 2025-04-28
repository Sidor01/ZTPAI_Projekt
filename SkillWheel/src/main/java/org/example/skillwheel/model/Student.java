package org.example.skillwheel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "students")
@Schema(description = "Encja reprezentująca studenta w systemie")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unikalny identyfikator studenta", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(description = "Imię studenta", example = "Jan", required = true)
    private String name;

    @NotBlank(message = "Surname is mandatory")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    @Schema(description = "Nazwisko studenta", example = "Kowalski", required = true)
    private String surname;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Schema(description = "Adres e-mail studenta", example = "jan.kowalski@example.com", required = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "Hasło studenta (minimum 8 znaków)", example = "password123", required = true)
    private String password;

    @Schema(description = "Nazwa szkoły, do której uczęszcza student", example = "Szkoła Jazdy AutoMaster")
    private String nameOfSchool;

    public Student() {
    }

    public Student(String name, String surname, String email, String password, String nameOfSchool) {
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
