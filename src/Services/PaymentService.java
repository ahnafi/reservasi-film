package Services;

import Config.Database;
import Domain.*;
import Repository.PaymentRepository;
import Repository.ReservationRepository;
import Repository.TicketRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

import Exception.ValidationException;

public class PaymentService {

    private PaymentRepository paymentRepository;
    private ReservationRepository reservationRepository;
    private TicketRepository ticketRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository, TicketRepository ticketRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
    }

    public void pay(int reservationId) throws ValidationException, SQLException {

        if(reservationId == 0) {
            throw new IllegalArgumentException("reservationId is required");
        }

        try{

            Database.beginTransaction();

            Reservation reservation = reservationRepository.find(reservationId);
            if(reservation == null) {
                throw new ValidationException("reservationId is not found");
            }

            if(reservation.status.equals(ReservationStatus.Confirmed.toString())) {
                throw new ValidationException("Reservation is already paid");
            }

            Payment payment = new Payment();
            payment.reservationId = reservationId;
            payment.amount = new BigDecimal(50000);
            payment.paymentDate = LocalDate.now().toString();
            payment.status = PaymentStatus.Completed.toString();

            paymentRepository.save(payment);

            reservation.status = ReservationStatus.Confirmed.toString();
            reservationRepository.update(reservation);

            Ticket ticket = new Ticket();
            ticket.reservationId = reservationId;
            ticket.chairNumber =  reservation.showtimeId + reservation.chairNumber.toString();
            ticket.purchaseDate = LocalDate.now().toString();
            ticketRepository.save(ticket);

            Database.commitTransaction();

        }catch (Exception e) {
            Database.rollbackTransaction();
            throw e;
        }

    }

}
