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
package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.paradox.ParadoxField;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.utils.TestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link ParadoxFieldFactory} class.
 *
 * @since 1.3
 */
class FieldFactoryTest {

    /**
     * The connection string used in  tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The connection.
     */
    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeAll
    static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    @AfterAll
    static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for sanity.
     */
    @Test
    void testSanity() {
        assertTrue(TestUtil.assertSanity(ParadoxFieldFactory.class));
    }

    /**
     * Test for invalid field type.
     */
    @Test
    void testUnsupportedType() {
        final ParadoxField field = new ParadoxField(ParadoxType.NULL);
        assertThrows(SQLException.class, () -> ParadoxFieldFactory.parse(null, null, field));
    }
}
