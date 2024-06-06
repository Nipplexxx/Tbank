import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

class AccountForm extends JFrame {

    private DefaultTableModel tableModel;
    private DefaultTableModel transactionTableModel;
    private String username;
    private String customerId;

    public AccountForm(String username) {
        this.username = username;
        setTitle("Accounts - " + username);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel);

        String[] columnNames = {"Счет ID", "Клиент ID", "Номер счета", "Тип счета", "Баланс", "Дата создания счета", "Дата последнего обновления счета"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        panel.add(scrollPane, BorderLayout.CENTER);

        JButton transferButton = new JButton("Перевод денег");
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TransferForm(username);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(transferButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel for transactions
        JPanel transactionPanel = new JPanel(new BorderLayout());
        String[] transactionColumnNames = {"Транзакции ID", "Номер счета", "Тип транзакции ", "Сумма транзакции", "Дата и время транзакции", "Описание транзакции"};
        transactionTableModel = new DefaultTableModel(transactionColumnNames, 0);
        JTable transactionTable = new JTable(transactionTableModel);
        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
        transactionTable.setFillsViewportHeight(true);

        transactionPanel.add(new JLabel("Transaction History"), BorderLayout.NORTH);
        transactionPanel.add(transactionScrollPane, BorderLayout.CENTER);

        panel.add(transactionPanel, BorderLayout.EAST);

        loadCustomerData();
        loadAccountsFromFile("accounts.txt");
        loadTransactionsFromFile("transactions.txt");
        recordLastLogin("customers.txt");

        setVisible(true);
    }

    private void loadCustomerData() {
        try {
            Path path = Paths.get("customers.txt");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 11 && parts[9].equals(username)) {
                        customerId = parts[0];
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAccountsFromFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 7 && parts[1].equals(customerId)) {
                        tableModel.addRow(parts);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactionsFromFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 6) {
                        transactionTableModel.addRow(parts);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordLastLogin(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                List<String> newLines = new ArrayList<>();
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 11 && parts[9].equals(username)) {
                        parts[8] = new Date().toString();
                    }
                    newLines.add(String.join(",", parts));
                }
                Files.write(path, newLines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
