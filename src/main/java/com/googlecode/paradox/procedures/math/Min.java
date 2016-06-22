package com.googlecode.paradox.procedures.math;

import com.googlecode.paradox.procedures.CallableProcedure;

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
