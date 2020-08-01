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

import com.googlecode.paradox.function.aggregate.IGroupingContext;
import com.googlecode.paradox.rowset.ValuesComparator;

import java.math.BigDecimal;

/**
 * Max context.
 *
 * @version 1.1
 * @since 1.6.0
 */
public class MaxContext implements IGroupingContext<BigDecimal> {

    private BigDecimal value;

    /**
     * Creates a new instance.
     *
     * @param value the amount to count.
     */
    public MaxContext(final BigDecimal value) {
        this.value = value;
    }

    @Override
    public void process(final IGroupingContext<BigDecimal> context) {
        final BigDecimal other = ((MaxContext) context).value;

        if (ValuesComparator.compare(this.value, other) < 0) {
            this.value = other;
        }
    }

    @Override
    public BigDecimal toValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
