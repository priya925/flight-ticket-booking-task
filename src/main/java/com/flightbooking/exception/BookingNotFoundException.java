package com.flightbooking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String reference) {
        super("Booking not found: " + reference);
    }
}
