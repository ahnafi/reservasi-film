package View;

import Config.Database;
import Domain.Showtime;
import Exception.ValidationException;
import Model.CreateShowtimeRequest;
import Model.FindAllShowtimeResponse;
import Model.RemoveShowtimeRequest;
import Repository.FilmRepository;
import Repository.ShowtimeRepository;
import Repository.StudioRepository;
import Services.ShowtimeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class HomeView extends JFrame {
    private JTable filmTable;
    private DefaultTableModel tableModel;
    private JButton btnDelete, btnAddShowtime, btnSortShowtime;
    private JButton btnShowFilmView;
    private JButton btnShowStudioView;
    private JButton btnShowReservationView;

    private ShowtimeService showtimeService;

    public HomeView() {
        Connection connection = Database.getConnection();
        ShowtimeRepository showtimeRepository = new ShowtimeRepository(connection);
        FilmRepository filmRepository = new FilmRepository(connection);
        StudioRepository studioRepository = new StudioRepository(connection);
        this.showtimeService = new ShowtimeService(showtimeRepository, filmRepository, studioRepository);

        setTitle("Showtime Film");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"ID", "ID Film", "ID Studio", "Showtime"}, 0);
        filmTable = new JTable(tableModel);

        showtime();

        JPanel buttonPanel = new JPanel();

        btnDelete = new JButton("Delete Selected Showtime");
        btnAddShowtime = new JButton("Add Showtime");
        btnSortShowtime = new JButton("Sort Showtime");
        btnShowReservationView = new JButton("Open Reservation View");

        btnShowFilmView = new JButton("Show Film List");
        btnShowStudioView = new JButton("Show Studio List");

        buttonPanel.add(btnShowFilmView);
        buttonPanel.add(btnShowStudioView);
        buttonPanel.add(btnShowReservationView);

        btnShowFilmView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilmView filmView = new FilmView();
                filmView.setVisible(true);
            }
        });

        btnShowStudioView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudioView studioView = new StudioView();
                studioView.setVisible(true);
            }
        });

        btnShowReservationView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReservationView reservationView = new ReservationView();
                reservationView.setVisible(true);
            }
        });

        buttonPanel.add(btnAddShowtime);
        buttonPanel.add(btnSortShowtime);
        buttonPanel.add(btnDelete);

        JScrollPane scrollPane = new JScrollPane(filmTable);

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeShowtime();
            }
        });

        btnAddShowtime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addShowtime();
            }
        });

        btnSortShowtime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortShowtime();
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void showtime() {
        try {
            FindAllShowtimeResponse response = this.showtimeService.getAllShowtimes();
            for (Showtime data : response.showtimes) {
                tableModel.addRow(new Object[]{data.id, data.filmId, data.studioId, data.showtime});
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }

    public void addShowtime() {
        try {
            int filmId = Integer.parseInt(JOptionPane.showInputDialog("Enter Film ID:"));
            int studioId = Integer.parseInt(JOptionPane.showInputDialog("Enter Studio ID:"));

            CreateShowtimeRequest request = new CreateShowtimeRequest();
            request.filmId = filmId;
            request.studioId = studioId;

            this.showtimeService.create(request);
            JOptionPane.showMessageDialog(null, "Showtime added successfully!");

        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter valid numbers for IDs.");
        }
    }

    public void sortShowtime() {
        try {
            int studioId = Integer.parseInt(JOptionPane.showInputDialog("Enter Studio ID to Sort Showtimes:"));

            this.showtimeService.sortShowtime(studioId);
            JOptionPane.showMessageDialog(null, "Showtimes updated and sorted successfully!");

            FindAllShowtimeResponse response = this.showtimeService.getShowtimes(studioId);
            tableModel.setRowCount(0);

            for (Showtime showtime : response.showtimes) {
                tableModel.addRow(new Object[]{showtime.id, showtime.filmId, showtime.studioId, showtime.showtime});
            }

        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter a valid number for Studio ID.");
        }
    }

    public void removeShowtime() {
        try {
            int selectedRow = filmTable.getSelectedRow();
            if (selectedRow != -1) {
                int showtimeId = (int) tableModel.getValueAt(selectedRow, 0);

                RemoveShowtimeRequest request = new RemoveShowtimeRequest();
                request.id = showtimeId;

                this.showtimeService.remove(request);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(null, "Showtime removed successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a showtime to delete.");
            }
        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}
