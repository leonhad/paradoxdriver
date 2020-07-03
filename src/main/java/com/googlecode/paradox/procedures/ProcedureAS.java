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
package com.googlecode.paradox.procedures;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.procedures.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores the procedures in this driver.
 *
 * @version 1.3
 * @since 1.0
 */
public final class ProcedureAS {

    /**
     * All registered procedures.
     */
    private final List<AbstractCallableProcedure> procedures = new ArrayList<>();

    /**
     * Register the default procedures.
     *
     * @param connection the Paradox connection.
     */
    public ProcedureAS(final ParadoxConnection connection) {
        this.register(new Average(connection));
        this.register(new Count(connection));
        this.register(new Max(connection));
        this.register(new Min(connection));
        this.register(new Sum(connection));
    }

    /**
     * Gets the procedure by name.
     *
     * @param name the procedure name.
     * @return the procedure.
     */
    public AbstractCallableProcedure get(final String name) {
        for (final AbstractCallableProcedure procedure : this.procedures) {
            if (procedure.getName().equalsIgnoreCase(name)) {
                return procedure;
            }
        }
        return null;
    }

    /**
     * Gets the procedures list.
     *
     * @return the procedures list.
     */
    public List<AbstractCallableProcedure> list() {
        return this.procedures.stream().filter(p -> !p.isNative()).collect(Collectors.toList());
    }

    /**
     * Registers a new procedure.
     *
     * @param procedure the procedure to register.
     */
    private void register(final AbstractCallableProcedure procedure) {
        this.procedures.add(procedure);
    }
}
