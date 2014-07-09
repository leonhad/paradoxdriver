package org.paradox.data.table.value;

public abstract class AbstractFieldValue<T> {

    protected final T value;

    public AbstractFieldValue(final T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
