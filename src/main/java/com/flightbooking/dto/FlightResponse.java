package com.flightbooking.dto;

import java.time.LocalDateTime;

public record FlightResponse(
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime departureTime,
        int totalSeats,
        int availableSeats,
        int bookedSeats
) {}