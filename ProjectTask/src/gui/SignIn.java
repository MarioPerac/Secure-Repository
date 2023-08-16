package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import openssl.OpenSSL;

public class SignIn {

    private static String USERS_DIR = "users";

    public static boolean checkUser(String certificate, String username, String password) throws Exception {

        if (OpenSSL.checkCertValidity(certificate)) {
            if (checkPassword(username, password)) {
                return true;
            } else
                throw new Exception("Incorrect password or username.");

        } else
            throw new Exception("Certificate is not valid.");
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
}
