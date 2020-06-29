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
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.parser.nodes.values.AsteriskNode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Unit test {@link JoinNode} class.
 *
 * @author Leonardo Costa
 * @version 1.1
 * @since 1.3
 */
public class JoinNodeTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for new instance.
     */
    @Test
    public void testInstance() {
        final JoinNode node = new JoinNode(conn);
        Assert.assertNull("Invalid node name.", node.getName());
    }

    /**
     * Test join type.
     */
    @Test
    public void testJoinType() {
        final JoinNode node = new JoinNode(conn);
        node.setType(JoinType.LEFT);
        Assert.assertEquals("Invalid node type.", JoinType.LEFT, node.getType());
    }

    /**
     * Test for join name.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select ac.AreaCode, st.State, st.Capital, c.County " +
                        "from geog.tblAC ac " +
                        "     join geog.tblsttes st on st.State = ac.State " +
                        "     join geog.County c on c.StateID = st.State " +
                        "where c.CountyID = 205")) {
            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertNull("Invalid value.", rs.getTimestamp("Timestamp"));

            Assert.assertTrue("Invalid Result Set state.", rs.next());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Assert.assertEquals("Invalid value.", "2020-02-01 01:00:01",
                    format.format(rs.getTimestamp("Timestamp")));

            Assert.assertFalse("Invalid Result Set state.", rs.next());
        }
    }
}
