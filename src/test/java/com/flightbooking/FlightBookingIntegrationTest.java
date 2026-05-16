package com.flightbooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightbooking.dto.Dtos.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlightBookingIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static final String FLIGHT_NUMBER = "AA101";
    private static String bookingReference;

    @Test
    @Order(1)
    void createFlight_shouldReturn201() throws Exception {
        CreateFlightRequest req = new CreateFlightRequest(
                FLIGHT_NUMBER, "BLR", "DEL",
                LocalDateTime.now().plusDays(10), 2);

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value(FLIGHT_NUMBER))
                .andExpect(jsonPath("$.availableSeats").value(2));
    }

    @Test
    @Order(2)
    void createFlight_duplicate_shouldReturn409() throws Exception {
        CreateFlightRequest req = new CreateFlightRequest(
                FLIGHT_NUMBER, "BLR", "DEL",
                LocalDateTime.now().plusDays(10), 2);

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void createBooking_shouldReturn201() throws Exception {
        CreateBookingRequest req = new CreateBookingRequest(
                FLIGHT_NUMBER,
                List.of(new PassengerRequest("Alice", "Smith", "P1234567")));

        MvcResult result = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.seatCount").value(1))
                .andReturn();

        BookingResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingResponse.class);
        bookingReference = response.bookingReference();
    }

    @Test
    @Order(4)
    void createBooking_overbooking_shouldReturn409() throws Exception {
        // Only 1 seat left, but try to book 2
        CreateBookingRequest req = new CreateBookingRequest(
                FLIGHT_NUMBER,
                List.of(
                        new PassengerRequest("Bob", "Jones", "P9876543"),
                        new PassengerRequest("Carol", "White", "P1111111")
                ));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(5)
    void getBooking_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/" + bookingReference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingReference").value(bookingReference));
    }

    @Test
    @Order(6)
    void getFlight_shouldShowUpdatedAvailability() throws Exception {
        mockMvc.perform(get("/api/v1/flights/" + FLIGHT_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(1));
    }

    @Test
    @Order(7)
    void cancelBooking_shouldReturn200AndReleaseSeats() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/" + bookingReference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Seats should be back
        mockMvc.perform(get("/api/v1/flights/" + FLIGHT_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(2));
    }

    @Test
    @Order(8)
    void cancelBooking_alreadyCancelled_shouldReturn409() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/" + bookingReference))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(9)
    void getBooking_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/NOTEXIST"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    void createBooking_invalidInput_shouldReturn400() throws Exception {
        // Missing passengers list
        String badJson = """
                {"flightNumber": "AA101", "passengers": []}
                """;
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }
}
