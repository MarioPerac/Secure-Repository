package gui;

import repository.SecureRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class RepositoryFrame extends JFrame {
    private JPanel mainPanel;
    private JTable table;
    private JButton chooseButton;
    private JButton uploadButton;
    private JLabel fileLabel;
    private JButton downloadButton;
    private JComboBox comboBox;
    private JButton signOutButton;
    private JLabel downloadErrorLabel;
    private File selectedFile;
    private DefaultTableModel repositoryTableModel;
    private static int nextFileNumber;

    public RepositoryFrame(String username) {
        setTitle("Secure Repository");
        setSize(550, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);


        repositoryTableModel = new DefaultTableModel();
        repositoryTableModel.addColumn("Number");
        repositoryTableModel.addColumn("Files");
        repositoryTableModel.addRow(new String[]{"Number", "Files"});
        List<String> filesNames = SecureRepository.showAllFilesNames(username);
        for (int i = 0; i < filesNames.size(); i++) {
            String number = Integer.toString(i);
            repositoryTableModel.addRow(new String[]{number, filesNames.get(i)});
            comboBox.addItem(number);
        }
        nextFileNumber = filesNames.size();


        table.setModel(repositoryTableModel);
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateColumnsFromModel(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        add(mainPanel);
        setVisible(true);

        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SignInFrame signInFrame = new SignInFrame();
                dispose();
            }
        });
        JFrame fileChooserFrame = new JFrame("File Chooser");
        fileChooserFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(fileChooserFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    fileLabel.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileLabel.setText("Successful upload.");
                if (selectedFile != null) {
                    SecureRepository.uploadFile(selectedFile.getAbsolutePath(), username);
                    repositoryTableModel.addRow(new String[]{Integer.toString(nextFileNumber), selectedFile.getName()});
                    nextFileNumber++;
                } else
                    fileLabel.setText("Nothing selected.");
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String fileName = filesNames.get(Integer.valueOf((String) comboBox.getSelectedItem()));
                try {
                    SecureRepository.downloadFile(username, fileName);
                    downloadErrorLabel.setText("Successful download.");
                } catch (Exception e) {
                    downloadErrorLabel.setText(e.getMessage());
                }
            }
        });
    }
}
