package elab;

import core.SimpleAnnotation;
import core.SimpleClass;
import core.TypeInterface;
import core.TypedClass;
import test.ClassTest;

/**
 * Created by enrico on 3/14/16.
 */
public class Elaboration {

    @SimpleAnnotation
    public void main() {
        ClassTest clz;
        new Runnable() {
            @Override
            public void run() {
                        for (TypeInterface<SimpleClass> a = new TypedClass<>(null); true; ) {

                        }
            }
        };

    }


}


