package ru.ifmo.zakharvoit.extratask1.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class StreamUtil {
    public static String inputStreamToString(InputStream stream) throws IOException {
        return new String(inputStreamToByteArray(stream));
    }

    public static byte[] inputStreamToByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int r = stream.read(buffer);
            if (r == -1) break;
            out.write(buffer, 0, r);
        }
        return out.toByteArray();
    }
}
