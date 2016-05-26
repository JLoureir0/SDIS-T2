package pinypon.utils;

import java.io.*;

public class ReadFile {
    public static String fileReaderString(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

    public static byte[] FileInputStreamBytes(String path) throws IOException {
        FileInputStream fileInputStream = null;
        byte[] bytes = null;
        try {
            File file = new File(path);
            fileInputStream = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return bytes;
    }
}
