package com.googlecode.paradox.metadata;

import static java.nio.charset.Charset.forName;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 72330554168
 */
public class ParadoxIndex extends ParadoxDataFile {

    private Charset charset = forName("Cp437");
    private String fatherName;
    private List<Short> fieldsOrder;
    private String sortOrderID;

    public ParadoxIndex(final File file, final String name) {
        super(file, name);
    }

    /**
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @return the fatherName
     */
    public String getFatherName() {
        return fatherName;
    }

    /**
     * @return the fieldsOrder
     */
    public List<Short> getFieldsOrder() {
        return fieldsOrder;
    }

    public String getOrder() {
        switch (referencialIntegrity) {
        case 0:
        case 1:
        case 0x20:
        case 0x21:
            return "A";
        case 0x10:
        case 0x11:
        case 0x30:
            return "D";
        default:
            return "A";
        }
    }

    public List<ParadoxField> getPrimaryKeys() {
        final ArrayList<ParadoxField> ret = new ArrayList<>();
        for (int loop = 0; loop < primaryFieldCount; loop++) {
            ret.add(fields.get(loop));
        }
        return ret;
    }

    /**
     * @return the sortOrderID
     */
    public String getSortOrderID() {
        return sortOrderID;
    }

    /**
     * If this table is valid
     *
     * @return true if this table is valid
     */
    @Override
    public boolean isValid() {
        switch (type) {
        case 3:
        case 5:
        case 6:
        case 8:
            return true;
        default:
            return false;
        }
    }

    /**
     * @param charset
     *            the charset to set
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * @param fatherName
     *            the fatherName to set
     */
    public void setFatherName(final String fatherName) {
        this.fatherName = fatherName;
    }

    /**
     * @param fieldsOrder
     *            the fieldsOrder to set
     */
    public void setFieldsOrder(final List<Short> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * @param sortOrderID
     *            the sortOrderID to set
     */
    public void setSortOrderID(final String sortOrderID) {
        this.sortOrderID = sortOrderID;
    }
}
