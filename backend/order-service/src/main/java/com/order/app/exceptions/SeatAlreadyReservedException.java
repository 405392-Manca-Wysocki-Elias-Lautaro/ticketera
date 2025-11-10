package com.order.app.exceptions;

public class SeatAlreadyReservedException extends RuntimeException {
    
    private final Long seatId;
    
    public SeatAlreadyReservedException(Long seatId) {
        super("Seat is already reserved: " + seatId);
        this.seatId = seatId;
    }
    
    public SeatAlreadyReservedException(Long seatId, String message) {
        super(message);
        this.seatId = seatId;
    }
    
    public Long getSeatId() {
        return seatId;
    }
}
