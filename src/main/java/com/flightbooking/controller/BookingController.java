package com.flightbooking.controller;

import com.flightbooking.dto.BookingResponse;
import com.flightbooking.dto.CreateBookingRequest;
import com.flightbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Book seats on a flight for one or more passengers.
     * Returns 409 Conflict if the flight is full (prevents overbooking).
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve a booking by its reference code.
     */
    @GetMapping("/{bookingReference}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String bookingReference) {
        return ResponseEntity.ok(bookingService.getBooking(bookingReference));
    }

    /**
     * Cancel a booking and release the seats back to the flight.
     */
    @DeleteMapping("/{bookingReference}")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable String bookingReference) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingReference));
    }
}
