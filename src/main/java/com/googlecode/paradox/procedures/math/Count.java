package com.googlecode.paradox.procedures.math;

import com.googlecode.paradox.procedures.CallableProcedure;

/**
 *
 * @author Leonardo Alves da Costa
 */
public class Count extends CallableProcedure {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "count";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRemarks() {
		return "Returns the row count";
	}
}
