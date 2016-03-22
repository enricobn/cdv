package org.altviews.intellij.ui.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.SettingsSavingComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.altviews.core.AVGraph;
import org.altviews.core.AVGraphFileReader;
import org.altviews.core.AVGraphFileWriter;
import org.altviews.core.AVModule;
import org.altviews.intellij.core.AVJavIdeaModuleTypeProvider;
import org.altviews.intellij.core.AVJavaIDEADependenciesFinder;
import org.altviews.intellij.core.AVJavaIDEAModuleNavigator;
import org.altviews.intellij.core.AVJavaIDEANamespaceNavigator;
import org.altviews.intellij.ui.AVIDEAFileSaveChooser;
import org.altviews.intellij.ui.AVJavaIDEAModuleChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by enrico on 3/8/16.
 */
public class AVIDEAFileEditor implements FileEditor,SettingsSavingComponent {
    private final VirtualFile virtualFile;
    private final AVSwingEditor panel;
    private final AVGraphFileWriter writer;
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    public AVIDEAFileEditor(final Project project, final VirtualFile virtualFile) {
        this.virtualFile = virtualFile;

        this.writer = new AVGraphFileWriter();

        this.panel = new AVSwingEditor(
                new AVJavaIDEAModuleChooser(project),
                new AVJavaIDEAModuleNavigator(project),
                new AVJavaIDEADependenciesFinder(project),
                new AVJavIdeaModuleTypeProvider(project),
                new AVIDEAFileSaveChooser(project),
                new AVJavaIDEANamespaceNavigator(project),
                false);
    }

    public void save() {
//        HintManager.getInstance().showHint(panel, new RelativePoint(panel, new Point(10,10)),
//                HintManager.HIDE_BY_ESCAPE, 10);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    writer.write(panel.getGraph(), virtualFile.getOutputStream(null));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void loadFile() {
        AVGraphFileReader reader = new AVGraphFileReader();
        try (final InputStream inputStream = virtualFile.getInputStream()) {
            final AVGraph graph = reader.read(inputStream);
            panel.addModules(graph.getModules());
        } catch (IOException e) {
            // TODO something better in Intellij?
            throw new RuntimeException(e);
        }
        panel.addListener(new AVFileEditorComponentListener() {
            @Override
            public void onAdd(AVModule module) {
                save();
            }

            @Override
            public void onRemove(AVModule module) {
                save();
            }
        });
    }

    @NotNull
    @Override
    public synchronized JComponent getComponent() {
        return panel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return getComponent();
    }

    @NotNull
    @Override
    public String getName() {
        return "Alt view";
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel fileEditorStateLevel) {
        return new AVEditorState();
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void selectNotify() {
        if (loaded.compareAndSet(false, true)) {
            loadFile();
        }
    }

    @Override
    public void deselectNotify() {

    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

    }

    public static class AVEditorState implements FileEditorState {

        public AVEditorState() {
        }

        @Override
        public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
            return false;
        }
    }
}
