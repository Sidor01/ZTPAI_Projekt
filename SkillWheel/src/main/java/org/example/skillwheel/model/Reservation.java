package org.example.skillwheel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Schema(description = "Encja reprezentująca rezerwację jazdy w systemie")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unikalny identyfikator rezerwacji", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "is_reserved", nullable = false)
    @Schema(description = "Czy termin został zarezerwowany", example = "true", required = true)
    @NotNull(message = "Reservation status is required")
    private Boolean isReserved;

    @Column(name = "student_id", nullable = false)
    @Schema(description = "ID studenta", example = "10", required = true)
    @NotNull(message = "Student ID is required")
    private Long studentID;

    @Column(name = "instructor_id", nullable = false)
    @Schema(description = "ID instruktora", example = "5", required = true)
    @NotNull(message = "Instructor ID is required")
    private Long instructorID;

    @Column(name = "reservation_date", nullable = false)
    @Schema(description = "Data rezerwacji", example = "2025-05-10", required = true)
    @NotNull(message = "Reservation date is required")
    @FutureOrPresent(message = "Reservation date must be in the present or future")
    private LocalDate reservationDate;

    @Column(name = "reservation_time", nullable = false)
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "Godzina rezerwacji", example = "14:30:00", required = true)
    @NotNull(message = "Reservation time is required")
    private LocalTime reservationTime;

    @Column(name = "reservation_place", nullable = false)
    @Schema(description = "Miejsce spotkania", example = "Plac Manewrowy", required = true)
    @NotBlank(message = "Reservation place is required")
    private String reservationPlace;

    public Reservation() {
    }

    public Reservation(Long id, Boolean isReserved, Long studentID, Long instructorID, LocalDate reservationDate, LocalTime reservationTime, String reservationPlace) {
        this.id = id;
        this.isReserved = isReserved;
        this.studentID = studentID;
        this.instructorID = instructorID;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.reservationPlace = reservationPlace;
    }

    // Getters and Setters

    public String getReservationPlace() {
        return reservationPlace;
    }

    public void setReservationPlace(String reservationPlace) {
        this.reservationPlace = reservationPlace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsReserved() {
        return isReserved;
    }

    public void setIsReserved(Boolean isReserved) {
        this.isReserved = isReserved;
    }

    public Long getStudentID() {
        return studentID;
    }

    public void setStudentID(Long studentID) {
        this.studentID = studentID;
    }

    public Long getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(Long instructorID) {
        this.instructorID = instructorID;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }
}
