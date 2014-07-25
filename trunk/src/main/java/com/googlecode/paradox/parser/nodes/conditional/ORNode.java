package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;

public class ORNode extends SQLNode {

	public ORNode(final SQLNode child) {
		super("OR");
		setChildren(Arrays.asList(child));
	}

}
