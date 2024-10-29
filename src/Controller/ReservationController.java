package Controller;

import Config.Database;
import Domain.Payment;
import Model.*;
import Exception.ValidationException;
import Repository.*;
import Services.PaymentService;
import Services.ReservationService;
import Services.ShowtimeService;
import Services.TicketService;

import javax.swing.JFrame;
import java.sql.Connection;
import java.sql.SQLException;


public class ReservationController extends JFrame {

    private ReservationService reservationService;
    private ShowtimeService showtimeService;
    private PaymentService paymentService;
    private TicketService ticketService;

    public ReservationController() {
        Connection connection = Database.getConnection();
        ReservationRepository reservationRepository = new ReservationRepository(connection);
        ShowtimeRepository showtimeRepository = new ShowtimeRepository(connection);
        StudioRepository studioRepository = new StudioRepository(connection);
        PaymentRepository paymentRepository = new PaymentRepository(connection);
        FilmRepository filmRepository = new FilmRepository(connection);
        TicketRepository TicketRepository = new TicketRepository(connection);

        this.reservationService = new ReservationService(reservationRepository, showtimeRepository, studioRepository, paymentRepository);
        this.showtimeService = new ShowtimeService(showtimeRepository, filmRepository, studioRepository);
        this.paymentService = new PaymentService(paymentRepository, reservationRepository, TicketRepository);
        this.ticketService = new TicketService(TicketRepository);
    }

    public void reserve() {
        int showtimeId = 0;
        int chairNumber = 0;
        try {
            int studioId = 0;
            FindAllShowtimeResponse resAllShow = this.showtimeService.getAllShowtimes();

//            tampilkan semua showtime yang ada
//            for(Showtime showtime : resAllShow.showtimes){
//            System.out.println("Showtime ID: " + showtime.id + " Film ID: " + showtime.filmId + " Studio ID: " + showtime.studioId + " Showtime: " + showtime.showtime);
//            }
//            isi studioId dengan studioId yang dipilih
//            ketika showtime dengan setudioid tertentu dipilih maka akan muncul showtimeId dan chairNumber

            FindAllShowtimeResponse resShowtimeByStudio = this.showtimeService.getShowtimes(showtimeId);

//              for(Showtime showtime : resShowtimeByStudio.showtimes){
////            System.out.println("Showtime ID: " + showtime.id + " Film ID: " + showtime.filmId + " Studio ID: " + showtime.studioId + " Showtime: " + showtime.showtime);
////            }


//            tampilkan showtime yang dipilih
//            for (int i = 0; i < resShowtimeByStudio.chairCapacity; i++) {
//            tampikan showtimeId dan chairNumber [][][][][][][]

//            }
//             jik sudah memilih nomor kursi ,set chairNumber dengan nomor kursi yang dipilih

            ReserveReservationRequest request = new ReserveReservationRequest();
            request.showtimeId = showtimeId;
            request.chairNumber = chairNumber;

            ReserveReservationResponse response = this.reservationService.reserve(request);

//            tampilkan response message
//            System.out.println(response.message);

            Payment dataPayment = this.paymentService.getPayment(response.payment.id);
            boolean isPay = false;
            String amount = "";
//            tampilkan view payment dengan parameter response.payment

//            jika user menekan tombol pay  maka akan memanggil method pay

            if (isPay) {
                CreatePaymentResponse paySuccess = this.paymentService.pay(response.payment.id, amount);

//                tampilkan view ticket dengan parameter paySuccess.ticket
//                System.out.println(paySuccess.ticket.id + " " + paySuccess.ticket.reservationId + " " + paySuccess.ticket.chairNumber + " " + paySuccess.ticket.purchaseDate);
            } else {
//                tampilkan view payment dengan parameter response.payment
                this.paymentService.cancel(response.payment.id);
            }

        } catch (ValidationException | SQLException err) {
//            alert error message
            System.out.println(err.getMessage());
        }
    }

}
