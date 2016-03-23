package org.altviews.core;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by enrico on 3/23/16.
 */
public class AVGraphFileWriterTest {

    @Test
    public void testWriteRead() throws Exception {
        AVGraphFileWriter writer = new AVGraphFileWriter();

        final Set<AVModule> modules = new HashSet<>();
        modules.add(new AVModuleImpl("org.alviews.Module1"));
        modules.add(new AVModuleImpl("org.alviews.ui.Module2"));

        AVGraph graph = new AVGraph() {
            @Override
            public Set<AVModule> getModules() {
                return modules;
            }
        };

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            writer.write(graph, os);

            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

            AVGraphFileReader reader = new AVGraphFileReader();
            final AVGraph graphRed = reader.read(is);

            assertEquals(modules, graphRed.getModules());
        }
    }
}