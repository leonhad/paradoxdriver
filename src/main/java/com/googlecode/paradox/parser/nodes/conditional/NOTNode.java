package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;

public class NOTNode extends SQLNode {

	public NOTNode(final SQLNode child) {
		super("NOT");
		setChildren(Arrays.asList(child));
	}

}
