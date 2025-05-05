package org.example.skillwheel.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class RegisterRequest {
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotEmpty(message = "Surname cannot be empty")
    private String surname;
    @NotBlank
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Name of school cannot be empty")
    private String nameOfSchool;

    public String getName() {return name;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public String getSurname() {return surname;}
    public String getNameOfSchool() {return nameOfSchool;}

    public void setPassword(@NotBlank @NotEmpty(message = "Password cannot be empty") String password) {
        this.password = password;
    }
}
