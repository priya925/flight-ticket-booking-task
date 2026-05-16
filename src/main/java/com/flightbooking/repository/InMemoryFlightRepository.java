package com.flightbooking.repository;

import com.flightbooking.model.Booking;
import com.flightbooking.model.Flight;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFlightRepository {
    private final ConcurrentHashMap<String, Flight> flights = new ConcurrentHashMap<>();

    public Flight save(Flight flight) {
        flights.put(flight.getFlightNumber(), flight);
        return flight;
    }

    public Optional<Flight> findByFlightNumber(String flightNumber) {
        return Optional.ofNullable(flights.get(flightNumber));
    }

    public boolean existsByFlightNumber(String flightNumber) {
        return flights.containsKey(flightNumber);
    }

    public Collection<Flight> findAll() {
        return flights.values();
    }
}
