package org.example.skillwheel.service;

import org.example.skillwheel.model.Reservation;
import org.example.skillwheel.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByStudentId(Long studentId) {
        return reservationRepository.findByStudentID(studentId);
    }

    public List<Reservation> getReservationsByInstructorId(Long instructorId) {
        return reservationRepository.findByInstructorID(instructorId);
    }

    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> updateReservationTime(Long id, LocalTime newTime) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setReservationTime(newTime);
            return reservationRepository.save(reservation);
        });
    }

    public Optional<Reservation> updateReservationDate(Long id, LocalDate newDate) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setReservationDate(newDate);
            return reservationRepository.save(reservation);
        });
    }

    public Optional<Reservation> updateInstructor(Long id, Long newInstructorId) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setInstructorID(newInstructorId);
            return reservationRepository.save(reservation);
        });
    }

    public Optional<Reservation> updateStudent(Long id, Long newStudentId) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setStudentID(newStudentId);
            return reservationRepository.save(reservation);
        });
    }

    public Optional<Reservation> updateReservationPlace(Long id, String newPlace) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setReservationPlace(newPlace);
            return reservationRepository.save(reservation);
        });
    }

    public boolean deleteReservation(Long id) {
        return reservationRepository.findById(id).map(reservation -> {
            reservationRepository.delete(reservation);
            return true;
        }).orElse(false);
    }
}