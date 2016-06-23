package com.googlecode.paradox.integration;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.googlecode.paradox.data.IndexData;
import com.googlecode.paradox.data.PrimaryKeyData;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.ViewData;
import com.googlecode.paradox.utils.DateUtils;
import com.googlecode.paradox.utils.StringUtils;
import com.googlecode.paradox.utils.TestUtil;
import com.googlecode.paradox.utils.Utils;

/**
 * Generic tests for all utility classes.
 *
 * @author Leonardo Alves da Costa
 * @since 1.2
 * @version 1.1
 */
@Category(IntegrationTest.class)
public class UtilityTest {

    /**
     * Test for utilities classes.
     *
     * @throws NoSuchMethodException in case of not well formed class.
     * @throws InvocationTargetException in case of not well formed class.
     * @throws InstantiationException in case of not well formed class.
     * @throws IllegalAccessException in case of not well formed class.
     */
    @Test
    public void testClassesIntegrity() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TestUtil.assertUtilityClassWellDefined(Utils.class);
        TestUtil.assertUtilityClassWellDefined(StringUtils.class);
        TestUtil.assertUtilityClassWellDefined(DateUtils.class);
        TestUtil.assertUtilityClassWellDefined(TableData.class);
        TestUtil.assertUtilityClassWellDefined(ViewData.class);
        TestUtil.assertUtilityClassWellDefined(IndexData.class);
        TestUtil.assertUtilityClassWellDefined(PrimaryKeyData.class);
    }
}
