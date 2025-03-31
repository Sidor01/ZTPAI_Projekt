package org.example.skillwheel.repository;

import org.example.skillwheel.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStudentID(Long studentID);
    List<Reservation> findByInstructorID(Long instructorID);
}
