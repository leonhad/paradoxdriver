package com.googlecode.paradox.parser.nodes.values;

import com.googlecode.paradox.parser.nodes.SQLNode;

public class CharacterNode extends SQLNode {

	public CharacterNode(final String value, final String alias) {
		super(value.toUpperCase(), alias.toUpperCase());
	}

}
