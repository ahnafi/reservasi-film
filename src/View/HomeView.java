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
    private JButton btnEdit, btnDelete;

    private ShowtimeService showtimeService;

    public HomeView() {
        Connection connection = Database.getConnection();
        ShowtimeRepository showtimeRepository = new ShowtimeRepository(connection);
        FilmRepository filmRepository = new FilmRepository(connection);
        StudioRepository studioRepository = new StudioRepository(connection);
        this.showtimeService = new ShowtimeService(showtimeRepository, filmRepository, studioRepository);

        setTitle("");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"ID", "ID Film", "ID Studio","Showtime"}, 0);
        filmTable = new JTable(tableModel);

        home();

        JPanel buttonPanel = new JPanel();
        btnEdit = new JButton("Edit Selected Film");
        btnDelete = new JButton("Delete Selected Film");

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        // Menambahkan tabel ke dalam JScrollPane
        JScrollPane scrollPane = new JScrollPane(filmTable);

        // Menambahkan listener untuk tombol Delete
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = filmTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(null, "Film deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a film to delete.");
                }
            }
        });

        // Menambahkan listener untuk tombol Edit
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = filmTable.getSelectedRow();
                if (selectedRow != -1) {
                    String filmId = (String) tableModel.getValueAt(selectedRow, 0);
                    String filmTitle = (String) tableModel.getValueAt(selectedRow, 1);
                    String filmDirector = (String) tableModel.getValueAt(selectedRow, 2);

                    // Contoh logika untuk mengedit film yang dipilih
                    String newTitle = JOptionPane.showInputDialog("Edit Title:", filmTitle);
                    String newDirector = JOptionPane.showInputDialog("Edit Director:", filmDirector);

                    if (newTitle != null && newDirector != null) {
                        tableModel.setValueAt(newTitle, selectedRow, 1);
                        tableModel.setValueAt(newDirector, selectedRow, 2);
                        JOptionPane.showMessageDialog(null, "Film updated successfully!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a film to edit.");
                }
            }
        });

        // Mengatur layout dan menambahkan komponen
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void home (){
        try {

            FindAllShowtimeResponse response = this.showtimeService.getAllShowtimes();

            if(response.showtimes == null){
                JOptionPane.showMessageDialog(null, "No Showtimes found!");
                return;
            }

            for(Showtime data : response.showtimes){
                tableModel.addRow(new Object[]{data.id,data.filmId,data.studioId,data.showtime});
            }

        }catch (SQLException err) {
            JOptionPane.showMessageDialog(null,"error" + err.getMessage());
        }
    }


    public void addShowtime() {
        try {
            int filmId = 0;
            int studioId = 0;

//            view input form for filmId and studioId
//            filmId = inputFilmId();
//            studioId = inputStudioId();
//            lalu masukan ke variabel di atas

            CreateShowtimeRequest request = new CreateShowtimeRequest();
            request.filmId = filmId;
            request.studioId = studioId;

            this.showtimeService.create(request);

//            view success message alert atau tampilan
//            System.out.println("Showtime added successfully");

        } catch (ValidationException | SQLException e) {
//            view error message alert atau tampilan
            System.out.println(e.getMessage());
        }
    }

    public void updateShowtime() {
        try {
            int studioId = 0;
//        tombol sort showtime untuk mereset jam tayang berdasarkan studioId

            this.showtimeService.sortShowtime(studioId);

//            view success message alert atau tampilan
//            System.out.println("Showtime updated successfully");

//            tampilakan jam tayang yang sudah diurutkan

            FindAllShowtimeResponse response = this.showtimeService.getShowtimes(studioId);
//            tampilkan dalam bentuk list atau tabel , dengan view
//            System.out.println("Showtimes:");
//            for (Showtime showtime : response.showtimes) {
//                System.out.println(showtime.showtime);
//            }


        }catch (ValidationException | SQLException e){
//            view error message alert atau tampilan
            System.out.println(e.getMessage());
        }
    }

    public void removeShowtime() {
        try{

            int showtimeId = 0;

//            view input form for showtimeId
//            showtimeId = inputShowtimeId();
//            lalu masukan ke variabel di atas

            RemoveShowtimeRequest request = new RemoveShowtimeRequest();
            request.id = showtimeId;
            this.showtimeService.remove(request);

//            view success message alert atau tampilan
//            System.out.println("Showtime removed successfully");

        }catch (ValidationException | SQLException e){
//            view error message alert atau tampilan
            System.out.println(e.getMessage());
        }
    }

}
