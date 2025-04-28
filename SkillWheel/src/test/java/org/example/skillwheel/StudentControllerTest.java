package org.example.skillwheel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.skillwheel.controller.StudentController;
import org.example.skillwheel.exception.GlobalExceptionHandler;
import org.example.skillwheel.model.Student;
import org.example.skillwheel.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getStudentById_ShouldReturnStudent() throws Exception {
        Student student = new Student("Alice", "Johnson", "alice@example.com", "password123", "Springfield High");
        student.setId(1L);

        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.surname", is("Johnson")))
                .andExpect(jsonPath("$.email", is("alice@example.com")))
                .andExpect(jsonPath("$.nameOfSchool", is("Springfield High")));
    }

    @Test
    void getStudentById_ShouldReturn404WhenNotFound() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllStudents_ShouldReturnStudentsList() throws Exception {
        Student student = new Student("Bob", "Smith", "bob@example.com", "password456", "Springfield High");
        student.setId(2L);

        when(studentService.getAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("Bob")));
    }

    @Test
    void getAllStudents_ShouldReturnEmptyList() throws Exception {
        when(studentService.getAllStudents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void addStudent_ShouldCreateStudent() throws Exception {
        Student newStudent = new Student("Charlie", "Brown", "charlie@example.com", "password789", "Springfield High");
        Student savedStudent = new Student("Charlie", "Brown", "charlie@example.com", "password789", "Springfield High");
        savedStudent.setId(3L);

        when(studentService.addStudent(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Charlie")))
                .andExpect(jsonPath("$.nameOfSchool", is("Springfield High")));
    }

    @Test
    void updateStudent_ShouldUpdateExistingStudent() throws Exception {
        Student updatedStudent = new Student("Alice", "Johnson-Smith", "alice.smith@example.com", "newpassword", "Updated School");
        updatedStudent.setId(1L);

        when(studentService.updateStudent(eq(1L), any(Student.class)))
                .thenReturn(Optional.of(updatedStudent));

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname", is("Johnson-Smith")))
                .andExpect(jsonPath("$.nameOfSchool", is("Updated School")));
    }

    @Test
    void updateStudent_ShouldReturn404WhenNotFound() throws Exception {
        Student nonExistingStudent = new Student("Non", "Existing", "none@example.com", "password", "No School");
        nonExistingStudent.setId(99L);

        when(studentService.updateStudent(eq(99L), any(Student.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/students/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingStudent)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_ShouldDeleteStudent() throws Exception {
        when(studentService.deleteStudent(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteStudent_ShouldReturn404WhenNotFound() throws Exception {
        when(studentService.deleteStudent(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addStudent_ShouldReturn422WhenValidationFails() throws Exception {
        Student invalidStudent = new Student();
        invalidStudent.setName("");
        invalidStudent.setSurname("");
        invalidStudent.setEmail("invalid-email");
        invalidStudent.setPassword("");
        invalidStudent.setNameOfSchool("");

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.name", anyOf(
                        is("Name is mandatory"),
                        is("Name must be between 2 and 50 characters")
                )))
                .andExpect(jsonPath("$.surname", anyOf(
                        is("Surname is mandatory"),
                        is("Surname must be between 2 and 50 characters")
                )))
                .andExpect(jsonPath("$.email", is("Email should be valid")))
                .andExpect(jsonPath("$.password", anyOf(
                        is("Password is mandatory"),
                        is("Password must be at least 8 characters long")
                )));
    }

    @Test
    void addStudent_ShouldReturn422ForShortValues() throws Exception {
        Student invalidStudent = new Student("A", "B", "invalid", "short", "X");

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.name", is("Name must be between 2 and 50 characters")))
                .andExpect(jsonPath("$.surname", is("Surname must be between 2 and 50 characters")))
                .andExpect(jsonPath("$.email", is("Email should be valid")))
                .andExpect(jsonPath("$.password", is("Password must be at least 8 characters long")));
    }
}