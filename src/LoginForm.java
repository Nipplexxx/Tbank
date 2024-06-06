import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

class LoginForm extends JFrame {

    private Map<String, String> users;

    public LoginForm() {
        setTitle("Login Form");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        add(panel);

        JLabel userLabel = new JLabel("Логин:");
        JTextField userText = new JTextField(20);

        JLabel passwordLabel = new JLabel("Пароль:");
        JPasswordField passwordText = new JPasswordField(20);

        JButton loginButton = new JButton("Войти");
        JButton cancelButton = new JButton("Закрыть");
        JButton registerButton = new JButton("Регистрация");

        JLabel messageLabel = new JLabel("");

        panel.add(userLabel);
        panel.add(userText);

        panel.add(passwordLabel);
        panel.add(passwordText);

        panel.add(loginButton);
        panel.add(cancelButton);

        panel.add(registerButton);
        panel.add(new JLabel());

        panel.add(messageLabel);

        // Загружаем пользователей из файла
        users = loadUsersFromFile("customers.txt");

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());

            if (users.containsKey(username) && users.get(username).equals(password)) {
                messageLabel.setText("Успешный вход!");
                new AccountForm(username);
                dispose();
            } else {
                messageLabel.setText("Неправильный логин или пароль!");
            }
        });

        cancelButton.addActionListener(e -> System.exit(0));

        registerButton.addActionListener(e -> new RegistrationForm());

        setVisible(true);
    }

    private Map<String, String> loadUsersFromFile(String fileName) {
        Map<String, String> users = new HashMap<>();
        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 11) {
                        String login = parts[9];
                        String password = parts[10];
                        users.put(login, password);
                    }
                }
            } else {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}
