/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.results;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Unit test for {@link TypeName} class.
 *
 * @version 1.0
 * @since 1.3
 */
public class TypeNameTest {

    /**
     * Gets the class name.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testClassName() throws SQLException {
        Assert.assertEquals("Invalid token type.", "java.io.InputStream", TypeName.getClassNameByType(Types.BINARY));
    }

    /**
     * Test for unsupported type.
     *
     * @throws SQLException if there is no errors.
     */
    @Test(expected = SQLException.class)
    public void testClassNameUnsupported() throws SQLException {
        TypeName.getClassNameByType(99);
    }

    /**
     * Gets the field name.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testFieldName() throws SQLException {
        Assert.assertEquals("Invalid token type name.", "BINARY", TypeName.getTypeName(Types.BINARY));
    }

    /**
     * Test for unsupported type name.
     *
     * @throws SQLException if there is no errors.
     */
    @Test(expected = SQLException.class)
    public void testNameUnsupported() throws SQLException {
        TypeName.getTypeName(99);
    }

    /**
     * Test for SQL Type.
     */
    @Test
    public void testSQLType() {
        Assert.assertEquals("Invalid token type.", Types.BINARY, TypeName.BINARY.getSQLType());
    }
}
