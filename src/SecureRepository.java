import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SecureRepository {

    public static List<List<String>> splitFile(String file) {
        Random random = new Random();
        int numFiles = random.nextInt(12) + 4;
        int linesPerFile = 0;
        List<List<String>> splitedFileLines = new LinkedList<>();
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
            splitedFileLines.add(new LinkedList<String>());

        int currentLine = 0;

        for (int i = 0; i < numFiles; i++) {
            for (int j = 0; j < linesPerFile; j++) {
                if (currentLine < fileDataSize) {
                    splitedFileLines.get(i).add(fileData.get(currentLine));
                    currentLine++;
                } else
                    break;
            }
        }

        for (int i = currentLine; i < fileDataSize; i++)
            splitedFileLines.get(splitedFileLines.size() - 1).add(fileData.get(i));

        return splitedFileLines;
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
                String encFile = dir + File.separator + "data.bin";
                OpenSSL.fileEncryption(file, encFile, publicKeyFile);
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
            String content = OpenSSL.fileDecryption(innerDir + File.separator + "data.bin",
                    privateKeyFile);

            String newFile = innerDir + File.separator + ".file.txt";
            Path newFilePath = Paths.get(newFile);
            try {
                Files.createFile(newFilePath);
                Files.writeString(newFilePath, content);

                if (OpenSSL.checkSignature(publicKeyFile, innerDir + File.separator + "signature.txt", newFile)) {
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

}