package repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import openssl.OpenSSL;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureRepository {
    private static String USERS_LOCAL_DIR = "UsersLocalRepo";
    private static String USERS_DIR = "users";
    private static char REPLACEMENT_CHAR = '_';

    public static void uploadFile(String file, String username) {
        String fileName = Paths.get(file).getFileName().toString();
        String folderName = fileName.replace('.', REPLACEMENT_CHAR);
        String userDir = USERS_DIR + File.separator + username;
        String fileFolder = userDir + File.separator + folderName;
        try {
            Files.createDirectories(Paths.get(fileFolder));
        } catch (IOException e) {
            System.err.println(e);
        }
        String privateKey = USERS_LOCAL_DIR + File.separator + username + File.separator + "private.key";
        String publicKey = USERS_LOCAL_DIR + File.separator + username + File.separator + "public.key";

        List<List<String>> splittedFile = splitFile(file);
        createEncryptedAndSignedFilesInSeparateDir(splittedFile, fileFolder, privateKey, publicKey);
    }

    public static void downloadFile(String username, String fileName) throws Exception {
        String folderNmame = fileName.replace('.', REPLACEMENT_CHAR);
        String userDir = USERS_DIR + File.separator + username + File.separator + folderNmame;
        String privateKey = USERS_LOCAL_DIR + File.separator + username + File.separator + "private.key";
        String publicKey = USERS_LOCAL_DIR + File.separator + username + File.separator + "public.key";
        List<String> fileContent = mergeFilesFromSeparateDir(userDir, privateKey, publicKey);
        String downloadFolder = USERS_LOCAL_DIR + File.separator + username + File.separator + "Downloads";
        String downloadFile = downloadFolder + File.separator + fileName;
        Path downloadFilePath = Paths.get(downloadFile);
        Files.createFile(downloadFilePath);
        boolean start = true;
        for (String line : fileContent) {
            if (!start) {
                Files.writeString(downloadFilePath, "\n", StandardOpenOption.APPEND);
            } else
                start = false;
            Files.writeString(downloadFilePath, line, StandardOpenOption.APPEND);
        }
    }

    public static List<String> showAllFilesNames(String username) {
        LinkedList<String> listNames = new LinkedList<>();
        String userDirName = USERS_DIR + File.separator + username;
        File userDir = new File(userDirName);

        File[] allFolders = userDir.listFiles();
        for (File file : allFolders) {
            if (file.isDirectory()) {
                String folderName = file.getName();
                String fileName = folderName.replace(REPLACEMENT_CHAR, '.');
                listNames.add(fileName);
            }
        }
        return listNames;

    }

    public static List<List<String>> splitFile(String file) {
        Random random = new Random();
        int numFiles = random.nextInt(12) + 4;
        int linesPerFile = 0;
        List<List<String>> splittedFileLines = new LinkedList<>();
        List<String> fileData = null;

        try {
            fileData = Files.readAllLines(Paths.get(file));
        } catch (IOException e) {

            e.printStackTrace();
        }

        int fileDataSize = fileData.size();
        linesPerFile = fileDataSize / numFiles;

        if (linesPerFile == 0)
            linesPerFile++;

        for (int i = 0; i < numFiles; i++)
            splittedFileLines.add(new LinkedList<String>());

        int currentLine = 0;

        for (int i = 0; i < numFiles; i++) {
            for (int j = 0; j < linesPerFile; j++) {
                if (currentLine < fileDataSize) {
                    splittedFileLines.get(i).add(fileData.get(currentLine));
                    currentLine++;
                } else
                    break;
            }
        }

        for (int i = currentLine; i < fileDataSize; i++)
            splittedFileLines.get(splittedFileLines.size() - 1).add(fileData.get(i));

        return splittedFileLines;
    }

    public static void createEncryptedAndSignedFilesInSeparateDir(List<List<String>> listOfFilesContenet,
                                                                  String destDir,
                                                                  String privateKeyFile, String publicKeyFile) {

        for (int i = 0; i < listOfFilesContenet.size(); i++) {
            try {
                String dir = destDir + File.separator + i;
                String file = dir + File.separator + "data.txt";
                Path filePath = Paths.get(file);
                Files.createDirectories(Paths.get(dir));
                Files.createFile(filePath);
                boolean start = true;
                for (String line : listOfFilesContenet.get(i)) {
                    if (!start)
                        Files.writeString(filePath, "\n", StandardOpenOption.APPEND);
                    else
                        start = false;
                    Files.writeString(filePath, line, StandardOpenOption.APPEND);
                }

                OpenSSL.digestSHA256WithRSA(dir, file, privateKeyFile);
                String encFile = dir + File.separator + "data.enc";

                OpenSSL.fileEncryptionRSA(file, encFile, publicKeyFile);
                Files.delete(filePath);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public static List<String> mergeFilesFromSeparateDir(String dir, String privateKeyFile, String publicKeyFile)
            throws Exception {
        List<String> fileContent = new LinkedList<>();

        int dirNum = 0;

        while (Files.exists(Paths.get(dir + File.separator + dirNum))) {
            String innerDir = dir + File.separator + dirNum;
            String content = OpenSSL.fileDecryptionRSA(innerDir + File.separator + "data.enc",
                    privateKeyFile);

            String newFile = innerDir + File.separator + ".file.txt";
            Path newFilePath = Paths.get(newFile);
            try {
                Files.createFile(newFilePath);
                Files.writeString(newFilePath, content);

                if (OpenSSL.checkSignature(publicKeyFile, innerDir + File.separator + "signature.txt", newFile)) {
                    if (!content.trim().isEmpty())
                        fileContent.add(content);
                } else
                    throw new Exception("The file (" + newFile + ") is corrupted!!!");
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                Files.delete(newFilePath);
            }

            dirNum++;
        }

        return fileContent;
    }

    private static String generateRandomKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}