package com.googlecode.paradox.procedures;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.paradox.metadata.ParadoxField;

/**
 * Abstract class used to create any callable procedure.
 *
 * @author Leonardo Alves da Costa
 * @since 09/12/2014.
 * @version 1.1
 */
public abstract class CallableProcedure {

    /**
     * Get the procedure columns.
     *
     * @return the procedure columns.
     */
    public List<ParadoxField> getCols() {
        final ArrayList<ParadoxField> ret = new ArrayList<>();

        final ParadoxField field = new ParadoxField();
        field.setName("field");
        field.setType((byte) 0xC);
        ret.add(field);

        return ret;
    }

    /**
     * Gets the procedure name.
     *
     * @return the procedure name.
     */
    public abstract String getName();

    /**
     * Gets the procedure description.
     *
     * @return the procedure description.
     */
    public abstract String getRemarks();

    /**
     * Gets the return type.
     *
     * @return the return type.
     */
    public int getReturnType() {
        return DatabaseMetaData.procedureReturnsResult;
    }
}
