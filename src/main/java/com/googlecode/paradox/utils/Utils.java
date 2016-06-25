/*
 * ViewData.java
 *
 * 06/20/2015
 * Copyright (C) 2016 Leonardo Alves da Costa
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
package com.googlecode.paradox.utils;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Driver utilities.
 *
 * @author Leonardo Alves da Costa
 * @since 1.2
 * @version 1.1
 */
public final class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    /**
     * Utility class.
     */
    private Utils() {
        // Utility class.
    }

    public static void close(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (final IOException e) {
            LOGGER.log(Level.FINER, e.getMessage(), e);
        }
    }

    /**
     * Returns true if this either implements the interface argument or is
     * directly or indirectly a wrapper for an object that does. Returns false
     * otherwise. If this implements the interface then return true, else if
     * this is a wrapper then return the result of recursively calling
     * <code>isWrapperFor</code> on the wrapped object. If this does not
     * implement the interface and is not a wrapper, return false. This method
     * should be implemented as a low-cost operation compared to
     * <code>unwrap</code> so that callers can use this method to avoid
     * expensive <code>unwrap</code> calls that may fail. If this method returns
     * true then calling <code>unwrap</code> with the same argument should
     * succeed.
     *
     * @param wrapper
     *            wrapper to test for.
     * @param iface
     *            a Class defining an interface.
     * @return true if this implements the interface or directly or indirectly
     *         wraps an object that does.
     * @throws java.sql.SQLException
     *             if an error occurs while determining whether this is a
     *             wrapper for an object with the given interface.
     * @since 1.2
     */
    public static boolean isWrapperFor(final Wrapper wrapper, final Class<?> iface) throws SQLException {
        return wrapper.getClass().isAssignableFrom(iface);
    }

    /**
     * Returns an object that implements the given interface to allow access to
     * non-standard methods, or standard methods not exposed by the proxy.
     *
     * If the receiver implements the interface then the result is the receiver
     * or a proxy for the receiver. If the receiver is a wrapper and the wrapped
     * object implements the interface then the result is the wrapped object or
     * a proxy for the wrapped object. Otherwise return the the result of
     * calling <code>unwrap</code> recursively on the wrapped object or a proxy
     * for that result. If the receiver is not a wrapper and does not implement
     * the interface, then an <code>SQLException</code> is thrown.
     *
     * @param <T>
     *            the type of the class modeled by this Class object.
     * @param wrapper
     *            the wrapper class.
     * @param iface
     *            A Class defining an interface that the result must implement.
     * @return an object that implements the interface. May be a proxy for the
     *         actual implementing object.
     * @throws java.sql.SQLException
     *             If no object found that implements the interface.
     * @since 1.2
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(final Wrapper wrapper, final Class<T> iface) throws SQLException {
        if (wrapper.isWrapperFor(iface)) {
            return (T) wrapper;
        }
        throw new SQLException("Type not found.", SQLStates.TYPE_NOT_FOUND);
    }
}
