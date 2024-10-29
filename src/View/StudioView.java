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
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Studio ID:"));
            String name = JOptionPane.showInputDialog("Enter Studio Name:");
            int capacity = Integer.parseInt(JOptionPane.showInputDialog("Enter Studio Capacity:"));

            AddStudioRequest request = new AddStudioRequest();
            request.id = id;
            request.name = name;
            request.capacity = capacity;

            AddStudioResponse response = this.studioService.add(request);
            Studio studio = response.studio;

            tableModel.addRow(new Object[]{studio.id, studio.name, studio.capacity});
            JOptionPane.showMessageDialog(null, "Studio " + studio.name + " added successfully!");

        } catch (ValidationException | SQLException | NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }

    private void updateStudio(int studioId) {
        try {
            String name = JOptionPane.showInputDialog("Edit Name:");
            int capacity = Integer.parseInt(JOptionPane.showInputDialog("Edit Capacity:"));

            UpdateStudioRequest request = new UpdateStudioRequest();
            request.id = studioId;
            request.name = name;
            request.capacity = capacity;

            UpdateStudioResponse response = this.studioService.update(request);
            Studio studio = response.studio;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == studioId) {
                    tableModel.setValueAt(studio.name, i, 1);
                    tableModel.setValueAt(studio.capacity, i, 2);
                    break;
                }
            }
            JOptionPane.showMessageDialog(null, "Studio " + studio.name + " updated successfully!");

        } catch (ValidationException | SQLException | NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
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
