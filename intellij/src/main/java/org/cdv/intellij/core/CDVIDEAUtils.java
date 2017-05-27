package org.cdv.intellij.core;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;

/**
 * Created by enrico on 5/27/17.
 */
public class CDVIDEAUtils {

    public static void runUndoableWriteActionCommand(final Project project, final Runnable runnable, final String s, final Object o) {
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, runnable, s, o);
            }
        });
    }

}
