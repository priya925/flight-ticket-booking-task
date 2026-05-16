package com.flightbooking.controller;

import com.flightbooking.dto.CreateFlightRequest;
import com.flightbooking.dto.FlightResponse;
import com.flightbooking.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Register a new flight (used to seed flights for booking).
     */
    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        FlightResponse response = flightService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get details for a specific flight including seat availability.
     */
    @GetMapping("/{flightNumber}")
    public ResponseEntity<FlightResponse> getFlight(@PathVariable String flightNumber) {
        return ResponseEntity.ok(flightService.getFlight(flightNumber));
    }

    /**
     * List all registered flights.
     */
    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }
}
