package org.cdv.intellij.core;

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
public final class CDVConfiguration implements PersistentStateComponent<CDVConfiguration.State> {
    public static class State {
        private final List<Pattern> includesPatterns = new ArrayList<>();
        private final List<Pattern> excludesPatterns = new ArrayList<>();
        private List<String> includes = new ArrayList<>();
        private List<String> excludes = new ArrayList<>();

        public State() {
            List<String> excludes = new ArrayList<>();
            excludes.add("^java.");
            excludes.add("^javax.");
            setExcludes(excludes);
        }

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

    public static CDVConfiguration getConfig(Project project) {
        return ServiceManager.getService(project, CDVConfiguration.class);
    }
}
