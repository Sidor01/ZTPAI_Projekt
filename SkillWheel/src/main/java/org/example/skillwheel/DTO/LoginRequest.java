package org.example.skillwheel.DTO;

import jakarta.validation.constraints.NotEmpty;

public class LoginRequest {
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public void setPassword(@NotEmpty(message = "Password cannot be empty") String password) {
        this.password = password;
    }
    public void setEmail(@NotEmpty(message = "Email cannot be empty") String email) { this.email = email; }
}