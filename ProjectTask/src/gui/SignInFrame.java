package gui;

import javax.swing.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignInFrame extends JFrame {
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JPanel mainPanel;
    private JLabel certificateLabel;
    private JButton chooseButton;
    private JButton signUpButton;
    private JTextArea errorArea;
    private File selectedFile;
    private int SIGN_IN_ATTEMPTS = 3;
    private int REACTIVATE_ATTEMPTS = 1;


    public SignInFrame() {
        setTitle("Secure Repository");
        setSize(550, 600);
        add(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        JFrame fileChooserFrame = new JFrame("File Chooser");
        fileChooserFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(fileChooserFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    chooseButton.setText("SELECTED");
                }
            }
        });

        setVisible(true);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SignUpFrame signUpFrame = new SignUpFrame();
                dispose();
            }
        });
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String certificate = selectedFile.getAbsolutePath();
                String username = usernameField.getText();
                String password = passwordField.getText();
                RepositoryFrame repositoryFrame = null;
                try {

                    if (SIGN_IN_ATTEMPTS > 0 && SignIn.checkUser(certificate, username, password)) {
                        repositoryFrame = new RepositoryFrame(username);
                        dispose();
                    }


                } catch (Exception e) {
                    errorArea.setText(e.getMessage());
                    SIGN_IN_ATTEMPTS--;
                }

                if (SIGN_IN_ATTEMPTS == 0 && REACTIVATE_ATTEMPTS > 0) {
                    SignIn.suspendUserCertificate(username);
                    errorArea.setText("Your certificate has been SUSPENDED! \n" +
                            "You have one attempt to REACTIVATE it \n" +
                            " or create a new account!");
                    SIGN_IN_ATTEMPTS++;
                    REACTIVATE_ATTEMPTS--;
                } else if (SIGN_IN_ATTEMPTS == 1 && REACTIVATE_ATTEMPTS == 0) {
                    SignIn.reactivateUserCertificate(username);
                } else if (REACTIVATE_ATTEMPTS == 0) {

                    errorArea.setText("You can not reactivate your certificate,\n please create a new account.");
                    SignIn.removeUserAccount(username);
                    REACTIVATE_ATTEMPTS--;
                }

            }
        });
    }
}
