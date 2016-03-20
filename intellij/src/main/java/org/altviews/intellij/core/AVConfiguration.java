package org.altviews.intellij.core;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by enrico on 3/19/16.
 */
//@Storage(id="altViews", file = StoragePathMacros.APP_CONFIG + "/altViews.xml")
@State(name = "AVConfiguration", storages = {@com.intellij.openapi.components.Storage(file = "$WORKSPACE_FILE$")})
public class AVConfiguration implements PersistentStateComponent<AVConfiguration.State> {
    public static class State {
        public java.util.List<String> includes = new ArrayList<>();
        public java.util.List<String> excludes = new ArrayList<>();
    }

    private State state = new State();

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(State state) {
        this.state = state;
    }

    public static AVConfiguration getConfig(Project project) {
        return ServiceManager.getService(project, AVConfiguration.class);
    }
}
