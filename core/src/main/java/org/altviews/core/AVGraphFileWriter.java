package org.altviews.core;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by enrico on 3/9/16.
 */
public class AVGraphFileWriter {

    public void write(AVGraph graph, OutputStream os) throws IOException {
        List<String> lines = new ArrayList<>();
        for (AVModule module : graph.getModules()) {
            lines.add(module.getFullName());
        }

        Collections.sort(lines);

        try {
            IOUtils.writeLines(lines, "\n", os, "UTF-8");
        } finally {
            os.close();
        }
    }
}
