package com.googlecode.paradox.procedures.math;

import com.googlecode.paradox.procedures.CallableProcedure;

/**
 *
 * @author Leonardo Alves da Costa
 */
public class Sum extends CallableProcedure {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "sum";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemarks() {
        return "Returns the sum of row value";
    }
}
