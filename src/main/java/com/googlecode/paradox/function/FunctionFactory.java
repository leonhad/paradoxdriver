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
package com.googlecode.paradox.function;

import com.googlecode.paradox.function.conditional.CoalesceFunction;
import com.googlecode.paradox.function.conditional.NullIfFunction;
import com.googlecode.paradox.function.conditional.NvlFunction;
import com.googlecode.paradox.function.date.CurrentDateFunction;
import com.googlecode.paradox.function.date.CurrentTimeFunction;
import com.googlecode.paradox.function.date.CurrentTimestampFunction;
import com.googlecode.paradox.function.date.ExtractFunction;
import com.googlecode.paradox.function.grouping.CountFunction;
import com.googlecode.paradox.function.numeric.IsNumericFunction;
import com.googlecode.paradox.function.string.*;
import com.googlecode.paradox.function.system.UserFunction;
import com.googlecode.paradox.function.system.VersionFunction;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Function factory to register and produce new functions.
 *
 * @version 1.6.0
 * @since 1.0
 */
public final class FunctionFactory {

    /**
     * The registered function list.
     */
    private static final HashMap<String, Supplier<? extends IFunction>> FUNCTIONS = new HashMap<>();

    /**
     * The registered function list that can be called without parenthesis.
     */
    private static final Map<String, Supplier<? extends IFunction>> FUNCTION_ALIAS;

    static {
        // Conditional functions.
        FUNCTIONS.put(CoalesceFunction.NAME, CoalesceFunction::new);
        FUNCTIONS.put("ISNULL", NvlFunction::new);
        FUNCTIONS.put(NullIfFunction.NAME, NullIfFunction::new);
        FUNCTIONS.put(NvlFunction.NAME, NvlFunction::new);

        // Grouping functions.
        FUNCTIONS.put(CountFunction.NAME, CountFunction::new);

        // Numeric functions.
        FUNCTIONS.put(IsNumericFunction.NAME, IsNumericFunction::new);

        // String functions.
        FUNCTIONS.put(AsciiFunction.NAME, AsciiFunction::new);
        FUNCTIONS.put(BitLengthFunction.NAME, BitLengthFunction::new);
        FUNCTIONS.put(CharLengthFunction.NAME, CharLengthFunction::new);
        FUNCTIONS.put(CharFunction.NAME, CharFunction::new);
        FUNCTIONS.put("CHR", CharFunction::new);
        FUNCTIONS.put(ConcatFunction.NAME, ConcatFunction::new);
        FUNCTIONS.put(ConcatWSFunction.NAME, ConcatWSFunction::new);
        FUNCTIONS.put("CHARACTER_LENGTH", CharLengthFunction::new);
        FUNCTIONS.put("LENGTH", CharLengthFunction::new);
        FUNCTIONS.put("LEN", CharLengthFunction::new);
        FUNCTIONS.put(CurrentDateFunction.NAME, CurrentDateFunction::new);
        FUNCTIONS.put(CurrentTimeFunction.NAME, CurrentTimeFunction::new);
        FUNCTIONS.put(CurrentTimestampFunction.NAME, CurrentTimestampFunction::new);
        FUNCTIONS.put(ExtractFunction.NAME, ExtractFunction::new);
        FUNCTIONS.put(LeftFunction.NAME, LeftFunction::new);
        FUNCTIONS.put(LowerFunction.NAME, LowerFunction::new);
        FUNCTIONS.put(LPadFunction.NAME, LPadFunction::new);
        FUNCTIONS.put(OctectLengthFunction.NAME, OctectLengthFunction::new);
        FUNCTIONS.put(PositionFunction.NAME, PositionFunction::new);
        FUNCTIONS.put(RPadFunction.NAME, RPadFunction::new);
        FUNCTIONS.put(RepeatFunction.NAME, RepeatFunction::new);
        FUNCTIONS.put("REPLICATE", RepeatFunction::new);
        FUNCTIONS.put(ReplaceFunction.NAME, ReplaceFunction::new);
        FUNCTIONS.put(ReverseFunction.NAME, ReverseFunction::new);
        FUNCTIONS.put(RightFunction.NAME, RightFunction::new);
        FUNCTIONS.put(SpaceFunction.NAME, SpaceFunction::new);
        FUNCTIONS.put(UpperFunction.NAME, UpperFunction::new);

        // System functions.
        FUNCTIONS.put("CURRENT_USER", UserFunction::new);
        FUNCTIONS.put("SESSION_USER", UserFunction::new);
        FUNCTIONS.put("SYSTEM_USER", UserFunction::new);
        FUNCTIONS.put(UserFunction.NAME, UserFunction::new);
        FUNCTIONS.put(VersionFunction.NAME, VersionFunction::new);

        FUNCTION_ALIAS = FUNCTIONS.entrySet().stream().filter(e -> e.getValue().get().isAllowAlias())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Utility class, not for use.
     */
    private FunctionFactory() {
        // Not used.
    }

    /**
     * Gets a function by alias.
     *
     * @param alias the function alias.
     * @return the  a function by  alias.
     */
    public static boolean isFunctionAlias(final String alias) {
        if (alias != null) {
            final Supplier<? extends IFunction> supplier = FUNCTION_ALIAS.get(alias.toUpperCase(Locale.US));
            return supplier != null;
        }

        return false;
    }

    /**
     * Gets a function by name.
     *
     * @param name the function by name.
     * @return the function by name.
     */
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
