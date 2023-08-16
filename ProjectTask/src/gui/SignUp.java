package gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import openssl.OpenSSL;
import user.User;

public class SignUp {

    private static String USERS_LOCAL_DIR = "UsersLocalRepo";
    private static String USERS_DIR = "users";

    public static void addUser(User user) {
        String privateKey = createUserLocalDirWithKey(user.getUsername());
        String userDir = createUserDir(user.getUsername());
        OpenSSL.createSignedCertificate(user, privateKey);
        String passwd = OpenSSL.passwdSHA256(user.getPassword());

        String passFile = userDir + File.separator + "pass.txt";

        try {
            Files.writeString(Paths.get(passFile), passwd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createUserLocalDirWithKey(String username) {
        String userDir = USERS_LOCAL_DIR + File.separator + username;
        try {
            Files.createDirectories(Paths.get(userDir));
            Files.createDirectories(Paths.get(userDir + File.separator + "Downloads"));

        } catch (IOException e) {

            e.printStackTrace();
        }
        String privateKey = OpenSSL.generateKeyPairRSA(userDir);
        OpenSSL.publicKeyExtraction(userDir, privateKey);
        return privateKey;

    }

    private static String createUserDir(String username) {
        String userDir = USERS_DIR + File.separator + username;
        try {
            Files.createDirectories(Paths.get(userDir));

        } catch (IOException e) {

            e.printStackTrace();
        }

        return userDir;
    }
}
