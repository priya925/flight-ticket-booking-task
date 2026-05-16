package com.flightbooking.dto;

public record PassengerResponse(
        String firstName,
        String lastName,
        String passportNumber
) {}