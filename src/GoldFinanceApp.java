import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class GoldFinanceApp extends JFrame {
    private final FinanceManager financeManager;
    private final JTextField txtDate, txtDescription, txtAmount;
    private final JComboBox<String> cbType;
    private final JLabel lblBalance;
    private final DefaultTableModel tableModel;
    private final JButton btnAdd;

    public GoldFinanceApp() {
        financeManager = new FinanceManager();

        setTitle("GoldFinance — Keuangan Pribadi Elegan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        // panel utama
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 250, 230));
        add(panel);

        // === PANEL INPUT ===
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(new Color(255, 250, 230));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Tanggal
        c.gridx = 0; c.gridy = 0;
        inputPanel.add(new JLabel("Tanggal (YYYY-MM-DD):"), c);
        txtDate = new JTextField();
        c.gridx = 1; c.gridy = 0; c.gridwidth = 2;
        inputPanel.add(txtDate, c);

        // Keterangan
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
        inputPanel.add(new JLabel("Keterangan:"), c);
        txtDescription = new JTextField();
        c.gridx = 1; c.gridy = 1; c.gridwidth = 2;
        inputPanel.add(txtDescription, c);

        // Jumlah
        c.gridx = 0; c.gridy = 2; c.gridwidth = 1;
        inputPanel.add(new JLabel("Jumlah (Rp):"), c);
        txtAmount = new JTextField();
        c.gridx = 1; c.gridy = 2; c.gridwidth = 2;
        inputPanel.add(txtAmount, c);

        // Tipe Transaksi
        c.gridx = 0; c.gridy = 3; c.gridwidth = 1;
        inputPanel.add(new JLabel("Tipe Transaksi:"), c);
        cbType = new JComboBox<>(new String[]{"Pemasukan", "Pengeluaran"});
        c.gridx = 1; c.gridy = 3; c.gridwidth = 2;
        inputPanel.add(cbType, c);

        // Tombol Tambah
        btnAdd = new JButton("➕ Tambah Transaksi");
        btnAdd.setBackground(new Color(230, 230, 255));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> addTransaction());

        c.gridx = 0; c.gridy = 4; c.gridwidth = 3;
        inputPanel.add(btnAdd, c);

        panel.add(inputPanel, BorderLayout.NORTH);

        // === TABEL HISTORI ===
        String[] columns = {"Tanggal", "Keterangan", "Jumlah (Rp)", "Tipe"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // === LABEL SALDO ===
        lblBalance = new JLabel("Total Saldo: Rp 0", SwingConstants.CENTER);
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBalance.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(lblBalance, BorderLayout.SOUTH);
    }

    // === METHOD TAMBAH TRANSAKSI ===
    private void addTransaction() {
        try {
            LocalDate date = LocalDate.parse(txtDate.getText().trim());
            String desc = txtDescription.getText().trim();
            double amount = Double.parseDouble(txtAmount.getText().trim());
            String type = (String) cbType.getSelectedItem();

            Transaction transaction = new Transaction(date, desc, amount, type);
            financeManager.addTransaction(transaction);

            // Tambahkan ke tabel
            tableModel.addRow(new Object[]{
                    transaction.getDate(),
                    transaction.getDescription(),
                    String.format("Rp %, .0f", transaction.getAmount()),
                    transaction.getType()
            });

            // Update saldo otomatis
            double total = financeManager.getTotalBalance();
            lblBalance.setText(String.format("Total Saldo: Rp %, .0f", total));

            // Bersihkan input
            txtDescription.setText("");
            txtAmount.setText("");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah! Gunakan YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GoldFinanceApp().setVisible(true));
    }
}


