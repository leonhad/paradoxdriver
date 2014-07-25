package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;

public class XORNode extends SQLNode {

	public XORNode(final SQLNode child) {
		super("XOR");
		setChildren(Arrays.asList(child));
	}

}
