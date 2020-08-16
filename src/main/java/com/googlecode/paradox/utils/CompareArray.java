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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.rowset.ValuesComparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A distinct comparator to use with Object array.
 *
 * @version 1.0
 * @since 1.6.0
 */
class CompareArray implements Comparator<Object[]>, Serializable {
    /**
     * The columns to compare.
     */
    private final int[] columns;

    /**
     * The connection information.
     */
    @SuppressWarnings("java:S1948")
    private final ConnectionInfo connectionInfo;

    /**
     * Creates a new instance.
     *
     * @param columns        the columns to compare.
     * @param connectionInfo the connection information.
     */
    public CompareArray(final int[] columns, final ConnectionInfo connectionInfo) {
        this.columns = columns;
        this.connectionInfo = connectionInfo;
    }

    @Override
    @SuppressWarnings("java:S1142")
    public int compare(Object[] o1, Object[] o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        }

        for (int i : columns) {
            int ret = ValuesComparator.compare(o1[i], o2[i], connectionInfo);
            if (ret != 0) {
                return ret;
            }
        }

        return 0;
    }
}
