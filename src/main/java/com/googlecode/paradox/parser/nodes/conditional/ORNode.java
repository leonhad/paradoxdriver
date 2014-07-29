package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.comparisons.IComparision;

public class ORNode extends SQLNode implements IComparision {

	public ORNode(final SQLNode child) {
		super("OR");
		setChildren(Arrays.asList(child));
	}

}
