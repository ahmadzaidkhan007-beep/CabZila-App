package tech.codehunt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.codehunt.model.Booking;
import tech.codehunt.model.BookingStatus; // Yeh naya import add hua hai
import java.util.List;

@Repository
public interface BookingDao extends JpaRepository<Booking, Long> {
    List<Booking> findByPassengerId(Long passengerId);

    List<Booking> findByDriverId(Long driverId);

    // NAYA METHOD: Driver ko pending (REQUESTED) rides dikhane ke liye
    List<Booking> findByStatus(BookingStatus status);
}