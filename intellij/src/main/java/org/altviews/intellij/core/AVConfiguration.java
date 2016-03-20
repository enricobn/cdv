package org.altviews.intellij.core;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by enrico on 3/19/16.
 */
//@Storage(id="altViews", file = StoragePathMacros.APP_CONFIG + "/altViews.xml")
@State(name = "AVConfiguration", storages = {@com.intellij.openapi.components.Storage(file = "$WORKSPACE_FILE$")})
public class AVConfiguration implements PersistentStateComponent<AVConfiguration.State> {
    public static class State {
        private List<String> includes = new ArrayList<>();
        private List<String> excludes = new ArrayList<>();
        private List<Pattern> includesPatterns = new ArrayList<>();
        private List<Pattern> excludesPatterns = new ArrayList<>();

        public List<String> getIncludes() {
            return includes;
        }

        public void setIncludes(List<String> includes) {
            this.includes = includes;
            includesPatterns.clear();
            for (String include : includes) {
                includesPatterns.add(Pattern.compile(include));
            }
        }

        public List<String> getExcludes() {
            return excludes;
        }

        public void setExcludes(List<String> excludes) {
            this.excludes = excludes;
            excludesPatterns.clear();
            for (String include : excludes) {
                excludesPatterns.add(Pattern.compile(include));
            }
        }

        public boolean isValid(String fullClassName) {
            boolean valid;
            if (includesPatterns.isEmpty()) {
                valid = true;
            } else {
                valid = match(fullClassName, includesPatterns);
            }

            if (!valid) {
                return false;
            }

            if (excludesPatterns.isEmpty()) {
                valid = true;
            } else {
                valid = !match(fullClassName, excludesPatterns);
            }
            return valid;
        }

        private static boolean match(String value, Collection<Pattern> patterns) {
            for (Pattern pattern : patterns) {
                final Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    return true;
                }
            }
            return false;
        }

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
