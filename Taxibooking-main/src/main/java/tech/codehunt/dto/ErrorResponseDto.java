package tech.codehunt.dto;

public class ErrorResponseDto {
    private String timestamp;
    private int status;
    private String error;
    private String message;

    // Getters
    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }

    // Setters
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
}