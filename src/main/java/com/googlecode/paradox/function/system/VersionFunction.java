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
package com.googlecode.paradox.function.system;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.Constants;

/**
 * The SQL VERSION functions.
 *
 * @version 1.4
 * @since 1.6.0
 */
public class VersionFunction extends AbstractSystemFunction {

    /**
     * The function name.
     */
    public static final String NAME = "VERSION";

    /**
     * Column parameter list.
     */
    private static final Column[] COLUMNS = {
            new Column(null, ParadoxType.VARCHAR, "The driver version.", 0, false, RESULT)
    };

    @Override
    public String getRemarks() {
        return "Gets the driver version.";
    }

    @Override
    public Column[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object execute(final ConnectionInfo connectionInfo, final Object[] values, final ParadoxType[] types,
                          final FieldNode[] fields) {
        final String system = String.format("%s %s (%s), %s %s (%s)",
                System.getProperty("java.vm.name"), System.getProperty("java.runtime.version"),
                System.getProperty("java.vendor.version"), System.getProperty("os.name"),
                System.getProperty("os.version"), System.getProperty("os.arch"));

        return String.format("%s %s on %s", Constants.DRIVER_NAME, Constants.DRIVER_VERSION, system);
    }
}
