package com.googlecode.paradox.utils;

import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Constants} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ConstantsTest {
    
    /**
     * Test for sanity.
     *
     * @throws IllegalAccessException
     *             in case of errors.
     * @throws InstantiationException
     *             in case of errors.
     * @throws InvocationTargetException
     *             in case of errors.
     * @throws NoSuchMethodException
     *             in case of errors.
     */
    @Test
    public void testSanity()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertUtilityClassWellDefined(Constants.class));
    }
}
