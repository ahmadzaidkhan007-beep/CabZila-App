package tech.codehunt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import tech.codehunt.dao.BookingDao;
import tech.codehunt.dao.UserDao;
import tech.codehunt.dto.BookingRequestDto;
import tech.codehunt.model.Booking;
import tech.codehunt.model.BookingStatus;
import tech.codehunt.model.User;

@Service
public class BookingService {

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private UserDao userDao;

    @Transactional
    public Booking createBooking(BookingRequestDto request) {
        if (request.getPassengerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "passengerId is required");
        }
        if (request.getPickupLocation() == null || request.getPickupLocation().isBlank()
                || request.getDropoffLocation() == null || request.getDropoffLocation().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pickupLocation and dropoffLocation are required");
        }

        User passenger = userDao.findById(request.getPassengerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setPickupLocation(request.getPickupLocation());
        booking.setDropoffLocation(request.getDropoffLocation());
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setVehicleType(request.getVehicleType() != null ? request.getVehicleType() : "SEDAN");

        // Calculate Base Fare
        double calculatedFare = calculateMockFare(request);

        // 🔥 APPLY ZILA COINS DISCOUNT 🔥
        if (request.getUseZilaCoins() != null && request.getUseZilaCoins()) {
            calculatedFare = calculatedFare - 10.0;
            if (calculatedFare < 0) calculatedFare = 0.0;
            booking.setCoinsUsed(true);
        } else {
            booking.setCoinsUsed(false);
        }

        booking.setFare(Math.round(calculatedFare * 100.0) / 100.0); // Safely Round off

        // ⚡ GENERATE RANDOM 4-DIGIT OTP ⚡
        String generatedOtp = String.format("%04d", new Random().nextInt(10000));
        booking.setOtp(generatedOtp);

        return bookingDao.save(booking);
    }

    @Transactional
    public Booking acceptBooking(Long bookingId, Long driverId) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getStatus() != BookingStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking cannot be accepted.");
        }

        User driver = userDao.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        booking.setDriver(driver);
        booking.setStatus(BookingStatus.ACCEPTED);
        return bookingDao.save(booking);
    }

    // ⚡ VERIFY OTP AND START RIDE ⚡
    @Transactional
    public Booking startBooking(Long bookingId, String enteredOtp) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride must be ACCEPTED first.");
        }
        if (booking.getOtp() == null || !booking.getOtp().equals(enteredOtp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP! Ask passenger for correct PIN.");
        }

        booking.setStatus(BookingStatus.ONGOING);
        return bookingDao.save(booking);
    }

    @Transactional
    public Booking completeBooking(Long bookingId) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        booking.setStatus(BookingStatus.COMPLETED);
        return bookingDao.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingDao.save(booking);
    }

    @Transactional
    public Booking declineBooking(Long bookingId) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        booking.setStatus(BookingStatus.DECLINED);
        return bookingDao.save(booking);
    }

    @Transactional
    public Booking rateBooking(Long bookingId, Integer rating, String feedback) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only COMPLETED rides can be rated.");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5 stars.");
        }

        booking.setRating(rating);
        booking.setFeedback(feedback != null ? feedback.trim() : "");
        return bookingDao.save(booking);
    }

    public List<Booking> getPassengerBookings(Long passengerId) { return bookingDao.findByPassengerId(passengerId); }
    public List<Booking> getDriverBookings(Long driverId) { return bookingDao.findByDriverId(driverId); }
    public List<Booking> getAvailableBookings() { return bookingDao.findByStatus(BookingStatus.REQUESTED); }
    public List<Booking> getAllBookings() { return bookingDao.findAll(); }

    @Transactional
    public void deleteBooking(Long bookingId) {
        if (!bookingDao.existsById(bookingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }
        bookingDao.deleteById(bookingId);
    }

    private Double calculateMockFare(BookingRequestDto request) {
        String route = request.getPickupLocation().trim().toLowerCase() + request.getDropoffLocation().trim().toLowerCase();
        int mockDistanceKm = 3 + (Math.abs(route.hashCode() % 13));

        double baseFare = 0.0; double perKmRate = 0.0;
        String vehicleType = request.getVehicleType() == null ? "SEDAN" : request.getVehicleType().trim().toUpperCase();

        switch (vehicleType) {
            case "BIKE": baseFare = 20.0; perKmRate = 5.0; break;
            case "AUTO": baseFare = 30.0; perKmRate = 9.0; break;
            case "SUV": baseFare = 70.0; perKmRate = 16.0; break;
            case "SEDAN": default: baseFare = 50.0; perKmRate = 12.0; break;
        }

        double totalFare = baseFare + (mockDistanceKm * perKmRate);
        LocalTime now = LocalTime.now();
        boolean isNightSurge = now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
        boolean isHighDemandSurge = (Math.abs(route.hashCode() % 10) < 3);

        if (isNightSurge || isHighDemandSurge) {
            totalFare = totalFare * 1.4;
        }
        return totalFare;
    }
}