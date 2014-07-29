package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.comparisons.IComparision;

public class ANDNode extends SQLNode implements IComparision {

	public ANDNode(final SQLNode child) {
		super("AND");
		setChildren(Arrays.asList(child));
	}

}
