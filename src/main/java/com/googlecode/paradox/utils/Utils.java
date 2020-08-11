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

import com.googlecode.paradox.exceptions.ParadoxException;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * Driver utilities.
 *
 * @version 1.3
 * @since 1.2
 */
public final class Utils {

    /**
     * Utility class.
     */
    private Utils() {
        // Utility class.
    }

    /**
     * Returns true if this either implements the interface argument or is
     * directly or indirectly a wrapper for an object that does. Returns false
     * otherwise..
     *
     * @param wrapper wrapper to test for.
     * @param iFace   a Class defining an interface.
     * @return true if this implements the interface or directly or indirectly
     * wraps an object that does.
     * @since 1.2
     */
    public static boolean isWrapperFor(final Wrapper wrapper, final Class<?> iFace) {
        return wrapper.getClass().isAssignableFrom(iFace);
    }

    /**
     * Returns an object that implements the given interface to allow access to
     * non-standard methods, or standard methods not exposed by the proxy.
     *
     * @param <T>     the type of the class modeled by this Class object.
     * @param wrapper the wrapper class.
     * @param iFace   A Class defining an interface that the result must implement.
     * @return an object that implements the interface. May be a proxy for the
     * actual implementing object.
     * @throws java.sql.SQLException If no object found that implements the interface.
     * @since 1.2
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(final Wrapper wrapper, final Class<T> iFace) throws SQLException {
        if (wrapper.isWrapperFor(iFace)) {
            return (T) wrapper;
        }
        throw new ParadoxException(ParadoxException.Error.TYPE_NOT_FOUND);
    }

    /**
     * Remove a given suffix from {@link String}.
     *
     * @param name   the {@link String} to format.
     * @param suffix the suffix.
     * @return the formatted {@link String}.
     */
    public static String removeSuffix(final String name, final String suffix) {
        if (name != null) {
            int index = name.lastIndexOf('.');
            if (index != -1 && name.substring(index + 1).equalsIgnoreCase(suffix)) {
                return name.substring(0, index);
            }
        }
        return name;
    }

    /**
     * Remove a given suffix from {@link String}.
     *
     * @param name the {@link String} to format.
     * @return the formatted {@link String}.
     */
    public static String removeSuffix(final String name) {
        if (name != null) {
            int index = name.lastIndexOf('.');
            if (index != -1) {
                return name.substring(0, index);
            }
        }

        return name;
    }

    /**
     * Searchs a value in enum with insensitive case.
     *
     * @param enumeration the enumeration to search.
     * @param search      the value to search.
     * @param <T>         the result type (same as enumeration).
     * @return The enumeration or <code>null</code> if not found.
     */
    public static <T extends Enum<?>> T searchEnum(final Class<T> enumeration, final String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(search)) {
                return each;
            }
        }

        return null;
    }
}
