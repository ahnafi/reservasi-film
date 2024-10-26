package Model;

import Domain.ReservationStatus;

public class ReserveReservationRequest {
    public int showtimeId;
    public Integer chairNumber;
    public String status = ReservationStatus.Locked.toString();
}
