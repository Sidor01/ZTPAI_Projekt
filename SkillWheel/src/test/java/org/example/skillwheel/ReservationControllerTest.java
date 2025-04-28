package org.example.skillwheel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.skillwheel.controller.ReservationController;
import org.example.skillwheel.model.Reservation;
import org.example.skillwheel.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
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
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    void getReservationById_ShouldReturnReservation() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.reservation.id", is(1)))
                .andExpect(jsonPath("$.reservation.studentID", is(100)))
                .andExpect(jsonPath("$.reservation.instructorID", is(200)));
    }

    @Test
    void getReservationById_ShouldReturn404WhenNotFound() throws Exception {
        when(reservationService.getReservationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Reservation not found")));
    }

    @Test
    void getAllReservations_ShouldReturnReservationsList() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getAllReservations()).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.reservations", hasSize(1)))
                .andExpect(jsonPath("$.reservations[0].id", is(1)));
    }

    @Test
    void getAllReservations_ShouldReturnEmptyList() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.reservations", empty()));
    }

    @Test
    void getReservationsByStudentId_ShouldReturnReservations() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getReservationsByStudentId(100L)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations/student/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.reservations", hasSize(1)))
                .andExpect(jsonPath("$.reservations[0].studentID", is(100)));
    }

    @Test
    void getReservationsByInstructorId_ShouldReturnReservations() throws Exception {
        Reservation reservation = createTestReservation(1L);

        when(reservationService.getReservationsByInstructorId(200L)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations/instructor/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.reservations", hasSize(1)))
                .andExpect(jsonPath("$.reservations[0].instructorID", is(200)));
    }

    @Test
    void addReservation_ShouldCreateReservation() throws Exception {
        Reservation newReservation = createTestReservation(null);
        Reservation savedReservation = createTestReservation(1L);

        // Upewnij się, że testowa rezerwacja ma wszystkie wymagane pola
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
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.reservation.id", is(1)));
    }


    @Test
    void addReservation_ShouldReturn400WhenIdExists() throws Exception {
        Reservation existingReservation = createTestReservation(1L);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingReservation)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("New reservation should not have an ID")));
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
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.reservation.reservationTime", is("15:30:00")));
    }

    @Test
    void deleteReservation_ShouldDeleteReservation() throws Exception {
        when(reservationService.deleteReservation(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
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
        Reservation invalidReservation = new Reservation(); // brak wymaganych pól

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReservation)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)));
    }

    @Test
    void addReservation_ShouldReturn422WhenInvalidDate() throws Exception {
        Reservation invalidReservation = createTestReservation(null);
        invalidReservation.setReservationDate(LocalDate.of(2020, 1, 1)); // data w przeszłości

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReservation)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)));
    }
}