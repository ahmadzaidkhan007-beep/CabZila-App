package tech.codehunt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookingRequestDto {

    @NotNull(message = "Passenger ID is required")
    private Long passengerId;

    @NotBlank(message = "Pickup location cannot be empty")
    private String pickupLocation;

    @NotBlank(message = "Dropoff location cannot be empty")
    private String dropoffLocation;

    private String vehicleType;

    // 🔥 NAYA FIELD: Coin use karne ke liye
    private Boolean useZilaCoins;

    // Getters
    public Long getPassengerId() { return passengerId; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDropoffLocation() { return dropoffLocation; }
    public String getVehicleType() { return vehicleType; }
    public Boolean getUseZilaCoins() { return useZilaCoins; }

    // Setters
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setUseZilaCoins(Boolean useZilaCoins) { this.useZilaCoins = useZilaCoins; }
}