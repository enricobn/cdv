package org.altviews.core;

/**
 * Created by enrico on 3/4/16.
 */
public interface AVModuleProvider {

    AVModule getModule(String className);

}
