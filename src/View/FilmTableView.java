package View;

import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FilmTableView extends JFrame {
    private JTable filmTable;
    private DefaultTableModel tableModel;
    private JButton btnEdit, btnDelete;

    public FilmTableView() {
        setTitle("Film List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Membuat model tabel dengan kolom "ID", "Title", dan "Director"
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Director"}, 0);
        filmTable = new JTable(tableModel);

        // Menambahkan contoh data film ke tabel
        addSampleData();

        // Membuat panel untuk tombol Edit dan Delete
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

    private void addSampleData() {
        // Menambahkan beberapa data contoh ke dalam tabel
        tableModel.addRow(new Object[]{"1", "Inception", "Christopher Nolan"});
        tableModel.addRow(new Object[]{"2", "The Matrix", "Wachowskis"});
        tableModel.addRow(new Object[]{"3", "Interstellar", "Christopher Nolan"});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FilmTableView filmTableView = new FilmTableView();
            filmTableView.setVisible(true);
        });
    }
}
