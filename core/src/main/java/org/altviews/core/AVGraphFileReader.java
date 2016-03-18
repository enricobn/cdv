package org.altviews.core;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by enrico on 3/9/16.
 */
public class AVGraphFileReader {

    public AVGraph read(InputStream is) throws IOException {
        final List<String> lines = IOUtils.readLines(is, "UTF-8");
        final Set<AVModule> modules = new HashSet<>();
        for (String line : lines) {
            if (line.trim().length() > 0) {
                modules.add(new AVModuleImpl(line.trim()));
            }
        }

        return new AVGraph() {
            @Override
            public Set<AVModule> getModules() {
                return modules;
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

        };
    }
}
