package tech.codehunt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

import tech.codehunt.dto.BookingRequestDto;
import tech.codehunt.model.Booking;
import tech.codehunt.service.BookingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/request")
    public ResponseEntity<Booking> requestRide(@Valid @RequestBody BookingRequestDto request) {
        Booking booking = bookingService.createBooking(request);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Booking> acceptRide(@PathVariable("id") Long id, @RequestParam("driverId") Long driverId) {
        Booking booking = bookingService.acceptBooking(id, driverId);
        return ResponseEntity.ok(booking);
    }

    // 🔥 NAYA API: VERIFY OTP TO START RIDE 🔥
    @PutMapping("/{id}/start")
    public ResponseEntity<?> startRide(@PathVariable("id") Long id, @RequestParam("otp") String otp) {
        try {
            Booking startedBooking = bookingService.startBooking(id, otp);
            return ResponseEntity.ok(startedBooking);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Booking> completeRide(@PathVariable("id") Long id) {
        Booking booking = bookingService.completeBooking(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelRide(@PathVariable("id") Long id) {
        Booking booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/decline")
    public ResponseEntity<Booking> declineRide(@PathVariable("id") Long id) {
        Booking booking = bookingService.declineBooking(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/rate")
    public ResponseEntity<?> rateRide(@PathVariable("id") Long id, @RequestBody Map<String, Object> payload) {
        try {
            Integer rating = Integer.valueOf(payload.get("rating").toString());
            String feedback = payload.get("feedback") != null ? payload.get("feedback").toString() : "";
            Booking updated = bookingService.rateBooking(id, rating, feedback);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Error submitting rating"));
        }
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Booking>> getPassengerBookings(@PathVariable("passengerId") Long passengerId) {
        List<Booking> bookings = bookingService.getPassengerBookings(passengerId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Booking>> getDriverBookings(@PathVariable("driverId") Long driverId) {
        List<Booking> bookings = bookingService.getDriverBookings(driverId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Booking>> getAvailableRides() {
        List<Booking> availableRides = bookingService.getAvailableBookings();
        return ResponseEntity.ok(availableRides);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "real_error", e.getClass().getName(),
                            "exact_reason", e.getMessage() != null ? e.getMessage() : "No message"
                    ));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable("id") Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok().body("Booking deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting booking");
        }
    }
}