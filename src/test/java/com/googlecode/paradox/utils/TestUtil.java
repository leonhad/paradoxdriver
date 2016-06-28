/*
 * TestUtil.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;

/**
 * Utility class for test utility class sanity.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.2
 * @version 1.0
 */
public class TestUtil {

    /**
     * Verifies that a utility class is well defined.
     *
     * @param classReference
     *            utility class to verify.
     * @throws NoSuchMethodException
     *             in case of sanity failures.
     * @throws InvocationTargetException
     *             in case of sanity failures.
     * @throws InstantiationException
     *             in case of sanity failures.
     * @throws IllegalAccessException
     *             in case of sanity failures.
     */
    public static void assertUtilityClassWellDefined(final Class<?> classReference) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Assert.assertTrue("class must be final", Modifier.isFinal(classReference.getModifiers()));
        Assert.assertEquals("There must be only one constructor", 1, classReference.getDeclaredConstructors().length);
        final Constructor<?> constructor = classReference.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            Assert.fail("constructor is not private");
        }
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
        for (final Method method : classReference.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(classReference)) {
                Assert.fail("there exists a non-static method:" + method);
            }
        }
    }
}
