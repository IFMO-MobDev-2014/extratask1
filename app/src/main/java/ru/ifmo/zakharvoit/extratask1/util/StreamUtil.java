package ru.ifmo.zakharvoit.extratask1.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class StreamUtil {
    public static String inputStreamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        stream.close();
        return builder.toString();
    }
}
