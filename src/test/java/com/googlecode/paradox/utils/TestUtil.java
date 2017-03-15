/*
 * TestUtil.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Assert;

/**
 * Utility class for test utility class sanity.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.2
 */
public class TestUtil {
    
    /**
     * Verifies that a utility class is well defined.
     *
     * @param classReference
     *            utility class to verify.
     * @return true if there are no errors.
     * @throws NoSuchMethodException
     *             in case of sanity failures.
     * @throws InvocationTargetException
     *             in case of sanity failures.
     * @throws InstantiationException
     *             in case of sanity failures.
     * @throws IllegalAccessException
     *             in case of sanity failures.
     */
    public static boolean assertUtilityClassWellDefined(final Class<?> classReference)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Assert.assertTrue("class must be final", Modifier.isFinal(classReference.getModifiers()));
        Assert.assertEquals("There must be only one constructor", 1, classReference.getDeclaredConstructors().length);
        final Constructor<?> constructor = classReference.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            return false;
        }
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
        for (final Method method : classReference.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(classReference)) {
                return false;
            }
        }
        return true;
    }
}
