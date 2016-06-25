package com.googlecode.paradox.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;

public class TestUtil {

    /**
     * Verifies that a utility class is well defined.
     *
     * @param classReference
     *            utility class to verify.
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
