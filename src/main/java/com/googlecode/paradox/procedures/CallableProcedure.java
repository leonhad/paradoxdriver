package com.googlecode.paradox.procedures;

import java.util.List;

import com.googlecode.paradox.metadata.ParadoxField;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;

/**
 *
 * @author Leonardo Alves da Costa
 */
public abstract class CallableProcedure {

    public abstract String getName();

    public abstract String getRemarks();

    public List<ParadoxField> getCols() {
        final ArrayList<ParadoxField> ret = new ArrayList<ParadoxField>();

        final ParadoxField field = new ParadoxField();
        field.setName("field");
        field.setType((byte)0xC);
        ret.add(field);

        return ret;
    }

    public int getReturnType() {
        return DatabaseMetaData.procedureReturnsResult;
    }
}
