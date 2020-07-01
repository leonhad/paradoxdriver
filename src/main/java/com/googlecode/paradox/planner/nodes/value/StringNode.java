/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.planner.nodes.value;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.IFieldValue;

/**
 * Stores a String value.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class StringNode extends FieldNode implements IFieldValue {

    public StringNode(final ParadoxConnection connection, final String name) {
        super(connection, null, name, null);
    }
}
