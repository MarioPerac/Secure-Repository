package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.List;

import openssl.OpenSSL;

public class SignIn {

    private static String USERS_DIR = "users";
    private static String USERS_LOCAL_DIR = "UsersLocalRepo";

    public static boolean checkUser(String certificate, String username, String password) throws Exception {

        if (OpenSSL.checkCertValidity(certificate)) {
            if (checkPassword(username, password)) {
                return true;
            } else
                throw new Exception("Incorrect password or username.");

        } else
            throw new Exception("Certificate is not valid.");
    }

    public static void suspendUserCertificate(String user) {
        String userCertificate = USERS_LOCAL_DIR + File.separator + user + File.separator + user + ".pem";
        OpenSSL.certificateSuspension(userCertificate);
        OpenSSL.generateCRL();
    }

    public static void reactivateUserCertificate(String user) {
        String userCert = USERS_LOCAL_DIR + File.separator + user + File.separator + user + ".pem";
        String userInfo = OpenSSL.getSubjectInfo(userCert);
        String params[] = (userInfo).split(",");
        String userEmail = (params[5].split("=")[1]).trim();

        String index = "CA" + File.separator + "index.txt";
        Path indexPath = Paths.get(index);
        try {
            List<String> fileContent = Files.readAllLines(indexPath);
            Path tmpFile = Paths.get("CA" + File.separator + "tmp.txt");
            Files.createFile(tmpFile);
            boolean start = true;
            for (String line : fileContent) {
                String newLine = line;
                if (line.contains(userEmail)) {
                    String lineParams[] = line.split("\\s+");
                    newLine = "V" + line.substring(1);
                    newLine = newLine.replace(lineParams[2], "");

                }

                Files.writeString(tmpFile, newLine + "\n", StandardOpenOption.APPEND);
            }
            Files.delete(indexPath);
            Files.move(tmpFile, indexPath);
        } catch (Exception e) {
            System.err.println(e);
        }
        OpenSSL.generateCRL();

    }


    public static void removeUserAccount(String user) {
        String userLocalDir = USERS_LOCAL_DIR + File.separator + user;
        String userDir = USERS_DIR + File.separator + user;
        try {
            deleteDirectory(Paths.get(userLocalDir));
            deleteDirectory(Paths.get(userDir));
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private static boolean checkPassword(String username, String password) {

        String userDir = USERS_DIR + File.separator + username;
        String passFile = userDir + File.separator + "pass.txt";
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(passFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String userPasswordSHA256 = stringBuilder.toString();

        String params[] = userPasswordSHA256.split("\\$");
        String salt = params[2];

        String passwSHA265 = OpenSSL.passwdSHA256WithSalt(password, salt);

        if (userPasswordSHA256.compareTo(passwSHA265) == 0)
            return true;

        return false;
    }

    public static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        } else {
            System.err.println("Directory does not exist.");
        }
    }
}
