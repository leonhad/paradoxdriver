package com.googlecode.paradox.procedures.math;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.procedures.CallableProcedure;

/**
 *
 * @author 72330554168
 */
public class Count implements CallableProcedure {

    public String getName() {
        return "count";
    }

    public String getRemarks() {
        return "Returns the row count";
    }

    public ArrayList<ParadoxField> getCols() {
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
