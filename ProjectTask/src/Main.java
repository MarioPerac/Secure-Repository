import gui.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import openssl.OpenSSL;
import repository.SecureRepository;

public class Main {
    public static void main(String[] args) throws Exception {
        // User user = new User("korisnicko", "lozinka", "Stvarno Ime",
        // "email@mail.com");
        // OpenSSL.createSignedCertificate(user, "UsersLocalRepo/kljuc.key");

        // String passwd = OpenSSL.passwdSHA256(user.getPassword());
        // try {
        // Files.writeString(Paths.get("users/" + user.getUsername() + "/pass.txt"),
        // passwd);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // OpenSSL.digestSHA256WithRSA("UsersLocalRepo", "UsersLocalRepo/file.txt",
        // // "UsersLocalRepo/kljuc.key");
        // SecureRepository.createEncryptedAndSignedFilesInSeparateDir(
        // SecureRepository.splitFile("UsersLocalRepo/file.txt"),
        // "UsersLocalRepo/", "UsersLocalRepo/kljuc.key", "UsersLocalRepo/javni.key");
//        List<String> rez = SecureRepository.mergeFilesFromSeparateDir("UsersLocalRepo",
//                "UsersLocalRepo/kljuc.key",
//                "UsersLocalRepo/javni.key");

        SignInFrame signInFrame = new SignInFrame();
//        OpenSSL.checkCertValidity("/home/mario/Desktop/Treca godina ETF/V semestar/Kriptografija i racunarska zastita/Secure-Repository/ProjectTask/UsersLocalRepo/korisnik1/korisnik1.pem");
    }

}