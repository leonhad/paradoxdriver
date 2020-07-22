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

import com.googlecode.paradox.function.date.*;
import com.googlecode.paradox.function.general.*;
import com.googlecode.paradox.function.grouping.CountFunction;
import com.googlecode.paradox.function.numeric.*;
import com.googlecode.paradox.function.string.*;
import com.googlecode.paradox.function.system.UserFunction;
import com.googlecode.paradox.function.system.VersionFunction;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Function factory to register and produce new functions.
 *
 * @version 1.1
 * @since 1.6.0
 */
public final class FunctionFactory {

    /**
     * The registered function list.
     */
    private static final SortedMap<String, Supplier<? extends IFunction>> FUNCTIONS = new TreeMap<>();

    /**
     * The registered function list that can be called without parenthesis.
     */
    private static final Map<String, Supplier<? extends IFunction>> FUNCTION_ALIAS;

    static {
        // Date functions.
        FUNCTIONS.put(CurrentDateFunction.NAME, CurrentDateFunction::new);
        FUNCTIONS.put(CurrentTimeFunction.NAME, CurrentTimeFunction::new);
        FUNCTIONS.put(CurrentTimestampFunction.NAME, CurrentTimestampFunction::new);
        FUNCTIONS.put(ExtractFunction.NAME, ExtractFunction::new);
        FUNCTIONS.put(IsDateFunction.NAME, IsDateFunction::new);

        // General functions.
        FUNCTIONS.put(CastFunction.NAME, CastFunction::new);
        FUNCTIONS.put(CoalesceFunction.NAME, CoalesceFunction::new);
        FUNCTIONS.put(ConvertFunction.NAME, ConvertFunction::new);
        FUNCTIONS.put(IntegerFunction.NAME, IntegerFunction::new);
        FUNCTIONS.put("ISNULL", NvlFunction::new);
        FUNCTIONS.put(NullIfFunction.NAME, NullIfFunction::new);
        FUNCTIONS.put(NvlFunction.NAME, NvlFunction::new);
        FUNCTIONS.put(NumericFunction.NAME, NumericFunction::new);

        // Grouping functions.
        FUNCTIONS.put(CountFunction.NAME, CountFunction::new);

        // Numeric functions.
        FUNCTIONS.put(AbsFunction.NAME, AbsFunction::new);
        FUNCTIONS.put(ExpFunction.NAME, ExpFunction::new);
        FUNCTIONS.put(IsNumericFunction.NAME, IsNumericFunction::new);
        FUNCTIONS.put(LogFunction.NAME, LogFunction::new);
        FUNCTIONS.put(PiFunction.NAME, PiFunction::new);
        FUNCTIONS.put(PowerFunction.NAME, PowerFunction::new);
        FUNCTIONS.put(RadiansFunction.NAME, RadiansFunction::new);
        FUNCTIONS.put(RandFunction.NAME, RandFunction::new);
        FUNCTIONS.put(RoundFunction.NAME, RoundFunction::new);
        FUNCTIONS.put(SignFunction.NAME, SignFunction::new);
        FUNCTIONS.put(SqrtFunction.NAME, SqrtFunction::new);
        FUNCTIONS.put(SquareFunction.NAME, SquareFunction::new);
        FUNCTIONS.put(TanFunction.NAME, TanFunction::new);

        // String functions.
        FUNCTIONS.put(AsciiFunction.NAME, AsciiFunction::new);
        FUNCTIONS.put(BitLengthFunction.NAME, BitLengthFunction::new);
        FUNCTIONS.put(CharLengthFunction.NAME, CharLengthFunction::new);
        FUNCTIONS.put(CharFunction.NAME, CharFunction::new);
        FUNCTIONS.put(ChrFunction.NAME, ChrFunction::new);
        FUNCTIONS.put(ConcatFunction.NAME, ConcatFunction::new);
        FUNCTIONS.put(ConcatWSFunction.NAME, ConcatWSFunction::new);
        FUNCTIONS.put("CHARACTER_LENGTH", CharLengthFunction::new);
        FUNCTIONS.put("LENGTH", CharLengthFunction::new);
        FUNCTIONS.put("LEN", CharLengthFunction::new);
        FUNCTIONS.put(InitCapFunction.NAME, InitCapFunction::new);
        FUNCTIONS.put(LeftFunction.NAME, LeftFunction::new);
        FUNCTIONS.put(LowerFunction.NAME, LowerFunction::new);
        FUNCTIONS.put(LPadFunction.NAME, LPadFunction::new);
        FUNCTIONS.put(OctetLengthFunction.NAME, OctetLengthFunction::new);
        FUNCTIONS.put(PositionFunction.NAME, PositionFunction::new);
        FUNCTIONS.put(RPadFunction.NAME, RPadFunction::new);
        FUNCTIONS.put(RepeatFunction.NAME, RepeatFunction::new);
        FUNCTIONS.put("REPLICATE", RepeatFunction::new);
        FUNCTIONS.put(ReplaceFunction.NAME, ReplaceFunction::new);
        FUNCTIONS.put(ReverseFunction.NAME, ReverseFunction::new);
        FUNCTIONS.put(RightFunction.NAME, RightFunction::new);
        FUNCTIONS.put(SpaceFunction.NAME, SpaceFunction::new);
        FUNCTIONS.put(SubstringFunction.NAME, SubstringFunction::new);
        FUNCTIONS.put("TRANSLATE", ReplaceFunction::new);
        FUNCTIONS.put(TextFunction.NAME, TextFunction::new);
        FUNCTIONS.put(TrimFunction.NAME, TrimFunction::new);
        FUNCTIONS.put(UpperFunction.NAME, UpperFunction::new);
        FUNCTIONS.put(VarcharFunction.NAME, VarcharFunction::new);

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

    @SuppressWarnings("java:S1452")
    public static Map<String, Supplier<? extends IFunction>> getFunctionAlias() {
        return Collections.unmodifiableMap(FUNCTIONS);
    }

    /**
     * Gets a function by alias.
     *
     * @param alias the function alias.
     * @return the a function by alias.
     */
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public static boolean isFunctionAlias(final String alias) {
        if (alias != null) {
            final Supplier<? extends IFunction> supplier = FUNCTION_ALIAS.get(alias.toUpperCase());
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
    @SuppressWarnings({"i18n-java:V1018", "java:S1449"})
    public static IFunction getByName(final String name) {
        if (name != null) {
            final Supplier<? extends IFunction> supplier = FUNCTIONS.get(name.toUpperCase());
            if (supplier != null) {
                return supplier.get();
            }
        }

        return null;
    }

    /**
     * Return the function list separated by comma filtered by the function type.
     *
     * @param type the function type to filter.
     * @return the function list filtered by type.
     */
    public static String getByType(final FunctionType type) {
        return FUNCTIONS.entrySet().stream()
                .filter(e -> e.getValue().get().type() == type).map(Entry::getKey)
                .sorted().collect(Collectors.joining(","));
    }

}
