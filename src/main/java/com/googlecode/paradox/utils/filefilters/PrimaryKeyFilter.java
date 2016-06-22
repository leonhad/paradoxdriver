package com.googlecode.paradox.utils.filefilters;

import java.io.File;
import java.io.FileFilter;

import com.googlecode.paradox.utils.Expressions;

/**
 * Paradox Primary Key file filter
 *
 * @author Leonardo Alves da Costa
 * @since 11/12/2009
 * @version 1.0
 */
public class PrimaryKeyFilter implements FileFilter {

	private final String pkName;

	public PrimaryKeyFilter() {
		pkName = null;
	}

	public PrimaryKeyFilter(final String pkName) {
		this.pkName = pkName;
	}

	@Override
	public boolean accept(final File pathname) {
		final String name = pathname.getName();

		if (Expressions.accept(name, "%.PX")) {
			if (pkName != null) {
				return Expressions.accept(name, pkName);
			}
			return true;
		}
		return false;
	}

}
