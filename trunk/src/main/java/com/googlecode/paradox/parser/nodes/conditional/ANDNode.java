package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;

public class ANDNode extends SQLNode {

	public ANDNode(final SQLNode child) {
		super("AND");
		setChildren(Arrays.asList(child));
	}

}
