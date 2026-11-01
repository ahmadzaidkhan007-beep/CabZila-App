package tech.codehunt.model;

public enum BookingStatus {
    REQUESTED,
    ACCEPTED,
    ONGOING,      // NAYA STATUS: Ride start hone ke baad
    COMPLETED,
    CANCELLED,
    DECLINED
}