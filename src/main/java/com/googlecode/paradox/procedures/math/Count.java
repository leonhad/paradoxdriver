package com.googlecode.paradox.procedures.math;

import com.googlecode.paradox.procedures.CallableProcedure;

/**
 *
 * @author Leonardo Alves da Costa
 */
public class Count extends CallableProcedure {

    @Override
    public String getName() {
        return "count";
    }

    @Override
    public String getRemarks() {
        return "Returns the row count";
    }
}
