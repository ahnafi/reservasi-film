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
import javax.swing.table.JTableHeader;
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

    private ShowtimeService showtimeService;

    public HomeView() {
        Connection connection = Database.getConnection();
        ShowtimeRepository showtimeRepository = new ShowtimeRepository(connection);
        FilmRepository filmRepository = new FilmRepository(connection);
        StudioRepository studioRepository = new StudioRepository(connection);
        this.showtimeService = new ShowtimeService(showtimeRepository, filmRepository, studioRepository);

        setTitle("Showtime Film");
        setSize(1000, 600); // Increased height for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "ID Film", "ID Studio", "Showtime"}, 0);
        filmTable = new JTable(tableModel);
        filmTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filmTable.setRowHeight(25);
        filmTable.setFont(new Font("Arial", Font.PLAIN, 14));
        filmTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        filmTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        filmTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        filmTable.getColumnModel().getColumn(3).setPreferredWidth(200);

        JTableHeader tableHeader = filmTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 16));
        tableHeader.setBackground(Color.LIGHT_GRAY);
        tableHeader.setForeground(Color.BLACK);

        showtime();

        // Sidebar Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(144,51,51)); // 144,51,51
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(220, getHeight())); // Lebar sidebar

        btnDelete = createStyledButton("Delete Showtime");
        btnAddShowtime = createStyledButton("Add Showtime");
        btnSortShowtime = createStyledButton("Sort Showtime");
        btnShowFilmView = createStyledButton("Show Film List");
        btnShowStudioView = createStyledButton("Show Studio List");

        // Add buttons with spacing
        buttonPanel.add(Box.createVerticalStrut(150));
        buttonPanel.add(btnShowFilmView);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnShowStudioView);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnAddShowtime);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnSortShowtime);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(btnDelete);
        buttonPanel.add(Box.createVerticalGlue()); // Push buttons to top, add glue at bottom

        JScrollPane scrollPane = new JScrollPane(filmTable);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.WEST);

        // Button action listeners
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
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(0xEB9E2A)); //warna background button
        button.setForeground(Color.WHITE); // Warna teks putih
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 40));
        return button;
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
            JTextField filmIdField = new JTextField(10);
            JTextField studioIdField = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("Enter Film ID:"));
            panel.add(filmIdField);
            panel.add(new JLabel("Enter Studio ID:"));
            panel.add(studioIdField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add Showtime",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int filmId = Integer.parseInt(filmIdField.getText().trim());
                int studioId = Integer.parseInt(studioIdField.getText().trim());

                CreateShowtimeRequest request = new CreateShowtimeRequest();
                request.filmId = filmId;
                request.studioId = studioId;

                Showtime show = this.showtimeService.create(request);
                tableModel.addRow(new Object[]{show.id, show.filmId, show.studioId, show.showtime});

                JOptionPane.showMessageDialog(null, "Showtime added successfully!");
            }
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

            FindAllShowtimeResponse response = this.showtimeService.getAllShowtimes();
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