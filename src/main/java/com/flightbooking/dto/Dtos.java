package com.flightbooking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

// ── Request DTOs ────────────────────────────────────────────────────────────

public class Dtos {

    // --- Flight registration ---
    public record CreateFlightRequest(
            @NotBlank(message = "Flight number is required")
            @Pattern(regexp = "[A-Z]{2}\\d{3,4}", message = "Flight number must match pattern e.g. AA123")
            String flightNumber,

            @NotBlank(message = "Origin is required")
            String origin,

            @NotBlank(message = "Destination is required")
            String destination,

            @NotNull(message = "Departure time is required")
            @Future(message = "Departure time must be in the future")
            LocalDateTime departureTime,

            @Min(value = 1, message = "Total seats must be at least 1")
            @Max(value = 900, message = "Total seats cannot exceed 900")
            int totalSeats
    ) {}

    // --- Booking ---
    public record PassengerRequest(
            @NotBlank(message = "First name is required")
            String firstName,

            @NotBlank(message = "Last name is required")
            String lastName,

            @NotBlank(message = "Passport number is required")
            @Size(min = 6, max = 20, message = "Passport number must be 6–20 characters")
            String passportNumber
    ) {}

    public record CreateBookingRequest(
            @NotBlank(message = "Flight number is required")
            String flightNumber,

            @NotEmpty(message = "At least one passenger is required")
            @Size(max = 9, message = "Cannot book more than 9 seats at once")
            @Valid
            List<PassengerRequest> passengers
    ) {}

    // ── Response DTOs ───────────────────────────────────────────────────────

    public record PassengerResponse(
            String firstName,
            String lastName,
            String passportNumber
    ) {}

    public record BookingResponse(
            String bookingReference,
            String flightNumber,
            String status,
            List<PassengerResponse> passengers,
            int seatCount,
            LocalDateTime bookedAt
    ) {}

    public record FlightResponse(
            String flightNumber,
            String origin,
            String destination,
            LocalDateTime departureTime,
            int totalSeats,
            int availableSeats,
            int bookedSeats
    ) {}

    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}
}
