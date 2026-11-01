package tech.codehunt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String dropoffLocation;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Double fare;

    private String vehicleType;

    // 🔥 OTP for Secure Ride Start
    @Column(length = 4)
    private String otp;

    // 🔥 NAYA FIELD: Coin Discount Tracking 🔥
    @Column(columnDefinition = "boolean default false")
    private Boolean coinsUsed = false;

    private Integer rating;
    private String feedback;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private User driver;

    public Booking() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    // 🔥 NAYA GETTER/SETTER 🔥
    public Boolean getCoinsUsed() { return coinsUsed; }
    public void setCoinsUsed(Boolean coinsUsed) { this.coinsUsed = coinsUsed; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getPassenger() { return passenger; }
    public void setPassenger(User passenger) { this.passenger = passenger; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public String getPassengerName() {
        return this.passenger != null ? this.passenger.getName() : "Unknown Passenger";
    }

    public String getDriverName() {
        return this.driver != null ? this.driver.getName() : "Searching...";
    }
}