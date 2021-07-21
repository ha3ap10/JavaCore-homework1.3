import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    static StringBuilder sb = new StringBuilder();
    static String dirGames = "Games";


    public static void main(String[] args) {
        //Установка
        ArrayList<String> dirsMain = new ArrayList<>(Arrays.asList("src", "res", "savegames", "temp"));
        ArrayList<String> dirsSrc = new ArrayList<>(Arrays.asList("main", "test"));
        ArrayList<String> dirsRes = new ArrayList<>(Arrays.asList("drawables", "vectors", "icons"));
        ArrayList<String> filesMain = new ArrayList<>(Arrays.asList("Main.java", "Utils.java"));
        ArrayList<String> filesTemp = new ArrayList<>(Arrays.asList("temp.txt"));

        //создать корневой каталог
        new File(dirGames).mkdir();

        makeDirs(dirGames, dirsMain);
        makeDirs(dirGames + "//src", dirsSrc);
        makeDirs(dirGames + "//res", dirsRes);
        createFiles(dirGames + "//src//main", filesMain);
        createFiles(dirGames + "//temp", filesTemp);
        saveLogFile(dirGames + "//temp//temp.txt");

        //Сохранение
        GameProgress gameProgress1 = new GameProgress(94, 10, 2, 254.32);
        GameProgress gameProgress2 = new GameProgress(82, 11, 3, 328.58);
        GameProgress gameProgress3 = new GameProgress(100, 14, 5, 512.65);

        String savePath = dirGames + "//savegames";
        saveGame(savePath + "//save1.dat", gameProgress1);
        saveGame(savePath + "//save2.dat", gameProgress2);
        saveGame(savePath + "//save3.dat", gameProgress3);


        ArrayList<String> saveFiles = new ArrayList<>();
        saveFiles.add(savePath + "//save1.dat");
        saveFiles.add(savePath + "//save2.dat");
        saveFiles.add(savePath + "//save3.dat");

        zipFiles(savePath + "//saves.zip", saveFiles);

        //Загрузка
//        openZip(savePath + "//saves.zip", savePath);
//        GameProgress loadedProgress = openProgress(savePath + "//save2.dat");
//        System.out.println(loadedProgress);

    }

    private static void saveGame(String save, GameProgress gameProgress) {
        try (FileOutputStream fileOutStream = new FileOutputStream(save);
             ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream)) {
            objectOutStream.writeObject(gameProgress);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void zipFiles(String zipPath, ArrayList<String> filesList) {
        try (ZipOutputStream zipOutStream = new ZipOutputStream(new
                FileOutputStream(zipPath))) {

            for (String filePath : filesList) {
                int i = filePath.lastIndexOf("/") + 1;
                String fileName = filePath.substring(i);

                try (FileInputStream fileInStream = new FileInputStream(filePath)) {
                    ZipEntry entry = new ZipEntry(fileName);
                    zipOutStream.putNextEntry(entry);
                    byte[] buffer = new byte[fileInStream.available()];
                    fileInStream.read(buffer);
                    zipOutStream.write(buffer);
                    zipOutStream.closeEntry();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        for (String saveFile : filesList) {
            new File(saveFile).delete();
        }
    }

    private static void openZip(String zipPath, String destPath) {
        try (ZipInputStream zipInStream = new ZipInputStream(new
                FileInputStream(zipPath))) {

            ZipEntry entry;
            String fileName;

            while ((entry = zipInStream.getNextEntry()) != null) {
                fileName = entry.getName();

                FileOutputStream fileOutStream = new FileOutputStream(destPath + "//" + fileName);
                for (int c = zipInStream.read(); c != -1; c = zipInStream.read()) {
                    fileOutStream.write(c);
                }
                fileOutStream.flush();
                zipInStream.closeEntry();
                fileOutStream.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void makeDirs(String dirParent, ArrayList<String> dirs) {
        for (String dirName : dirs) {
            sb
                    .append(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date()))
                    .append(" Директория ")
                    .append(dirName);

            if (new File(dirParent, dirName).mkdir()) {
                sb.append(" создана в директории ");
            } else {
                sb.append(" уже существует в директории ");
            }

            sb
                    .append(dirParent)
                    .append("\n");
        }
    }

    private static void createFiles(String dirParent, ArrayList<String> fileNames) {
        for (String fileName : fileNames) {
            try {
                sb
                        .append("Файл ")
                        .append(fileName);

                if (new File(dirParent, fileName).createNewFile()) {
                    sb.append(" создан в директории ");
                } else {
                    sb.append(" уже существует в директории ");
                }

                sb
                        .append(dirParent)
                        .append("\n");

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void saveLogFile(String logFileName) {
        try (FileWriter fw = new FileWriter(logFileName, true)) {
            fw.write(sb.toString() + "\n");
            System.out.println("Лог записан в файл: " + logFileName);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static GameProgress openProgress(String savePath) {
        GameProgress gameProgress = null;
        try (FileInputStream fileInStream = new FileInputStream(savePath);
             ObjectInputStream objInStream = new ObjectInputStream(fileInStream)) {
            gameProgress = (GameProgress) objInStream.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return gameProgress;
    }
}
