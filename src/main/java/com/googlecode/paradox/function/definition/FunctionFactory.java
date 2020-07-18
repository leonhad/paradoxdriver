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
package com.googlecode.paradox.function.definition;

import com.googlecode.paradox.function.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public final class FunctionFactory {

    private static final HashMap<String, Supplier<? extends IFunction>> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put(CoalesceFunction.NAME, CoalesceFunction::new);
        FUNCTIONS.put(CountFunction.NAME, CountFunction::new);
        FUNCTIONS.put(CurrentDateFunction.NAME, CurrentDateFunction::new);
        FUNCTIONS.put(CurrentTimeFunction.NAME, CurrentTimeFunction::new);
        FUNCTIONS.put(CurrentTimestampFunction.NAME, CurrentTimestampFunction::new);
        FUNCTIONS.put(LowerFunction.NAME, LowerFunction::new);
        FUNCTIONS.put(NullIfFunction.NAME, NullIfFunction::new);
        FUNCTIONS.put(NvlFunction.NAME, NvlFunction::new);
        FUNCTIONS.put(ReverseFunction.NAME, ReverseFunction::new);
        FUNCTIONS.put(UpperFunction.NAME, UpperFunction::new);
    }

    private FunctionFactory() {
        // Not used.
    }

    public static IFunction getByName(final String name) {
        if (name != null) {
            final Supplier<? extends IFunction> supplier = FUNCTIONS.get(name.toUpperCase(Locale.US));
            if (supplier != null) {
                return supplier.get();
            }
        }

        return null;
    }
}
