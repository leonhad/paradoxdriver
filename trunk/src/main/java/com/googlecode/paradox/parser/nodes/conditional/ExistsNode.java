package com.googlecode.paradox.parser.nodes.conditional;

import java.util.Arrays;

import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.parser.nodes.SelectNode;

public class ExistsNode extends SQLNode {

	public ExistsNode(final SelectNode child) {
		super("EXISTS");
		setChildren(Arrays.asList(child));
	}

}
