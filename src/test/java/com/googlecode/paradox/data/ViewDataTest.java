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
package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxView;
import com.googlecode.paradox.results.ParadoxFieldType;
import com.googlecode.paradox.utils.TestUtil;
import org.junit.*;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;

/**
 * Unit test for {@link ViewData}.
 *
 * @version 1.5
 * @since 1.0
 */
public class ViewDataTest {

    /**
     * Connection string used in tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Register the driver.
     *
     * @throws ClassNotFoundException in case of connection errors.
     */
    @BeforeClass
    public static void initClass() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Used to close the test connection.
     *
     * @throws Exception in case closing of errors.
     */
    @After
    public void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws Exception in case of connection errors.
     */
    @Before
    public void connect() throws Exception {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for list views.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testListViews() throws Exception {
        final List<ParadoxView> list = ViewData.listViews(this.conn.getCurrentSchema(), this.conn);
        Assert.assertEquals("Invalid views", 1, list.size());
        Assert.assertEquals("Invalid view name.", "AREAS", list.get(0).getName());
    }

    /**
     * Test for parse view.
     */
    @Test
    public void testParseExpression() {
        final ParadoxField field = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        ViewData.parseExpression(field, "_PC, CALC _PC*_QTD AS TOTAL_COST");
        Assert.assertTrue("Field is not checked.", field.isChecked());
        Assert.assertEquals("Invalid field name.", "_PC", field.getJoinName());
        Assert.assertEquals("Invalid field name.", "CALC _PC*_QTD", field.getExpression());
        Assert.assertEquals("Invalid field name.", "TOTAL_COST", field.getAlias());

        Assert.assertTrue("Invalid checked status.", field.isChecked());
    }

    /**
     * Test for class sanity.
     */
    @Test
    public void testSanity() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertUtilityClassWellDefined(ViewData.class));
    }

    /**
     * Test for view file reading.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testViewFileReading() throws Exception {
        final DatabaseMetaData meta = this.conn.getMetaData();

        try (ResultSet rs = meta.getColumns("test-classes", "db", "AREAS.QBE", "%")) {
            // This view have 3 fields.
            Assert.assertTrue("Invalid result set.", rs.next());
            Assert.assertTrue("Invalid result set.", rs.next());
            Assert.assertTrue("Invalid result set.", rs.next());
            Assert.assertFalse("Invalid result set.", rs.next());
        }
    }
}
