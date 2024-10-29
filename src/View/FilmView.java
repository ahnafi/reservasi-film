package View;

import Config.Database;
import Domain.Film;
import Exception.ValidationException;
import Model.*;
import Repository.FilmRepository;
import Services.FilmService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class FilmView extends JFrame {
    private FilmService filmService;
    private JTable filmTable;
    private DefaultTableModel tableModel;
    private JButton btnAddFilm, btnEditFilm, btnDeleteFilm;

    public FilmView() {
        Connection connection = Database.getConnection();
        FilmRepository filmRepository = new FilmRepository(connection);
        this.filmService = new FilmService(filmRepository);

        setTitle("Film List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Genre", "Duration"}, 0);
        filmTable = new JTable(tableModel);

        showAll(); // Mengisi tabel dengan data film

        JPanel buttonPanel = new JPanel();
        btnAddFilm = new JButton("Add Film");
        btnEditFilm = new JButton("Edit Selected Film");
        btnDeleteFilm = new JButton("Delete Selected Film");

        buttonPanel.add(btnAddFilm);
        buttonPanel.add(btnEditFilm);
        buttonPanel.add(btnDeleteFilm);

        JScrollPane scrollPane = new JScrollPane(filmTable);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Tambahkan listener untuk tombol Add Film
        btnAddFilm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFilm();
            }
        });

        // Tambahkan listener untuk tombol Edit Film
        btnEditFilm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = filmTable.getSelectedRow();
                if (selectedRow != -1) {
                    int filmId = (int) tableModel.getValueAt(selectedRow, 0);
                    updateFilm(filmId);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a film to edit.");
                }
            }
        });

        // Tambahkan listener untuk tombol Delete Film
        btnDeleteFilm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = filmTable.getSelectedRow();
                if (selectedRow != -1) {
                    int filmId = (int) tableModel.getValueAt(selectedRow, 0);
                    deleteFilm(filmId);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a film to delete.");
                }
            }
        });
    }

    private void showAll() {
        try {
            FindAllFilmResponse response = this.filmService.showAll();
            for (Film film : response.films) {
                tableModel.addRow(new Object[]{film.id, film.title, film.genre, film.duration});
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }

    private void addFilm() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Film ID:"));
            String title = JOptionPane.showInputDialog("Enter Film Title:");
            String genre = JOptionPane.showInputDialog("Enter Film Genre:");
            int duration = Integer.parseInt(JOptionPane.showInputDialog("Enter Film Duration:"));

            SaveFilmRequest request = new SaveFilmRequest();
            request.id = id;
            request.title = title;
            request.genre = genre;
            request.duration = duration;

            SaveFilmResponse response = this.filmService.add(request);
            Film film = response.film;

            tableModel.addRow(new Object[]{film.id, film.title, film.genre, film.duration});
            JOptionPane.showMessageDialog(null, "Film " + film.title + " added successfully!");

        } catch (ValidationException | SQLException | NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }

    private void updateFilm(int filmId) {
        try {
            String title = JOptionPane.showInputDialog("Edit Title:");
            String genre = JOptionPane.showInputDialog("Edit Genre:");
            int duration = Integer.parseInt(JOptionPane.showInputDialog("Edit Duration:"));

            UpdateFilmRequest request = new UpdateFilmRequest();
            request.id = filmId;
            request.title = title;
            request.genre = genre;
            request.duration = duration;

            UpdateFilmResponse response = this.filmService.update(request);
            Film film = response.film;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == filmId) {
                    tableModel.setValueAt(film.title, i, 1);
                    tableModel.setValueAt(film.genre, i, 2);
                    tableModel.setValueAt(film.duration, i, 3);
                    break;
                }
            }
            JOptionPane.showMessageDialog(null, "Film " + film.title + " updated successfully!");

        } catch (ValidationException | SQLException | NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }

    private void deleteFilm(int filmId) {
        try {
            DeleteFilmRequest request = new DeleteFilmRequest();
            request.filmId = filmId;
            this.filmService.delete(request);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == filmId) {
                    tableModel.removeRow(i);
                    break;
                }
            }
            JOptionPane.showMessageDialog(null, "Film deleted successfully!");

        } catch (ValidationException | SQLException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }
}
