package user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class User implements Serializable {
    private String username;
    transient private String password;

    private String commonName;
    private String email;
    private String country = "BA";
    private String provinceName = "RS";
    private String organizationName = "Elektrotehnicki fakultet";
    private String location = "Banja Luka";
    private String organizationalUnitName = "CA_Mario Perac";

    public User(String username, String password, String commonName, String email) {
        this.username = username;
        this.password = password;
        this.commonName = commonName;
        this.email = email;
    }

    public static void serialize(User user, String usersDir) {
        String userDir = usersDir + File.separator + user.getUsername();
        String userFile = userDir + File.separator + user.getUsername() + ".ser";
        Path userFilePath = Paths.get(userDir);
        ObjectOutputStream oos = null;
        try {
            if (!Files.exists(userFilePath))
                Files.createDirectory(userFilePath);

            oos = new ObjectOutputStream(new FileOutputStream(userFile));
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static User deserialize(String username, String usersDir) {
        String userFile = usersDir + File.separator + username + File.separator + username + ".ser";
        User user = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
            user = (User) ois.readObject();
        } catch (Exception e) {
            System.err.println(e);
        }

        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getLocation() {
        return location;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }
}
