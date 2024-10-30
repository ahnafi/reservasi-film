package Services;

import Config.Database;
import Domain.Payment;
import Domain.PaymentStatus;
import Domain.Reservation;
import Domain.Showtime;
import Model.ReserveReservationResponse;
import Model.UpdateReservationRequest;
import Model.UpdateReservationResponse;
import Model.ReserveReservationRequest;
import Repository.PaymentRepository;
import Repository.ReservationRepository;
import Repository.ShowtimeRepository;

import Exception.ValidationException;
import Repository.StudioRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationService {

    private ReservationRepository reservationRepository;
    private ShowtimeRepository showtimeRepository;
    private StudioRepository studioRepository;
    private PaymentRepository paymentRepository;

    public ReservationService(ReservationRepository reservationRepository, ShowtimeRepository showtimeRepository, StudioRepository studioRepository, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.showtimeRepository = showtimeRepository;
        this.studioRepository = studioRepository;
        this.paymentRepository = paymentRepository;
    }

    public ReserveReservationResponse reserve(ReserveReservationRequest request) throws ValidationException, SQLException {
        validateReserveReservationRequest(request);
        try {
            Database.beginTransaction();

            Showtime showtime = showtimeRepository.find(request.showtimeId);
            if (showtime == null) {
                throw new ValidationException("showtimeId is not found");
            }

            int capacity = this.studioRepository.find(showtime.studioId).capacity;
            Reservation[] reserved = this.reservationRepository.findByShowtime(showtime.id);

            if (reserved.length >= capacity) {
                throw new ValidationException("Studio is full");
            }

            for (Reservation reservation : reserved) {
                if (reservation.chairNumber.equals(request.chairNumber)) {
                    throw new ValidationException("Chair is already reserved");
                }
            }

            Reservation reservation = new Reservation();
            reservation.showtimeId = showtime.id;
            reservation.chairNumber = request.chairNumber;
            reservation.status = request.status;

            reservation.id = this.reservationRepository.save(reservation).id;

            Payment pay = new Payment();
            pay.reservationId = reservation.id;
            pay.amount = new BigDecimal(50000);
            pay.paymentDate = LocalDate.now().toString();
            pay.status = PaymentStatus.Pending.toString();

            pay.id = this.paymentRepository.save(pay).id;


            ReserveReservationResponse response = new ReserveReservationResponse();
            response.reservation = reservation;
            response.payment = pay;

            Database.commitTransaction();
            return response;

        } catch (SQLException e) {
            Database.rollbackTransaction();
            throw e;
        }
    }

    private void validateReserveReservationRequest(ReserveReservationRequest request) throws ValidationException {
        if (request.showtimeId <= 0) {
            throw new ValidationException("showtimeId is required");
        }
        if (request.chairNumber == null || request.chairNumber <= 0) {
            throw new ValidationException("chairNumber is required");
        }
    }

    public Reservation update(UpdateReservationRequest request) throws ValidationException, SQLException {
        validateUpdateReservationRequest(request);

        try {
            Database.beginTransaction();

            Reservation reservation = this.reservationRepository.find(request.id);
            if (reservation == null) {
                throw new ValidationException("Reservation not found");
            }

            Showtime showtime = this.showtimeRepository.find(request.showtimeId);
            if (showtime == null) {
                throw new ValidationException("Showtime not found");
            }

            int capacity = this.studioRepository.find(showtime.studioId).capacity;
            Reservation[] reserved = this.reservationRepository.findByShowtime(showtime.id);

            if (reserved.length >= capacity) {
                throw new ValidationException("Studio is full");
            }

            for (Reservation reservedReservation : reserved) {
                if (reservedReservation.chairNumber.equals(request.chairNumber)) {
                    throw new ValidationException("Chair is already reserved");
                }
            }

            reservation.showtimeId = request.showtimeId;
            reservation.chairNumber = request.chairNumber;
            reservation.status = request.status;

            UpdateReservationResponse response = new UpdateReservationResponse();
            response.reservation = this.reservationRepository.update(reservation);

            Database.commitTransaction();

            return reservation;
        } catch (SQLException e) {
            Database.rollbackTransaction();
            throw e;
        }
    }

    private void validateUpdateReservationRequest(UpdateReservationRequest request) throws ValidationException {
        if (request.id <= 0) {
            throw new ValidationException("ReservationId is required");
        }
        if (request.showtimeId <= 0) {
            throw new ValidationException("showtimeId is required");
        }
        if (request.chairNumber == null || request.chairNumber <= 0) {
            throw new ValidationException("chairNumber is required");
        }
        if (request.status == null || request.status.isEmpty()) {
            throw new ValidationException("status is required");
        }
    }

    public void cancel(int ReservationId) throws ValidationException, SQLException {

        if (ReservationId <= 0) {
            throw new ValidationException("ReservationId is required");
        }

        try {
            Database.beginTransaction();

            Reservation reservation = this.reservationRepository.find(ReservationId);
            if (reservation == null) {
                throw new ValidationException("Reservation not found");
            }

            this.reservationRepository.delete(ReservationId);

            Database.commitTransaction();

        } catch (SQLException e) {
            Database.rollbackTransaction();
            throw e;
        }
    }

    public Reservation[] getReservedChairsByShowtime(int showtimeId) throws SQLException {
        return this.reservationRepository.findByShowtime(showtimeId);
    }


}
