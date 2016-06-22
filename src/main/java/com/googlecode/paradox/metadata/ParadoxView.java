package com.googlecode.paradox.metadata;

import java.io.File;
import java.util.ArrayList;

/**
 * Paradox view config
 *
 * @author Leonardo Alves da Costa
 * @since 03/12/2009
 * @version 1.0
 */
public class ParadoxView extends ParadoxDataFile {

    private boolean valid = false;
    private ArrayList<ParadoxField> privateFields;
    private ArrayList<ParadoxField> fieldsOrder;
    private ArrayList<ParadoxField> fieldsSort;

    public ParadoxView(final File file, final String name) {
        super(file, name);
    }

    /**
     * @return the valid
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the fields
     */
    @Override
    public ArrayList<ParadoxField> getFields() {
        final ArrayList<ParadoxField> fields = new ArrayList<ParadoxField>();

        for (final ParadoxField field : privateFields) {
            if (field.isChecked()) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * @return the fieldsOrder
     */
    public ArrayList<ParadoxField> getFieldsOrder() {
        return fieldsOrder;
    }

    /**
     * @param fieldsOrder the fieldsOrder to set
     */
    public void setFieldsOrder(ArrayList<ParadoxField> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * @return the fieldsSort
     */
    public ArrayList<ParadoxField> getFieldsSort() {
        return fieldsSort;
    }

    /**
     * @param fieldsSort the fieldsSort to set
     */
    public void setFieldsSort(ArrayList<ParadoxField> fieldsSort) {
        this.fieldsSort = fieldsSort;
    }

    /**
     * @return the privateFields
     */
    public ArrayList<ParadoxField> getPrivateFields() {
        return privateFields;
    }

    /**
     * @param privateFields the privateFields to set
     */
    public void setPrivateFields(ArrayList<ParadoxField> privateFields) {
        this.privateFields = privateFields;
    }
}
