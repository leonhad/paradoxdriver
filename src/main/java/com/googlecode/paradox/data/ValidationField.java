package com.googlecode.paradox.data;

import com.googlecode.paradox.results.ParadoxType;

public class ValidationField {

    private int position;

    private String name;

    private String mask;

    private Object defaultValue;

    private ParadoxType type;

    private int fieldSize;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ParadoxType getType() {
        return type;
    }

    public void setType(ParadoxType type) {
        this.type = type;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }
}
