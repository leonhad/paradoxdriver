package com.googlecode.paradox.procedures.math;

import com.googlecode.paradox.procedures.CallableProcedure;

/**
 *
 * @author Leonardo Alves da Costa
 */
public class Averange extends CallableProcedure {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "averange";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRemarks() {
		return "Returns the avarange values.";
	}
}
