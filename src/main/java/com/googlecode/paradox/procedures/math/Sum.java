package com.googlecode.paradox.procedures.math;

import com.googlecode.paradox.procedures.CallableProcedure;

/**
 *
 * @author Leonardo Alves da Costa
 */
public class Sum extends CallableProcedure {

    @Override
    public String getName() {
        return "sum";
    }

    @Override
    public String getRemarks() {
        return "Returns the sum of row value";
    }
}
