package com.flightbooking.model;

import java.time.LocalDateTime;
import java.util.List;

public class Booking {

    private final String bookingReference;
    private final String flightNumber;
    private final List<Passenger> passengers;
    private final LocalDateTime bookedAt;
    private BookingStatus status;

    public Booking(String bookingReference, String flightNumber,
                   List<Passenger> passengers, LocalDateTime bookedAt) {
        this.bookingReference = bookingReference;
        this.flightNumber = flightNumber;
        this.passengers = List.copyOf(passengers);
        this.bookedAt = bookedAt;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getBookingReference() { return bookingReference; }
    public String getFlightNumber() { return flightNumber; }
    public List<Passenger> getPassengers() { return passengers; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public int getSeatCount() { return passengers.size(); }
}
