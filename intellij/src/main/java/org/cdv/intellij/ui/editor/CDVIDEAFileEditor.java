package org.cdv.intellij.ui.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.SettingsSavingComponent;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.*;
import org.cdv.core.CDVGraph;
import org.cdv.core.CDVGraphFileReader;
import org.cdv.core.CDVGraphFileWriter;
import org.cdv.core.CDVModule;
import org.cdv.intellij.core.CDVJavIdeaModuleTypeProvider;
import org.cdv.intellij.core.CDVJavaIDEADependenciesFinder;
import org.cdv.intellij.core.CDVJavaIDEAModuleNavigator;
import org.cdv.intellij.core.CDVJavaIDEANamespaceNavigator;
import org.cdv.intellij.ui.CDVIDEAFileSaveChooser;
import org.cdv.intellij.ui.CDVJavaIDEAModuleChooser;
import org.cdv.swing.CDVComponentListener;
import org.cdv.swing.CDVSwingEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by enrico on 3/8/16.
 */
public class CDVIDEAFileEditor implements FileEditor,SettingsSavingComponent {
    private final Project project;
    private final VirtualFile virtualFile;
    private final CDVSwingEditor panel;
    private final CDVGraphFileWriter writer;
    private final AtomicBoolean loaded = new AtomicBoolean(false);
    private boolean loading;

    public CDVIDEAFileEditor(final Project project, final VirtualFile virtualFile) {
        this.project = project;
        this.virtualFile = virtualFile;

        this.writer = new CDVGraphFileWriter();

        this.panel = new CDVSwingEditor(
                new CDVJavaIDEAModuleChooser(project),
                new CDVJavaIDEAModuleNavigator(project),
                new CDVJavaIDEADependenciesFinder(project),
                new CDVJavIdeaModuleTypeProvider(project),
                new CDVIDEAFileSaveChooser(project),
                new CDVJavaIDEANamespaceNavigator(project),
                false);
    }

    public void save() {
//        HintManager.getInstance().showHint(panel, new RelativePoint(panel, new Point(10,10)),
//                HintManager.HIDE_BY_ESCAPE, 10);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try(OutputStream os = virtualFile.getOutputStream(CDVIDEAFileEditor.this)) {
                    writer.write(panel.getGraph(), os);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void loadFile() {
        // it's run when indexes are ready
        DumbService.getInstance(project).runWhenSmart(new Runnable() {
            @Override
            public void run() {
                try {
                    loading = true;
                    panel.clear();
                    CDVGraphFileReader reader = new CDVGraphFileReader();
                    try (final InputStream inputStream = virtualFile.getInputStream()) {
                        final CDVGraph graph = reader.read(inputStream);
                        panel.addModules(graph.getModules());
                    } catch (Exception e) {
                        // TODO something better in Intellij?
                        throw new RuntimeException(e);
                    }
                } finally {
                    loading = false;
                }
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
        // TODO
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
            panel.addListener(new CDVComponentListener() {
                @Override
                public void onAdd(CDVModule module) {
                    if (!loading) {
                        save();
                    }
                }

                @Override
                public void onRemove(CDVModule module) {
                    if (!loading) {
                        save();
                    }
                }
            });
            VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
                @Override
                public void contentsChanged(@NotNull VirtualFileEvent event) {
                    if (event.getFile().equals(virtualFile) && event.isFromRefresh() &&
                            event.getRequestor() != CDVIDEAFileEditor.this) {
                        loadFile();
                    }
                }

                @Override
                public void fileDeleted(@NotNull VirtualFileEvent event) {

                }

                @Override
                public void fileMoved(@NotNull VirtualFileMoveEvent event) {

                }
            });
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
