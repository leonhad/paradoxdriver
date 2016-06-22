package com.googlecode.paradox.procedures.math;

import com.googlecode.paradox.procedures.CallableProcedure;

/**
 *
 * @author Leonardo Alves da Costa
 */
public class Max extends CallableProcedure {

    @Override
    public String getName() {
        return "max";
    }

    @Override
    public String getRemarks() {
        return "Returns the row max value";
    }
}
