package com.googlecode.paradox.metadata;

import java.io.File;
import java.util.List;

/**
 * Paradox view config
 *
 * @author Leonardo Alves da Costa
 * @since 03/12/2009
 * @version 1.0
 */
public class ParadoxView extends ParadoxDataFile {

    private boolean valid = false;
    private List<ParadoxField> fieldsOrder;
    private List<ParadoxField> fieldsSort;

    public ParadoxView(final File file, final String name) {
        super(file, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets if this view is valid.
     * 
     * @param valid the valid to set.
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the fieldsOrder
     */
    public List<ParadoxField> getFieldsOrder() {
        return fieldsOrder;
    }

    /**
     * @param fieldsOrder the fieldsOrder to set
     */
    public void setFieldsOrder(List<ParadoxField> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * @return the fieldsSort
     */
    public List<ParadoxField> getFieldsSort() {
        return fieldsSort;
    }

    /**
     * @param fieldsSort the fieldsSort to set
     */
    public void setFieldsSort(List<ParadoxField> fieldsSort) {
        this.fieldsSort = fieldsSort;
    }
}
