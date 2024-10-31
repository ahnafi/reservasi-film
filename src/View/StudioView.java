package View;

import Config.Database;
import Domain.Studio;
import Exception.ValidationException;
import Model.*;
import Repository.StudioRepository;
import Services.StudioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class StudioView extends JFrame {
    private StudioService studioService;
    private JTable studioTable;
    private DefaultTableModel tableModel;
    private JButton btnAddStudio, btnEditStudio, btnDeleteStudio;

    public StudioView() {
        Connection connection = Database.getConnection();
        StudioRepository studioRepository = new StudioRepository(connection);
        this.studioService = new StudioService(studioRepository);

        setTitle("Studio List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Capacity"}, 0);
        studioTable = new JTable(tableModel);

        showAll(); // Mengisi tabel dengan data studio

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x903333));
        btnAddStudio = new JButton("Add Studio");
        btnEditStudio = new JButton("Edit Selected Studio");
        btnDeleteStudio = new JButton("Delete Selected Studio");

        buttonPanel.add(btnAddStudio);
        buttonPanel.add(btnEditStudio);
        buttonPanel.add(btnDeleteStudio);

        JScrollPane scrollPane = new JScrollPane(studioTable);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Tambahkan listener untuk tombol Add Studio
        btnAddStudio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudio();
            }
        });

        // Tambahkan listener untuk tombol Edit Studio
        btnEditStudio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studioTable.getSelectedRow();
                if (selectedRow != -1) {
                    int studioId = (int) tableModel.getValueAt(selectedRow, 0);
                    updateStudio(studioId);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a studio to edit.");
                }
            }
        });

        // Tambahkan listener untuk tombol Delete Studio
        btnDeleteStudio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studioTable.getSelectedRow();
                if (selectedRow != -1) {
                    int studioId = (int) tableModel.getValueAt(selectedRow, 0);
                    deleteStudio(studioId);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a studio to delete.");
                }
            }
        });
    }

    private void showAll() {
        try {
            FindAllStudioResponse response = this.studioService.showAll();
            for (Studio studio : response.studios) {
                tableModel.addRow(new Object[]{studio.id, studio.name, studio.capacity});
            }
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }

    private void addStudio() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(10);
        JTextField capacityField = new JTextField(10);

        // Membuat panel untuk form input
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Studio ID:"));
        panel.add(idField);
        panel.add(new JLabel("Studio Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Studio Capacity:"));
        panel.add(capacityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Studio",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());

                // Validasi input sederhana
                if (name.isEmpty() || capacity <= 0) {
                    throw new ValidationException("Name tidak boleh kosong dan kapasitas harus lebih dari 0");
                }

                // Membuat request dan memanggil service
                AddStudioRequest request = new AddStudioRequest();
                request.id = id;
                request.name = name;
                request.capacity = capacity;

                AddStudioResponse response = this.studioService.add(request);
                Studio studio = response.studio;

                // Tambahkan ke tabel
                tableModel.addRow(new Object[]{studio.id, studio.name, studio.capacity});
                JOptionPane.showMessageDialog(null, "Studio " + studio.name + " added successfully!");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID dan kapasitas harus berupa angka");
            } catch (ValidationException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }
    }

    private void updateStudio(int studioId) {
        try {
            // Ambil data studio yang ada berdasarkan ID
            Studio existingStudio = this.studioService.getStudioById(studioId);

            // Membuat input fields dan isi dengan data sebelumnya
            JTextField nameField = new JTextField(existingStudio.name, 10);
            JTextField capacityField = new JTextField(String.valueOf(existingStudio.capacity), 10);

            // Membuat panel untuk form
            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("Studio Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Studio Capacity:"));
            panel.add(capacityField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Update Studio",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                // Ambil input baru dari pengguna
                String name = nameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());

                if (name.isEmpty() || capacity <= 0) {
                    throw new ValidationException("Name tidak boleh kosong dan kapasitas harus lebih dari 0");
                }

                // Membuat request update
                UpdateStudioRequest request = new UpdateStudioRequest();
                request.id = studioId;
                request.name = name;
                request.capacity = capacity;

                // Panggil service untuk update data
                UpdateStudioResponse response = this.studioService.update(request);
                Studio updatedStudio = response.studio;

                // Update data di tabel
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if ((int) tableModel.getValueAt(i, 0) == studioId) {
                        tableModel.setValueAt(updatedStudio.name, i, 1);
                        tableModel.setValueAt(updatedStudio.capacity, i, 2);
                        break;
                    }
                }

                JOptionPane.showMessageDialog(null,
                        "Studio " + updatedStudio.name + " updated successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Capacity harus berupa angka");
        } catch (ValidationException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }


    private void deleteStudio(int studioId) {
        try {
            DeleteStudioRequest request = new DeleteStudioRequest();
            request.id = studioId;
            this.studioService.delete(request);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == studioId) {
                    tableModel.removeRow(i);
                    break;
                }
            }
            JOptionPane.showMessageDialog(null, "Studio deleted successfully!");

        } catch (ValidationException | SQLException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }
}