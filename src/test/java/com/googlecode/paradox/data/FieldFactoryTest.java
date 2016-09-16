/*
 * FieldFactoryTest.java
 *
 * 07/21/2016
 * Copyright (C) 2016 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data;

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.utils.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Unit test for {@link FieldFactory} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class FieldFactoryTest {

    /**
     * Test for sanity.
     *
     * @throws NoSuchMethodException
     *         in case of errors.
     * @throws InstantiationException
     *         in case of errors.
     * @throws IllegalAccessException
     *         in case of errors.
     * @throws InvocationTargetException
     *         in case of errors.
     */
    @Test
    public void testSanity() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertUtilityClassWellDefined(FieldFactory.class));
    }

    /**
     * Test for invalid field type.
     *
     * @throws SQLException
     *         in case of success.
     */
    @Test(expected = SQLException.class)
    public void testUnsupportedType() throws SQLException {
        ParadoxField field = new ParadoxField();
        field.setType((byte) -1);
        FieldFactory.parse(null, null, field);
    }
}