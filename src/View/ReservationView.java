package View;

import Config.Database;
import Domain.Payment;
import Domain.Showtime;
import Domain.Studio;
import Exception.ValidationException;
import Model.*;
import Repository.*;
import Services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.SQLException;

public class ReservationView extends JFrame {
    private ReservationService reservationService;
    private ShowtimeService showtimeService;
    private PaymentService paymentService;
    private TicketService ticketService;
    private StudioService studioService;

    private JComboBox<Integer> showtimeComboBox;
    private JComboBox<Integer> chairComboBox;
    private JTextField amountField;
    private JButton payButton, reserveButton;
    private JTextArea reservationResultArea;

    public ReservationView() {
        Connection connection = Database.getConnection();
        ReservationRepository reservationRepository = new ReservationRepository(connection);
        ShowtimeRepository showtimeRepository = new ShowtimeRepository(connection);
        StudioRepository studioRepository = new StudioRepository(connection);
        PaymentRepository paymentRepository = new PaymentRepository(connection);
        FilmRepository filmRepository = new FilmRepository(connection);
        TicketRepository ticketRepository = new TicketRepository(connection);
        StudioRepository studioRepository2 = new StudioRepository(connection);

        this.reservationService = new ReservationService(reservationRepository, showtimeRepository, studioRepository, paymentRepository);
        this.showtimeService = new ShowtimeService(showtimeRepository, filmRepository, studioRepository);
        this.paymentService = new PaymentService(paymentRepository, reservationRepository, ticketRepository);
        this.ticketService = new TicketService(ticketRepository);
        this.studioService = new StudioService(studioRepository);

        // Set konfigurasi frame
        setTitle("Reservation System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel untuk pemilihan showtime dan kursi
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        showtimeComboBox = new JComboBox<>();
        chairComboBox = new JComboBox<>();
        amountField = new JTextField();
        reserveButton = new JButton("Reserve");
        payButton = new JButton("Pay");

        inputPanel.add(new JLabel("Select Showtime ID:"));
        inputPanel.add(showtimeComboBox);
        inputPanel.add(new JLabel("Select Chair Number:"));
        inputPanel.add(chairComboBox);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);

        // Tambahkan panel dan tombol ke frame
        add(inputPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(reserveButton);
        buttonPanel.add(payButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Panel hasil reservasi
        reservationResultArea = new JTextArea(5, 40);
        reservationResultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reservationResultArea);
        add(scrollPane, BorderLayout.NORTH);

        // Muat semua showtime pada combobox
        loadShowtimes();

        // Listener untuk memperbarui daftar kursi berdasarkan showtime yang dipilih
        showtimeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateChairComboBox();
                }
            }
        });

        // Listener untuk tombol Reserve
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reserve();
            }
        });

        // Listener untuk tombol Pay
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pay();
            }
        });
    }

    private void loadShowtimes() {
        try {
            FindAllShowtimeResponse response = showtimeService.getAllShowtimes();
            for (Showtime showtime : response.showtimes) {
                showtimeComboBox.addItem(showtime.id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading showtimes: " + e.getMessage());
        }
    }

    private void updateChairComboBox() {
        chairComboBox.removeAllItems(); // Bersihkan kursi sebelumnya
        try {
            int showtimeId = Integer.parseInt(JOptionPane.showInputDialog("Masukan Showtime Id"));

            FindAllShowtimeResponse selectedShowtime = this.showtimeService.getShowtimes(showtimeId);
            Studio std = this.studioService.findById(selectedShowtime.showtimes[0].studioId) ;

            // Ambil kapasitas kursi dari studio yang terkait dengan showtime
            int chairCapacity = std.capacity;

            // Tambahkan kursi berdasarkan kapasitas studio
            for (int i = 1; i <= chairCapacity; i++) {
                chairComboBox.addItem(i);
            }

            if (chairCapacity == 0) {
                JOptionPane.showMessageDialog(this, "No chairs available for the selected showtime.");
            }
        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading chairs: " + e.getMessage());
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Please select a valid showtime.");
        }
    }


    private void reserve() {
        try {
            int showtimeId = (int) showtimeComboBox.getSelectedItem();
            int chairNumber = (int) chairComboBox.getSelectedItem();

            ReserveReservationRequest request = new ReserveReservationRequest();
            request.showtimeId = showtimeId;
            request.chairNumber = chairNumber;

            ReserveReservationResponse response = reservationService.reserve(request);
            reservationResultArea.setText("Reservation Successful: ");

            Payment payment = paymentService.getPayment(response.payment.id);
            reservationResultArea.append("\nPayment ID: " + payment.id + "\nAmount Due: " + payment.amount);
        } catch (ValidationException | SQLException | NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Reservation Error: " + e.getMessage());
        }
    }

    private void pay() {
        try {
            int paymentId = Integer.parseInt(JOptionPane.showInputDialog("Enter Payment ID:"));
            String amount = amountField.getText();

            CreatePaymentResponse response = paymentService.pay(paymentId, amount);
            reservationResultArea.setText("Payment Successful:\nTicket ID: " + response.ticket.id +
                    "\nReservation ID: " + response.ticket.reservationId +
                    "\nChair Number: " + response.ticket.chairNumber +
                    "\nPurchase Date: " + response.ticket.purchaseDate);
        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Payment Error: " + e.getMessage());
        }
    }
}
