package com.googlecode.paradox.procedures.math;

import java.util.ArrayList;

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.procedures.CallableProcedure;
import java.util.List;

/**
 *
 * @author 72330554168
 */
public class Min extends CallableProcedure {

    @Override
    public String getName() {
        return "min";
    }

    @Override
    public String getRemarks() {
        return "Returns the row minimum value";
    }

    @Override
    public List<ParadoxField> getCols() {
        final ArrayList<ParadoxField> ret = new ArrayList<ParadoxField>();

        final ParadoxField field = new ParadoxField();
        field.setName("field");
        field.setType((byte)0xC);
        ret.add(field);

        return ret;
    }
}
