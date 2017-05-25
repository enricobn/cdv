package core;

/**
 * Created by enrico on 3/14/16.
 */
public class TypedClass<T> implements TypeInterface<T> {
    private final T value;

    public TypedClass(T value) {
        this.value = value;
    }

    @Override
    public T returnValue() {
        return value;
    }
}
