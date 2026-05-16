package com.flightbooking.service;

import com.flightbooking.dto.Dtos.*;
import com.flightbooking.exception.BookingNotFoundException;
import com.flightbooking.exception.NoAvailableSeatsException;
import com.flightbooking.model.Booking;
import com.flightbooking.model.BookingStatus;
import com.flightbooking.model.Flight;
import com.flightbooking.model.Passenger;
import com.flightbooking.repository.InMemoryBookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final InMemoryBookingRepository bookingRepository;
    private final FlightService flightService;

    public BookingService(InMemoryBookingRepository bookingRepository,
                          FlightService flightService) {
        this.bookingRepository = bookingRepository;
        this.flightService = flightService;
    }

    public BookingResponse createBooking(CreateBookingRequest request) {
        Flight flight = flightService.findOrThrow(request.flightNumber());

        int seatCount = request.passengers().size();

        // Atomic seat reservation — prevents overbooking
        boolean reserved = flight.reserveSeats(seatCount);
        if (!reserved) {
            throw new NoAvailableSeatsException(
                    request.flightNumber(), seatCount, flight.getAvailableSeats());
        }

        List<Passenger> passengers = request.passengers().stream()
                .map(p -> new Passenger(p.firstName(), p.lastName(), p.passportNumber()))
                .toList();

        String reference = generateReference();
        Booking booking = new Booking(reference, request.flightNumber(),
                passengers, LocalDateTime.now());
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    public BookingResponse cancelBooking(String bookingReference) {
        Booking booking = bookingRepository.findByReference(bookingReference)
                .orElseThrow(() -> new BookingNotFoundException(bookingReference));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking " + bookingReference + " is already cancelled");
        }

        // Release seats back to the flight
        Flight flight = flightService.findOrThrow(booking.getFlightNumber());
        flight.releaseSeats(booking.getSeatCount());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    public BookingResponse getBooking(String bookingReference) {
        Booking booking = bookingRepository.findByReference(bookingReference)
                .orElseThrow(() -> new BookingNotFoundException(bookingReference));
        return toResponse(booking);
    }

    private String generateReference() {
        // Short 8-char uppercase alphanumeric reference
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    private BookingResponse toResponse(Booking b) {
        List<PassengerResponse> passengers = b.getPassengers().stream()
                .map(p -> new PassengerResponse(p.getFirstName(), p.getLastName(), p.getPassportNumber()))
                .toList();
        return new BookingResponse(
                b.getBookingReference(),
                b.getFlightNumber(),
                b.getStatus().name(),
                passengers,
                b.getSeatCount(),
                b.getBookedAt()
        );
    }
}
