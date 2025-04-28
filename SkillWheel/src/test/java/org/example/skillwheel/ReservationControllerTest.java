package org.example.skillwheel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.skillwheel.controller.ReservationController;
import org.example.skillwheel.exception.GlobalExceptionHandler;
import org.example.skillwheel.model.Reservation;
import org.example.skillwheel.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getReservationById_ShouldReturnReservation() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.studentID", is(100)))
                .andExpect(jsonPath("$.instructorID", is(200)));
    }

    @Test
    void getReservationById_ShouldReturn404WhenNotFound() throws Exception {
        when(reservationService.getReservationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReservations_ShouldReturnReservationsList() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getAllReservations()).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getAllReservations_ShouldReturnEmptyList() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void getReservationsByStudentId_ShouldReturnReservations() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getReservationsByStudentId(100L)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations/student/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentID", is(100)));
    }

    @Test
    void getReservationsByInstructorId_ShouldReturnReservations() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getReservationsByInstructorId(200L)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations/instructor/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].instructorID", is(200)));
    }

    @Test
    void addReservation_ShouldCreateReservation() throws Exception {
        Reservation newReservation = createTestReservation(null);
        Reservation savedReservation = createTestReservation(1L);

        newReservation.setStudentID(100L);
        newReservation.setInstructorID(200L);
        newReservation.setReservationDate(LocalDate.now().plusDays(1));
        newReservation.setReservationTime(LocalTime.now());
        newReservation.setReservationPlace("Room 101");

        when(reservationService.addReservation(any(Reservation.class))).thenReturn(savedReservation);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReservation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateReservationTime_ShouldUpdateTime() throws Exception {
        Reservation updatedReservation = createTestReservation(1L);
        updatedReservation.setReservationTime(LocalTime.of(15, 30));

        when(reservationService.updateReservationTime(eq(1L), any(LocalTime.class)))
                .thenReturn(Optional.of(updatedReservation));

        mockMvc.perform(put("/api/reservations/1/time")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"15:30\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationTime", is("15:30:00")));
    }

    @Test
    void deleteReservation_ShouldDeleteReservation() throws Exception {
        when(reservationService.deleteReservation(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Reservation deleted successfully")));
    }

    private Reservation createTestReservation(Long id) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setIsReserved(true);
        reservation.setStudentID(100L);
        reservation.setInstructorID(200L);
        reservation.setReservationDate(LocalDate.of(2023, 6, 15));
        reservation.setReservationTime(LocalTime.of(14, 0));
        reservation.setReservationPlace("Room 101");
        return reservation;
    }

    @Test
    void addReservation_ShouldReturn422WhenMissingRequiredFields() throws Exception {
        Reservation invalidReservation = new Reservation();
        invalidReservation.setIsReserved(null);
        invalidReservation.setStudentID(null);
        invalidReservation.setInstructorID(null);
        invalidReservation.setReservationDate(null);
        invalidReservation.setReservationTime(null);
        invalidReservation.setReservationPlace(null);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReservation)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.isReserved", is("Reservation status is required")))
                .andExpect(jsonPath("$.studentID", is("Student ID is required")))
                .andExpect(jsonPath("$.instructorID", is("Instructor ID is required")))
                .andExpect(jsonPath("$.reservationDate", is("Reservation date is required")))
                .andExpect(jsonPath("$.reservationTime", is("Reservation time is required")))
                .andExpect(jsonPath("$.reservationPlace", is("Reservation place is required")));
    }

    @Test
    void addReservation_ShouldReturn422WhenInvalidDate() throws Exception {
        Reservation invalidReservation = createTestReservation(null);
        invalidReservation.setReservationDate(LocalDate.of(2020, 1, 1)); // past date

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReservation)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.reservationDate", is("Reservation date must be in the present or future")));
    }
}
