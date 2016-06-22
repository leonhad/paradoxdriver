package com.googlecode.paradox.utils;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * Driver utilities.
 * 
 * @author Leonardo Alves da Costa
 * @since 22/06/2016
 * @version 1.0
 */
public final class Utils {
    /**
     * Not for instantiation.
     */
    private Utils() {
        // Not used.
    }
    
    /**
     * Returns an object that implements the given interface to allow access to
     * non-standard methods, or standard methods not exposed by the proxy.
     *
     * If the receiver implements the interface then the result is the receiver
     * or a proxy for the receiver. If the receiver is a wrapper
     * and the wrapped object implements the interface then the result is the
     * wrapped object or a proxy for the wrapped object. Otherwise return the
     * the result of calling <code>unwrap</code> recursively on the wrapped object
     * or a proxy for that result. If the receiver is not a
     * wrapper and does not implement the interface, then an <code>SQLException</code> is thrown.
     *
     * @param <T> the type of the class modeled by this Class object.
     * @param wrapper the wrapper class.
     * @param iface A Class defining an interface that the result must implement.
     * @return an object that implements the interface. May be a proxy for the actual implementing object.
     * @throws java.sql.SQLException If no object found that implements the interface.
     * @since 1.2
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(final Wrapper wrapper, final Class<T> iface) throws SQLException {
        if (wrapper.isWrapperFor(iface)) {
            return (T) wrapper;
        }
        throw new SQLException("Type not found.", SQLStates.TYPE_NOT_FOUND);
    }
    
    /**
     * Returns true if this either implements the interface argument or is directly or indirectly a wrapper
     * for an object that does. Returns false otherwise. If this implements the interface then return true,
     * else if this is a wrapper then return the result of recursively calling <code>isWrapperFor</code> on the wrapped
     * object. If this does not implement the interface and is not a wrapper, return false.
     * This method should be implemented as a low-cost operation compared to <code>unwrap</code> so that
     * callers can use this method to avoid expensive <code>unwrap</code> calls that may fail. If this method
     * returns true then calling <code>unwrap</code> with the same argument should succeed.
     *
     * @param iface a Class defining an interface.
     * @return true if this implements the interface or directly or indirectly wraps an object that does.
     * @throws java.sql.SQLException  if an error occurs while determining whether this is a wrapper
     * for an object with the given interface.
     * @since 1.2
     */
    public static boolean isWrapperFor(final Wrapper wrapper, final Class<?> iface) throws SQLException {
        return wrapper.getClass().isAssignableFrom(iface);
    }
}
