package Domain;

public class Reservation {
    public Integer id = null;
    public int showtimeId;
    public String chairNumber;
    public String status = ReservationStatus.Locked.toString();
}
