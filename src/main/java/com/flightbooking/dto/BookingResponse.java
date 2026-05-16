package com.flightbooking.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        String bookingReference,
        String flightNumber,
        String status,
        List<PassengerResponse> passengers,
        int seatCount,
        LocalDateTime bookedAt
) {}