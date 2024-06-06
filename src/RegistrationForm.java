import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

class RegistrationForm extends JFrame {

    public RegistrationForm() {
        setTitle("Регистрация");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(12, 2));
        add(panel);

        JLabel idLabel = new JLabel("ID:");
        JLabel idValue = new JLabel("(автоматически)");

        JLabel firstNameLabel = new JLabel("Имя:");
        JTextField firstNameText = new JTextField(20);

        JLabel lastNameLabel = new JLabel("Фамилия:");
        JTextField lastNameText = new JTextField(20);

        JLabel dobLabel = new JLabel("Дата рождения:");
        JTextField dobText = new JTextField(20);

        JLabel addressLabel = new JLabel("Адрес:");
        JTextField addressText = new JTextField(20);

        JLabel phoneLabel = new JLabel("Телефон:");
        JTextField phoneText = new JTextField(20);

        JLabel emailLabel = new JLabel("Электронная почта:");
        JTextField emailText = new JTextField(20);

        JLabel loginLabel = new JLabel("Логин:");
        JTextField loginText = new JTextField(20);

        JLabel passwordLabel = new JLabel("Пароль:");
        JPasswordField passwordText = new JPasswordField(20);

        JButton registerButton = new JButton("Зарегистрировать");

        JLabel messageLabel = new JLabel("");

        panel.add(idLabel);
        panel.add(idValue);

        panel.add(firstNameLabel);
        panel.add(firstNameText);

        panel.add(lastNameLabel);
        panel.add(lastNameText);

        panel.add(dobLabel);
        panel.add(dobText);

        panel.add(addressLabel);
        panel.add(addressText);

        panel.add(phoneLabel);
        panel.add(phoneText);

        panel.add(emailLabel);
        panel.add(emailText);

        panel.add(loginLabel);
        panel.add(loginText);

        panel.add(passwordLabel);
        panel.add(passwordText);

        panel.add(registerButton);
        panel.add(new JLabel());

        panel.add(messageLabel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String dob = dobText.getText();
                String address = addressText.getText();
                String phone = phoneText.getText();
                String email = emailText.getText();
                String login = loginText.getText();
                String password = new String(passwordText.getPassword());

                if (!login.isEmpty() && !password.isEmpty()) {
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true));
                        int id = generateNewID("customers.txt");
                        String timestamp = new Date().toString();
                        writer.write(id + "," + firstName + "," + lastName + "," + dob + "," + address + "," + phone + "," + email + "," + timestamp + "," + timestamp + "," + login + "," + password);
                        writer.newLine();
                        writer.close();

                        writer = new BufferedWriter(new FileWriter("accounts.txt", true));
                        int accountId = generateNewID("accounts.txt");
                        String accountNumber = UUID.randomUUID().toString();
                        writer.write(accountId + "," + id + "," + accountNumber + ",Текущий,0.00," + timestamp + "," + timestamp);
                        writer.newLine();
                        writer.close();

                        messageLabel.setText("Регистрация успешна!");
                    } catch (IOException ioException) {
                        messageLabel.setText("Ошибка при регистрации!");
                        ioException.printStackTrace();
                    }
                } else {
                    messageLabel.setText("Все поля обязательны для заполнения!");
                }
            }
        });

        setVisible(true);
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
