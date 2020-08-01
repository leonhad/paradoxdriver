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
package com.googlecode.paradox.function.aggregate;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.parser.nodes.SQLNode;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.math.BigDecimal;
import java.util.List;

/**
 * The SQL sum function.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class SumFunction extends AbstractGroupingFunction<BigDecimal> {

    /**
     * The function name.
     */
    public static final String NAME = "SUM";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.NUMBER, "The sum of the values.", 0, true, RESULT),
            new Column("value", ParadoxType.NUMBER, "The numeric value to sum.", 1, true, IN),
    };

    @Override
    public String getRemarks() {
        return "Returns a sum of a set of values.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Context execute(final ConnectionInfo connectionInfo, final Object[] values,
                           final ParadoxType[] types, final FieldNode[] fields) {
        BigDecimal value = ValuesConverter.getBigDecimal(values[0]);
        if (values[0] == null) {
            value = BigDecimal.ZERO;
        }

        return new Context(value);
    }

    @Override
    public void validate(final List<SQLNode> parameters) {
        // Do nothing. This function is always valid. We are only counting rows.
    }

    /**
     * Count context.
     *
     * @version 1.0
     * @since 1.6.0
     */
    private static class Context implements IGroupingContext<BigDecimal> {
        private BigDecimal value;

        /**
         * Creates a new instance.
         *
         * @param value the amount to count.
         */
        public Context(final BigDecimal value) {
            this.value = value;
        }

        @Override
        public void process(final IGroupingContext<BigDecimal> context) {
            this.value = value.add(context.toValue());
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
}
