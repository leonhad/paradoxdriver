package com.googlecode.paradox.parser.nodes.values;

import com.googlecode.paradox.parser.TokenType;
import com.googlecode.paradox.parser.nodes.SQLNode;

/**
 * Created by Andre on 09.12.2014.
 */
public class AsteriskNode extends SQLNode {

    public AsteriskNode() {
        super(TokenType.ASTERISK.name());
    }
}
