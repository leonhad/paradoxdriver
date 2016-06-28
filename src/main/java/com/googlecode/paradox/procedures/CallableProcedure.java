/*
 * CallableProcedure.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.procedures;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.paradox.metadata.ParadoxField;

/**
 * Abstract class used to create any callable procedure.
 *
 * @author Leonardo Alves da Costa
 * @since 1.1.
 * @version 1.1
 */
public abstract class CallableProcedure {

    /**
     * Get the procedure columns.
     *
     * @return the procedure columns.
     */
    public List<ParadoxField> getCols() {
        final ArrayList<ParadoxField> ret = new ArrayList<>();

        final ParadoxField field = new ParadoxField();
        field.setName("field");
        field.setType((byte) 0xC);
        ret.add(field);

        return ret;
    }

    /**
     * Gets the procedure name.
     *
     * @return the procedure name.
     */
    public abstract String getName();

    /**
     * Gets the procedure description.
     *
     * @return the procedure description.
     */
    public abstract String getRemarks();

    /**
     * Gets the return type.
     *
     * @return the return type.
     */
    public int getReturnType() {
        return DatabaseMetaData.procedureReturnsResult;
    }
}
