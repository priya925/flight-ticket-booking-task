package com.flightbooking.service;

import com.flightbooking.dto.Dtos.*;
import com.flightbooking.exception.FlightAlreadyExistsException;
import com.flightbooking.exception.FlightNotFoundException;
import com.flightbooking.model.Flight;
import com.flightbooking.repository.InMemoryFlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    private final InMemoryFlightRepository flightRepository;

    public FlightService(InMemoryFlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public FlightResponse createFlight(CreateFlightRequest request) {
        if (flightRepository.existsByFlightNumber(request.flightNumber())) {
            throw new FlightAlreadyExistsException(request.flightNumber());
        }
        Flight flight = new Flight(
                request.flightNumber(),
                request.origin(),
                request.destination(),
                request.departureTime(),
                request.totalSeats()
        );
        flightRepository.save(flight);
        return toResponse(flight);
    }

    public FlightResponse getFlight(String flightNumber) {
        return toResponse(findOrThrow(flightNumber));
    }

    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Flight findOrThrow(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightNotFoundException(flightNumber));
    }

    private FlightResponse toResponse(Flight f) {
        return new FlightResponse(
                f.getFlightNumber(),
                f.getOrigin(),
                f.getDestination(),
                f.getDepartureTime(),
                f.getTotalSeats(),
                f.getAvailableSeats(),
                f.getBookedSeats()
        );
    }
}
