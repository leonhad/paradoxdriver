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
package com.googlecode.paradox.planner.sorting;

import com.googlecode.paradox.rowset.ValuesComparator;

import java.io.Serializable;
import java.util.Comparator;

public class OrderByComparator implements Comparator<Object[]>, Serializable {
    private final int index;

    private final OrderType orderType;

    public OrderByComparator(final int index, final OrderType orderType) {
        this.index = index;
        this.orderType = orderType;
    }

    @Override
    public int compare(Object[] o1, Object[] o2) {
        if (orderType == OrderType.ASC) {
            return ValuesComparator.compare(o1[index], o2[index]);
        }

        return ValuesComparator.compare(o2[index], o1[index]);
    }
}
