package org.paradox.procedures.math;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import org.paradox.metadata.ParadoxField;
import org.paradox.procedures.CallableProcedure;

/**
 *
 * @author 72330554168
 */
public class Averange implements CallableProcedure {

    public String getName() {
        return "averange";
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

    public String getRemarks() {
        return "Returns the avarange values.";
    }

}
