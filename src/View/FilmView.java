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
        buttonPanel.setBackground(new Color(144,51,51));
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
        // Membuat panel dengan layout grid untuk form input
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Membuat komponen input
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField durationField = new JTextField();

        // Menambahkan label dan input field ke panel
        panel.add(new JLabel("Film ID:"));
        panel.add(idField);
        panel.add(new JLabel("Film Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Film Genre:"));
        panel.add(genreField);
        panel.add(new JLabel("Film Duration (minutes):"));
        panel.add(durationField);

        // Menampilkan form dalam dialog konfirmasi
        int result = JOptionPane.showConfirmDialog(null, panel,
                "Enter Film Details", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // Jika pengguna menekan OK, ambil dan proses inputnya
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Mengambil dan memvalidasi input
                int id = Integer.parseInt(idField.getText().trim());
                String title = titleField.getText().trim();
                String genre = genreField.getText().trim();
                int duration = Integer.parseInt(durationField.getText().trim());

                // Membuat request dan memproses input
                SaveFilmRequest request = new SaveFilmRequest();
                request.id = id;
                request.title = title;
                request.genre = genre;
                request.duration = duration;

                // Menambahkan film melalui service
                SaveFilmResponse response = this.filmService.add(request);
                Film film = response.film;

                // Memasukkan data film ke dalam tabel
                tableModel.addRow(new Object[]{film.id, film.title, film.genre, film.duration});
                JOptionPane.showMessageDialog(null, "Film " + film.title + " added successfully!");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid input! Please enter valid numbers for ID and Duration.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException | SQLException err) {
                JOptionPane.showMessageDialog(null,
                        "Error: " + err.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Operation cancelled.");
        }
    }

    private void updateFilm(int filmId) {
        try {
            // Ambil data film berdasarkan ID
            Film existingFilm = this.filmService.getFilmById(filmId);

            // Membuat panel untuk form input
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

            // Membuat input field dan mengisi nilai awal dengan data film yang ada
            JTextField titleField = new JTextField(existingFilm.title);
            JTextField genreField = new JTextField(existingFilm.genre);
            JTextField durationField = new JTextField(String.valueOf(existingFilm.duration));

            // Menambahkan komponen ke panel
            panel.add(new JLabel("Edit Title:"));
            panel.add(titleField);
            panel.add(new JLabel("Edit Genre:"));
            panel.add(genreField);
            panel.add(new JLabel("Edit Duration (minutes):"));
            panel.add(durationField);

            // Tampilkan form dalam dialog dengan tombol OK/Cancel
            int result = JOptionPane.showConfirmDialog(null, panel,
                    "Update Film", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                // Ambil input baru dari user
                String newTitle = titleField.getText().trim();
                String newGenre = genreField.getText().trim();
                int newDuration = Integer.parseInt(durationField.getText().trim());

                // Buat request untuk update film
                UpdateFilmRequest request = new UpdateFilmRequest();
                request.id = filmId;
                request.title = newTitle;
                request.genre = newGenre;
                request.duration = newDuration;

                // Lakukan update melalui service
                UpdateFilmResponse response = this.filmService.update(request);
                Film updatedFilm = response.film;

                // Update data di tabel
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if ((int) tableModel.getValueAt(i, 0) == filmId) {
                        tableModel.setValueAt(updatedFilm.title, i, 1);
                        tableModel.setValueAt(updatedFilm.genre, i, 2);
                        tableModel.setValueAt(updatedFilm.duration, i, 3);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(null, "Film " + updatedFilm.title + " updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Update cancelled.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid input! Please enter a valid number for duration.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | SQLException err) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + err.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
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