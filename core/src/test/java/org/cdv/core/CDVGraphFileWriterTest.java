package org.cdv.core;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by enrico on 3/23/16.
 */
public class CDVGraphFileWriterTest {

    @Test
    public void testWriteRead() throws Exception {
        CDVGraphFileWriter writer = new CDVGraphFileWriter();

        final Set<CDVModule> modules = new HashSet<>();
        modules.add(new CDVModuleImpl("org.cdv.Module1"));
        modules.add(new CDVModuleImpl("org.cdv.ui.Module2"));

        CDVGraph graph = new CDVGraph() {
            @Override
            public Set<CDVModule> getModules() {
                return modules;
            }
        };

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            writer.write(graph, os);

            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

            CDVGraphFileReader reader = new CDVGraphFileReader();
            final CDVGraph graphRed = reader.read(is);

            assertEquals(modules, graphRed.getModules());
        }
    }
}