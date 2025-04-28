package org.example.skillwheel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.skillwheel.controller.InstructorController;
import org.example.skillwheel.model.Instructor;
import org.example.skillwheel.service.InstructorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InstructorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InstructorService instructorService;

    @InjectMocks
    private InstructorController instructorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(instructorController).build();
    }

    @Test
    void getInstructorById_ShouldReturnInstructor() throws Exception {
        Instructor instructor = new Instructor("John", "Doe", "john@example.com", "password123", "Springfield High");
        instructor.setId(1L);

        when(instructorService.getInstructorById(1L)).thenReturn(Optional.of(instructor));

        mockMvc.perform(get("/api/instructors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.instructor.id", is(1)))
                .andExpect(jsonPath("$.instructor.name", is("John")))
                .andExpect(jsonPath("$.instructor.surname", is("Doe")))
                .andExpect(jsonPath("$.instructor.email", is("john@example.com")))
                .andExpect(jsonPath("$.instructor.nameOfSchool", is("Springfield High")));
    }

    @Test
    void getInstructorById_ShouldReturn404WhenNotFound() throws Exception {
        when(instructorService.getInstructorById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/instructors/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Instructor not found")));
    }

    @Test
    void getAllInstructors_ShouldReturnInstructorsList() throws Exception {
        Instructor instructor = new Instructor("John", "Doe", "john@example.com", "password123", "Springfield High");
        instructor.setId(1L);

        when(instructorService.getAllInstructors()).thenReturn(Collections.singletonList(instructor));

        mockMvc.perform(get("/api/instructors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.instructors", hasSize(1)))
                .andExpect(jsonPath("$.instructors[0].id", is(1)))
                .andExpect(jsonPath("$.instructors[0].name", is("John")));
    }

    @Test
    void getAllInstructors_ShouldReturnEmptyList() throws Exception {
        when(instructorService.getAllInstructors()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/instructors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.instructors", empty()));
    }

    @Test
    void addInstructor_ShouldCreateInstructor() throws Exception {
        Instructor newInstructor = new Instructor("Jane", "Doe", "jane@example.com", "password456", "Springfield High");
        Instructor savedInstructor = new Instructor("Jane", "Doe", "jane@example.com", "password456", "Springfield High");
        savedInstructor.setId(2L);

        when(instructorService.getInstructorById(2L)).thenReturn(Optional.empty());
        when(instructorService.addInstructor(any(Instructor.class))).thenReturn(savedInstructor);

        mockMvc.perform(post("/api/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newInstructor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.instructor.id", is(2)))
                .andExpect(jsonPath("$.instructor.name", is("Jane")))
                .andExpect(jsonPath("$.instructor.nameOfSchool", is("Springfield High")));
    }

    @Test
    void addInstructor_ShouldReturn409WhenInstructorExists() throws Exception {
        Instructor existingInstructor = new Instructor("John", "Doe", "john@example.com", "password123", "School");

        when(instructorService.existsByEmail("john@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingInstructor)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Instructor with this email already exists")));
    }

    @Test
    void updateInstructor_ShouldUpdateExistingInstructor() throws Exception {
        Instructor updatedInstructor = new Instructor("John", "Smith", "john.smith@example.com", "newpassword", "Updated School");
        updatedInstructor.setId(1L);

        when(instructorService.updateInstructor(eq(1L), any(Instructor.class)))
                .thenReturn(Optional.of(updatedInstructor));

        mockMvc.perform(put("/api/instructors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInstructor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.instructor.surname", is("Smith")))
                .andExpect(jsonPath("$.instructor.nameOfSchool", is("Updated School")));
    }

    @Test
    void updateInstructor_ShouldReturn404WhenNotFound() throws Exception {
        Instructor nonExistingInstructor = new Instructor("Non", "Existing", "none@example.com", "password", "No School");
        nonExistingInstructor.setId(99L);

        when(instructorService.updateInstructor(eq(99L), any(Instructor.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/instructors/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingInstructor)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Instructor not found")));
    }

    @Test
    void deleteInstructor_ShouldDeleteInstructor() throws Exception {
        when(instructorService.deleteInstructor(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/instructors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Instructor deleted successfully")));
    }

    @Test
    void deleteInstructor_ShouldReturn404WhenNotFound() throws Exception {
        when(instructorService.deleteInstructor(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/instructors/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Instructor not found")));
    }

    @Test
    void addInstructor_ShouldReturn422WhenValidationFails() throws Exception {
        Instructor invalidInstructor = new Instructor();
        invalidInstructor.setName(null);  // Explicit null to trigger NotBlank
        invalidInstructor.setSurname(null);  // Explicit null to trigger NotBlank
        invalidInstructor.setEmail("invalid-email");
        invalidInstructor.setPassword(null);  // Explicit null to trigger NotBlank
        invalidInstructor.setNameOfSchool("");

        mockMvc.perform(post("/api/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInstructor)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.errors.name", is("Name is mandatory")))
                .andExpect(jsonPath("$.errors.surname", is("Surname is mandatory")))
                .andExpect(jsonPath("$.errors.email", is("Email should be valid")))
                .andExpect(jsonPath("$.errors.password", is("Password is mandatory")));
    }

    @Test
    void addInstructor_ShouldReturn422ForShortValues() throws Exception {
        Instructor invalidInstructor = new Instructor("A", "B", "invalid", "short", "X");

        mockMvc.perform(post("/api/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInstructor)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.errors.name", is("Name must be between 2 and 50 characters")))
                .andExpect(jsonPath("$.errors.surname", is("Surname must be between 2 and 50 characters")))
                .andExpect(jsonPath("$.errors.email", is("Email should be valid")))
                .andExpect(jsonPath("$.errors.password", is("Password must be at least 8 characters long")));
    }

    @Test
    void getInstructorById_ShouldReturn400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/instructors/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", containsString("Invalid parameter type")));
    }

    @Test
    void addInstructor_ShouldReturn400WhenInvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid JSON format")));
    }

    @Test
    void addInstructor_ShouldReturn400WhenDateIsInvalid() throws Exception {
        String jsonWithInvalidDate = """
    {
        "name": "John",
        "surname": "Doe",
        "email": "john@example.com",
        "password": "password123",
        "nameOfSchool": "School",
        "birthDate": "2023-13-45"
    }
    """;

        mockMvc.perform(post("/api/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithInvalidDate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", containsString("Invalid JSON format")));
    }

    @Test
    void addInstructor_ShouldReturn400WhenNumericFieldIsString() throws Exception {
        String jsonWithInvalidType = """
    {
        "name": "John",
        "surname": "Doe",
        "email": "john@example.com",
        "password": "password123",
        "nameOfSchool": "School",
        "age": "not-a-number"
    }
    """;

        mockMvc.perform(post("/api/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithInvalidType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid JSON format")));
    }
}