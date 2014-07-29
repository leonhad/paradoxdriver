package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.comparisons.IComparision;

public class ExistsNode extends SQLNode implements IComparision {

	public ExistsNode(final SelectNode child) {
		super("EXISTS");
		setChildren(Arrays.asList(child));
	}

}
