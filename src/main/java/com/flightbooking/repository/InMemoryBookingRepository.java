package com.flightbooking.repository;

import com.flightbooking.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository {
    private final ConcurrentHashMap<String, Booking> bookings = new ConcurrentHashMap<>();

    public Booking save(Booking booking) {
        bookings.put(booking.getBookingReference(), booking);
        return booking;
    }

    public Optional<Booking> findByReference(String reference) {
        return Optional.ofNullable(bookings.get(reference));
    }

    public boolean existsByReference(String reference) {
        return bookings.containsKey(reference);
    }
}
