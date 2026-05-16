package com.flightbooking.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Flight {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDateTime departureTime;
    private final int totalSeats;
    private final AtomicInteger bookedSeats;

    public Flight(String flightNumber, String origin, String destination,
                  LocalDateTime departureTime, int totalSeats) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.bookedSeats = new AtomicInteger(0);
    }

    /**
     * Thread-safe seat reservation. Returns true if seats were successfully reserved,
     * false if not enough seats are available (prevents overbooking).
     */
    public boolean reserveSeats(int count) {
        while (true) {
            int current = bookedSeats.get();
            int next = current + count;
            if (next > totalSeats) {
                return false;
            }
            if (bookedSeats.compareAndSet(current, next)) {
                return true;
            }
        }
    }

    public void releaseSeats(int count) {
        bookedSeats.updateAndGet(current -> Math.max(0, current - count));
    }

    public int getAvailableSeats() {
        return totalSeats - bookedSeats.get();
    }

    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public int getTotalSeats() { return totalSeats; }
    public int getBookedSeats() { return bookedSeats.get(); }
}
