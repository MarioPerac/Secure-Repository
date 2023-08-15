import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class OpenSSL {

    private static String CONFIG_PATH = "CA" + File.separator + "openssl.cnf";
    private static String REQ_PATH = "CA" + File.separator + "requests";
    private static String CERT_PATH = "CA" + File.separator + "certs";
    private static String DAYS = "182";

    private static String createRequest(User user, String keyPath) {

        String requestFile = REQ_PATH + File.separator + user.getUsername() + ".csr";

        String[] command = {
                "openssl",
                "req",
                "-new",
                "-config", CONFIG_PATH,
                "-key", keyPath,
                "-out", requestFile,
                "-days", DAYS,
                "-subj",
                ("/CN=" + user.getCommonName() +
                        "/C=" + user.getCountry() +
                        "/ST=" + user.getProvinceName() +
                        "/L=" + user.getLocation() +
                        "/emailAddress=" + user.getEmail() +
                        "/O=" + user.getOrganizationName() +
                        "/OU=" + user.getOrganizationalUnitName())
        };

        try {
            executeCommand(command);

        } catch (Exception e) {
            System.err.println(e);
        }

        return requestFile;
    }

    public static void createSignedCertificate(User user, String keyPath) {

        String requestFile = createRequest(user, keyPath);
        String certificateFile = SignUp.USERS_LOCAL + File.separator + user.getUsername() + File.separator
                + user.getUsername() + ".pem";

        String[] command = {
                "openssl",
                "ca",
                "-config", CONFIG_PATH,
                "-in", requestFile,
                "-out", certificateFile,
                "-batch"
        };

        try {
            executeCommand(command);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void fileEncryption(String file, String encFile, String publicKey) {
        String[] command = {
                "openssl",
                "pkeyutl",
                "-encrypt",
                "-in",
                file,
                "-out",
                encFile,
                "-inkey",
                publicKey,
                "-pubin"
        };

        try {
            executeCommand(command);

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public static String fileDecryption(String file, String privateKey) {
        String result = null;
        String[] command = {
                "openssl",
                "pkeyutl",
                "-decrypt",
                "-in",
                file,
                "-inkey",
                privateKey
        };

        try {
            result = executeCommandWithResult(command);

        } catch (Exception e) {
            System.err.println(e);
        }
        return result;
    }

    public static String generateKeyPairRSA(String dir) {
        String privateKey = dir + File.separator + "private.key";
        String[] command = {
                "openssl",
                "genrsa",
                "-out",
                privateKey
        };

        try {
            executeCommand(command);

        } catch (Exception e) {
            System.err.println(e);
        }

        return privateKey;
    }

    public static String publicKeyExtraction(String dir, String privateKey) {
        String publicKey = dir + File.separator + "public.key";
        String[] command = {
                "openssl",
                "rsa",
                "-in",
                privateKey,
                "-pubout",
                "-out",
                publicKey
        };

        try {
            executeCommand(command);

        } catch (Exception e) {
            System.err.println(e);
        }

        return publicKey;
    }

    public static void certificateSuspension(String username) {
        String certFile = CERT_PATH + File.separator + username + ".pem";
        String[] command = {
                "openssl",
                "ca",
                "-revoke",
                certFile,
                "-crl_reason",
                "certificateHold",
                "-config",
                CONFIG_PATH
        };

        try {
            executeCommand(command);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    // To doo
    public static void reactivateCertificate(String username) {

    }

    public static void digestSHA256WithRSA(String digestPath, String filePath, String privateKeyFile) {

        String digestFile = digestPath + File.separator + "signature.txt";
        String[] command = {
                "openssl",
                "dgst",
                "-sha256",
                "-sign", privateKeyFile,
                "-out", digestFile, filePath

        };

        try {
            executeCommand(command);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static boolean checkSignature(String publicKeyFile, String digestFile, String orginalFile) {
        boolean verify = false;
        String result = null;
        String[] command = {
                "openssl",
                "dgst",
                "-sha256",
                "-verify",
                publicKeyFile,
                "-signature",
                digestFile, orginalFile

        };

        try {
            result = executeCommandWithResult(command);
            if ("Verified OK".compareTo(result) == 0)
                verify = true;

        } catch (Exception e) {
            System.err.println(e);
        }

        return verify;
    }

    public static boolean checkCertValidity(String certFile) {

        boolean validity = false;
        String result = null;

        String[] command = {
                "openssl",
                "verify",
                "-CAfile",
                "CA/rootca.pem",
                certFile
        };

        try {
            result = executeCommandWithResult(command);
        } catch (Exception e) {
            System.err.println(e);
        }

        String[] params = result.split(" ");
        if ("OK".compareTo(params[1]) == 0)
            validity = true;

        return validity;
    }

    public static String passwdSHA256(String password) {
        String result = null;

        String[] command = {
                "openssl",
                "passwd",
                "-5",
                password
        };

        try {
            result = executeCommandWithResult(command);
        } catch (Exception e) {
            System.err.println(e);
        }

        return result;
    }

    public static String passwdSHA256WithSalt(String password, String salt) {

        String result = null;
        String[] command = {
                "openssl",
                "passwd",
                "-5",
                "-salt",
                salt,
                password
        };

        try {
            result = executeCommandWithResult(command);

        } catch (Exception e) {
            System.err.println(e);
        }

        return result;
    }

    private static void executeCommand(String[] commandOptions) throws Exception {

        Process process = Runtime.getRuntime().exec(commandOptions);
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            StringBuilder command = new StringBuilder();
            for (String str : commandOptions) {
                command.append(str + " ");

            }
            throw new Exception("Failed to execute command: " + command.toString());
        }
    }

    private static String executeCommandWithResult(String[] commandOptions) throws Exception {

        Process process = Runtime.getRuntime().exec(commandOptions);
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            boolean start = true;
            while ((line = reader.readLine()) != null) {
                if (!start)
                    stringBuilder.append("\n");
                else
                    start = false;

                stringBuilder.append(line);

            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            StringBuilder command = new StringBuilder();
            for (String str : commandOptions) {
                command.append(str + " ");

            }
            throw new Exception("Failed to execute command: " + command.toString());
        }
        return stringBuilder.toString();
    }
}
