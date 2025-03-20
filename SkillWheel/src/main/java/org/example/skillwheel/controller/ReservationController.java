package org.example.skillwheel.controller;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationController {

    static class Reservation {
        public Long id;
        public Boolean isReserved;
        public Long studentID;
        public Long instructorID;
        public LocalDate reservationDate;
        public LocalTime reservationTime;
    }
}
