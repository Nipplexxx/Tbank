import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

class TransferForm extends JFrame {

    private JTextField fromAccountField;
    private JTextField toAccountField;
    private JTextField amountField;
    private JLabel messageLabel;
    private String username;

    public TransferForm(String username) {
        this.username = username;
        setTitle("Перевод денег");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        add(panel);

        JLabel fromAccountLabel = new JLabel("Со счета:");
        fromAccountField = new JTextField(20);

        JLabel toAccountLabel = new JLabel("На счет:");
        toAccountField = new JTextField(20);

        JLabel amountLabel = new JLabel("Сумма:");
        amountField = new JTextField(20);

        JButton transferButton = new JButton("Перевести");

        messageLabel = new JLabel("");

        panel.add(fromAccountLabel);
        panel.add(fromAccountField);

        panel.add(toAccountLabel);
        panel.add(toAccountField);

        panel.add(amountLabel);
        panel.add(amountField);

        panel.add(transferButton);
        panel.add(new JLabel());

        panel.add(messageLabel);

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fromAccount = fromAccountField.getText();
                String toAccount = toAccountField.getText();
                String amountStr = amountField.getText();
                try {
                    double amount = Double.parseDouble(amountStr);
                    transferMoney(fromAccount, toAccount, amount);
                    messageLabel.setText("Перевод выполнен успешно!");
                } catch (NumberFormatException ex) {
                    messageLabel.setText("Неверная сумма!");
                } catch (IOException ex) {
                    messageLabel.setText("Ошибка при выполнении перевода!");
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    private void transferMoney(String fromAccount, String toAccount, double amount) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("accounts.txt"));
        Map<String, Double> accounts = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 7) {
                accounts.put(parts[2], Double.parseDouble(parts[4]));
            }
        }

        if (!accounts.containsKey(fromAccount)) {
            throw new IOException("Учетной запись, которая не найдена: " + fromAccount);
        }
        if (!accounts.containsKey(toAccount)) {
            throw new IOException("Учетная запись не найдена: " + toAccount);
        }

        if (accounts.get(fromAccount) < amount) {
            throw new IOException("Недостаточно средств на счете: " + fromAccount);
        }

        accounts.put(fromAccount, accounts.get(fromAccount) - amount);
        accounts.put(toAccount, accounts.get(toAccount) + amount);

        BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt"));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 7) {
                String accountNumber = parts[2];
                parts[4] = String.valueOf(accounts.get(accountNumber));
                writer.write(String.join(",", parts));
                writer.newLine();
            }
        }
        writer.close();

        int transactionId = generateNewID("transactions.txt");
        String timestamp = new Date().toString();
        writer = new BufferedWriter(new FileWriter("transactions.txt", true));
        writer.write(transactionId + "," + fromAccount + ",Перевод," + amount + "," + timestamp + ",Перевод на счет " + toAccount);
        writer.newLine();
        writer.write(transactionId + "," + toAccount + ",Перевод," + amount + "," + timestamp + ",Перевод со счета " + fromAccount);
        writer.newLine();
        writer.close();
    }

    private int generateNewID(String fileName) {
        int maxID = 0;
        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 1) {
                        int id = Integer.parseInt(parts[0]);
                        if (id > maxID) {
                            maxID = id;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maxID + 1;
    }
}
