package View;

import javax.swing.*;
import java.awt.*;

public class ComboBoxExample extends JFrame {
    private JComboBox<String> comboBox;
    private JTextField txtSelection;

    public ComboBoxExample() {
        setTitle("ComboBox Example");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Membuat JComboBox dengan beberapa pilihan
        comboBox = new JComboBox<>(new String[]{"Action", "Drama", "Comedy", "Horror", "Sci-Fi"});

        // Menambahkan listener untuk mendapatkan pilihan
        comboBox.addActionListener(e -> {
            String selectedGenre = (String) comboBox.getSelectedItem();
            txtSelection.setText(selectedGenre);
        });

        // Membuat JTextField untuk menampilkan pilihan yang dipilih
        txtSelection = new JTextField(15);
        txtSelection.setEditable(false);

        // Menambahkan komponen ke frame
        add(new JLabel("Select Genre:"));
        add(comboBox);
        add(new JLabel("Selected:"));
        add(txtSelection);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ComboBoxExample example = new ComboBoxExample();
            example.setVisible(true);
        });
    }
}
