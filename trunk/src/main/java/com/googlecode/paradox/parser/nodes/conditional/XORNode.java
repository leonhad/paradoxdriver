package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.comparisons.IComparision;

public class XORNode extends SQLNode implements IComparision {

	public XORNode(final SQLNode child) {
		super("XOR");
		setChildren(Arrays.asList(child));
	}

}
