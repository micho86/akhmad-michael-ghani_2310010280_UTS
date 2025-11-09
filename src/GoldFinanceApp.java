import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GoldFinanceApp extends JFrame {
    private FinanceManager manager;
    private DefaultTableModel tableModel;
    private JLabel balanceLabel;
    private ChartPanel chartPanel;

    public GoldFinanceApp() {
        manager = new FinanceManager();

        setTitle("💰 GoldFinance - Aplikasi Keuangan");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        JTextField dateField = new JTextField("2025-11-09");
        JTextField descField = new JTextField();
        JTextField amountField = new JTextField();
        String[] types = {"Pemasukan", "Pengeluaran"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        JButton addButton = new JButton("➕ Tambah Transaksi");

        inputPanel.add(new JLabel("Tanggal (yyyy-MM-dd):"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Deskripsi:"));
        inputPanel.add(descField);
        inputPanel.add(new JLabel("Jumlah:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Tipe:"));
        inputPanel.add(typeBox);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Tanggal", "Deskripsi", "Jumlah", "Tipe"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Balance Label
        balanceLabel = new JLabel("Saldo: Rp 0");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Chart Panel
        chartPanel = new ChartPanel();

        // Add Button Action
        addButton.addActionListener(e -> {
            try {
                LocalDate tanggal = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String deskripsi = descField.getText();
                double jumlah = Double.parseDouble(amountField.getText());
                String tipe = (String) typeBox.getSelectedItem();

                manager.addTransaction(new Transaction(tanggal, deskripsi, jumlah, tipe));
                tableModel.addRow(new Object[]{tanggal, deskripsi, jumlah, tipe});

                balanceLabel.setText("Saldo: Rp " + manager.getTotalBalance());
                chartPanel.updateChart(manager.getTransactions());
                dateField.setText(LocalDate.now().toString());
                descField.setText("");
                amountField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid! Pastikan format tanggal yyyy-MM-dd dan jumlah angka.");
            }
        });

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(addButton, BorderLayout.NORTH);
        bottomPanel.add(balanceLabel, BorderLayout.CENTER);
        bottomPanel.add(chartPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Panel untuk menggambar grafik
    static class ChartPanel extends JPanel {
        private double pemasukan = 0;
        private double pengeluaran = 0;

        public void updateChart(List<Transaction> transactions) {
            pemasukan = 0;
            pengeluaran = 0;
            for (Transaction t : transactions) {
                if (t.getType().equalsIgnoreCase("Pemasukan"))
                    pemasukan += t.getAmount();
                else
                    pengeluaran += t.getAmount();
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int barWidth = width / 4;
            int maxBarHeight = (int) (height * 0.7);
            double maxValue = Math.max(pemasukan, pengeluaran);

            if (maxValue == 0) return;

            int pemasukanHeight = (int) (maxBarHeight * (pemasukan / maxValue));
            int pengeluaranHeight = (int) (maxBarHeight * (pengeluaran / maxValue));

            g.setColor(Color.GREEN);
            g.fillRect(width / 4 - barWidth / 2, height - pemasukanHeight - 30, barWidth, pemasukanHeight);
            g.setColor(Color.RED);
            g.fillRect(3 * width / 4 - barWidth / 2, height - pengeluaranHeight - 30, barWidth, pengeluaranHeight);

            g.setColor(Color.BLACK);
            g.drawString("Pemasukan", width / 4 - 40, height - 10);
            g.drawString("Pengeluaran", 3 * width / 4 - 45, height - 10);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(600, 200);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GoldFinanceApp().setVisible(true));
    }
}


