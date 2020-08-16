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
package com.googlecode.paradox.function.aggregate.context;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.function.aggregate.IGroupingContext;

/**
 * Count context.
 *
 * @version 1.3
 * @since 1.6.0
 */
public class CountContext implements IGroupingContext<Integer> {

    private int value;

    /**
     * Creates a new instance.
     *
     * @param value the amount to count.
     */
    public CountContext(final int value) {
        this.value = value;
    }

    @Override
    public void process(final IGroupingContext<Integer> context, final ConnectionInfo connectionInfo) {
        if (context != null) {
            this.value += ((CountContext) context).value;
        }
    }

    @Override
    public Integer toValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
