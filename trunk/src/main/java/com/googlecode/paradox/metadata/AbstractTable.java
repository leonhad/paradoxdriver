package com.googlecode.paradox.metadata;

import java.io.File;
import java.util.ArrayList;

/**
 * Defines the paradox table default structure
 *
 * @author Leonardo Alves da Costa
 * @since 03/12/2009
 * @version 1.1
 */
public abstract class AbstractTable {

    private final File file;
    private String name;

    public AbstractTable(final File file, final String name) {
        this.file = file;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public abstract boolean isValid();

    public abstract ArrayList<ParadoxField> getFields();
}
