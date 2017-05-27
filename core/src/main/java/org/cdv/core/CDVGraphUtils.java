package org.cdv.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by enrico on 5/27/17.
 */
public class CDVGraphUtils {

    public static String toString(CDVGraph newGraph) throws Exception {
        CDVGraphFileWriter writer = new CDVGraphFileWriter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            writer.write(newGraph, os);
        } finally {
            os.close();
        }
        return os.toString("UTF-8");
    }

    public static CDVGraph fromString(String text) throws Exception {
        CDVGraphFileReader reader = new CDVGraphFileReader();

        try (InputStream inputStream = new ByteArrayInputStream(text.getBytes(Charset.forName("UTF-8")))) {
            return reader.read(inputStream);
        }
    }
}
