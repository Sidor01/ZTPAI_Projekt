package org.example.skillwheel.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_reserved", nullable = false)
    private Boolean isReserved;

    @Column(name = "student_id", nullable = false)
    private Long studentID;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorID;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;
    @Column(name = "reservation_place", nullable = false)
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

    public String getReservationPlace() {
        return reservationPlace;
    }

    public void setReservationPlace(String reservationPlace) {
        this.reservationPlace = reservationPlace;
    }

    // Getters and Setters
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