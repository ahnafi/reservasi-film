package Domain;

public class Reservation {
    public Integer id = null;
    public Integer showtimeId;
    public Integer chairNumber;
    public String status = ReservationStatus.Locked.toString();
}
