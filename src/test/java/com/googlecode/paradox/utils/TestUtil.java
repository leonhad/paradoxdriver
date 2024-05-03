/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utility class for test utility class sanity.
 *
 * @since 1.2
 */
public final class TestUtil {

    private static final Logger LOGGER = Logger.getLogger(TestUtil.class.getName());

    private TestUtil() {
        super();
    }

    /**
     * Verifies that a utility class is well-defined.
     *
     * @param classReference utility class to verify.
     * @return true if there are no errors.
     */
    public static boolean assertSanity(final Class<?> classReference) {
        assertTrue(Modifier.isFinal(classReference.getModifiers()));
        assertEquals(1, classReference.getDeclaredConstructors().length);

        try {
            final Constructor<?> constructor = classReference.getDeclaredConstructor();
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                throw new SQLException("Constructor not accessible");
            }
            constructor.setAccessible(true);
            constructor.newInstance();
            constructor.setAccessible(false);
            for (final Method method : classReference.getMethods()) {
                if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(classReference)) {
                    throw new SQLException("Invalid method " + method.getName());
                }
            }
            return true;
        } catch (final InstantiationException | InvocationTargetException | NoSuchMethodException
                       | IllegalAccessException | SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
}
