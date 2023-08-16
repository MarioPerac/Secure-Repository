package gui;

import user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpFrame extends JFrame {

    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JLabel commonNameLabel;
    private JLabel emailLabel;
    private JPasswordField passwordField;
    private JTextField commonNameField;
    private JTextField emailField;
    private JButton signUpButton;
    private JPanel mainPanel;

    public SignUpFrame() {
        setTitle("Secure Repository");
        setSize(550, 600);
        add(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String commonName = commonNameField.getText();
                String email = emailField.getText();

                User newUser = new User(username, password, commonName, email);
                SignUp.addUser(newUser);
                SignInFrame signInFrame = new SignInFrame();
                dispose();
            }
        });
    }
}
