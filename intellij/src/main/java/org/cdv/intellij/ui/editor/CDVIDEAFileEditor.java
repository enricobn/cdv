package org.cdv.intellij.ui.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.SettingsSavingComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.*;
import org.apache.commons.io.IOUtils;
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
import java.io.*;
import java.nio.charset.Charset;
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
    private volatile boolean loading;
    private volatile boolean saving;
    private volatile boolean avoidWriteOnDisk;
    private MyVirtualFileAdapter virtualFileListener;
    private Document document;
    private MyDocumentAdapter documentListener;

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
        loadDocument(virtualFile);
    }

    private void loadDocument(VirtualFile virtualFile) {
        document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (document == null) {
            throw new IllegalStateException("Cannot find document for " + virtualFile);
        }

        documentListener = new MyDocumentAdapter();
        document.addDocumentListener(documentListener);

        virtualFileListener = new MyVirtualFileAdapter();
        VirtualFileManager.getInstance().addVirtualFileListener(virtualFileListener);

    }

    public void save() {
//        HintManager.getInstance().showHint(panel, new RelativePoint(panel, new Point(10,10)),
//                HintManager.HIDE_BY_ESCAPE, 10);

    }

    private void saveDocument() {
            WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                @Override
                public void run() {
                    CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                writer.write(panel.getGraph(), os);
                                os.close();
                                document.setText(os.toString("UTF-8"));
//                                saving = true;
//                                try {
//                                    FileDocumentManager.getInstance().saveDocument(document);
//                                } finally {
//                                    saving = false;
//                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, "save", CDVIDEAFileEditor.this);
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

                    try (InputStream inputStream = new ByteArrayInputStream(document.getText().getBytes(Charset.forName("UTF-8")))) {
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
        return "Class dependency view";
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
                        saveDocument();
                    }
                }

                @Override
                public void onRemove(CDVModule module) {
                    if (!loading) {
                        saveDocument();
                    }
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
        if (virtualFileListener != null) {
            VirtualFileManager.getInstance().removeVirtualFileListener(virtualFileListener);
        }

        if (document != null) {
            document.removeDocumentListener(documentListener);
        }
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

    private class MyVirtualFileAdapter extends VirtualFileAdapter {
        @Override
        public void contentsChanged(@NotNull VirtualFileEvent event) {
            if (!saving && event.getFile().equals(virtualFile) &&
                    (event.getRequestor() == null || !(event.getRequestor() instanceof CDVIDEAFileEditor))) {
                try (final InputStream inputStream = virtualFile.getInputStream()) {
                    final String text = IOUtils.toString(inputStream, "UTF-8");
                    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                                @Override
                                public void run() {
                            CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                                @Override
                                public void run() {
                                    avoidWriteOnDisk = true;
                                    try {
                                        document.setText(text);
//                                        loadFile();
                                    } finally {
                                        avoidWriteOnDisk = false;
                                    }
                                }
                            }, "save", CDVIDEAFileEditor.this);
                                }
                        }
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

//                loadDocument(virtualFile);

            }
        }

        @Override
        public void fileDeleted(@NotNull VirtualFileEvent event) {

        }

        @Override
        public void fileMoved(@NotNull VirtualFileMoveEvent event) {

        }
    }

    private class MyDocumentAdapter extends DocumentAdapter {
        @Override
        public void documentChanged(DocumentEvent e) {
            if (!avoidWriteOnDisk) {
                saving = true;
                FileDocumentManager.getInstance().saveDocument(document);

                // On undo, the document change can be invoked before the effective changes to Psi classes,
                // so I must defer loading file.
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        loadFile();
                        saving = false;
                    }
                });
            }
        }
    }
}
