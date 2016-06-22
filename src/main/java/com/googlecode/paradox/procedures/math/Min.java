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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "min";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemarks() {
        return "Returns the row minimum value";
    }
}
