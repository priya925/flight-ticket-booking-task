package com.flightbooking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateBookingRequest(
        @NotBlank(message = "Flight number is required")
        String flightNumber,

        @NotEmpty(message = "At least one passenger is required")
        @Size(max = 9, message = "Cannot book more than 9 seats at once")
        @Valid
        List<PassengerRequest> passengers
) {}