package org.paradox.data.table.value;

public class BooleanValue extends AbstractFieldValue<Boolean> {

    public BooleanValue(final Boolean value) {
        super(value);
    }

    public boolean getBoolean() {
        if (value == null) {
            return false;
        }
        return value;
    }
}
