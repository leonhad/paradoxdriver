package org.paradox.data.table.value;

public class AbstractNumericValue extends AbstractFieldValue<Number> {

    public AbstractNumericValue(final Number value) {
        super(value);
    }

    public byte getByte() {
        if (value == null) {
            return 0;
        }
        return value.byteValue();
    }

    public short getShort() {
        if (value == null) {
            return 0;
        }
        return value.shortValue();
    }

    public int getInt() {
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    public long getLong() {
        if (value == null) {
            return 0;
        }
        return value.longValue();
    }

    public float getFloat() {
        if (value == null) {
            return 0f;
        }
        return value.floatValue();
    }

    public double getDouble() {
        if (value == null) {
            return 0d;
        }
        return value.doubleValue();
    }
}
