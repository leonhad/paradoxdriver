package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.comparisons.IComparision;

public class NOTNode extends SQLNode implements IComparision {

	public NOTNode(final SQLNode child) {
		super("NOT");
		setChildren(Arrays.asList(child));
	}

}
