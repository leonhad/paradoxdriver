/*
 * Utils.java 06/20/2015 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Locale;

/**
 * Driver utilities.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
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
     * Returns true if this either implements the interface argument or is directly or indirectly a wrapper for an
     * object that does. Returns false otherwise..
     *
     * @param wrapper
     *            wrapper to test for.
     * @param iFace
     *            a Class defining an interface.
     * @return true if this implements the interface or directly or indirectly wraps an object that does.
     * @since 1.2
     */
    public static boolean isWrapperFor(final Wrapper wrapper, final Class<?> iFace) {
        return wrapper.getClass().isAssignableFrom(iFace);
    }

    /**
     * Convert the Paradox VARCHAR to {@link String}. The paradox fill the entire buffer with zeros at end of VARCHAR
     * literals.
     *
     * @param buffer
     *            VARCHAR Buffer to convert.
     * @param charset
     *            Table charset.
     * @return a formatted {@link String}.
     */
    public static String parseString(final ByteBuffer buffer, final Charset charset) {
        final byte[] value = buffer.array();
        int length = value.length;
        for (; length > 0; length--) {
            // array value starts with zero, not 1
            if (value[length - 1] != 0) {
                break;
            }
        }
        buffer.flip();
        buffer.limit(length);
        return charset.decode(buffer).toString();
    }

    /**
     * Remove the DB suffix from a {@link String}.
     *
     * @param name
     *            the {@link String} to format.
     * @return the formatted {@link String}.
     */
    public static String removeDb(final String name) {
        return Utils.removeSuffix(name, "DB");
    }

    /**
     * Remove the MB suffix from a {@link String}.
     *
     * @param name
     *            the {@link String} to format.
     * @return the formatted {@link String}.
     */
    public static String removeMb(final String name) {
        return Utils.removeSuffix(name, "MB");
    }

    /**
     * Returns an object that implements the given interface to allow access to non-standard methods, or standard
     * methods not exposed by the proxy.
     *
     * @param <T>
     *            the type of the class modeled by this Class object.
     * @param wrapper
     *            the wrapper class.
     * @param iFace
     *            A Class defining an interface that the result must implement.
     * @return an object that implements the interface. May be a proxy for the actual implementing object.
     * @throws java.sql.SQLException
     *             If no object found that implements the interface.
     * @since 1.2
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(final Wrapper wrapper, final Class<T> iFace) throws SQLException {
        if (wrapper.isWrapperFor(iFace)) {
            return (T) wrapper;
        }
        throw new SQLException("Type not found.", SQLStates.TYPE_NOT_FOUND.getValue());
    }

    /**
     * Remove a given suffix from {@link String}.
     *
     * @param name
     *            the {@link String} to format.
     * @param suffix
     *            the suffix.
     * @return the formatted {@link String}.
     */
    private static String removeSuffix(final String name, final String suffix) {
        if ((name != null) && name.toUpperCase(Locale.US).endsWith("." + suffix.toUpperCase(Locale.US))) {
            return name.substring(0, name.length() - 3);
        }
        return name;
    }
}
