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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unit test {@link JoinNode} class.
 *
 * @version 1.3
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
        final JoinNode node = new JoinNode(conn, null, null, null, JoinType.INNER);
        Assert.assertNull("Invalid node name.", node.getName());
    }

    /**
     * Test join type.
     */
    @Test
    public void testJoinType() {
        final JoinNode node = new JoinNode(conn, null, null, null, JoinType.LEFT);
        Assert.assertEquals("Invalid node type.", JoinType.LEFT, node.getJoinType());
    }

    /**
     * Test for inner join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select c.CountyID, ac.AreaCode " +
                        "from geog.tblAC ac " +
                        "     join geog.tblsttes st on st.State = ac.State " +
                        "     inner join geog.County c on c.StateID = st.State " +
                        "where c.CountyID = 205")) {
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid county id value.", 205, rs.getInt("CountyID"));
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for left join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLeftJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select d.\"DATE\", d.\"TIME\" from fields.DATE7 d " +
                        "left join fields.DATE5 d5 on d5.\"DATE\" = d.\"DATE\"")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-01-01", rs.getDate("DATE").toString());
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-02-01", rs.getDate("DATE").toString());
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-01-02", rs.getDate("DATE").toString());
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertNull("Invalid date value.", rs.getDate("DATE"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-01-01", rs.getDate("DATE").toString());
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for cross join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCrossJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select Id from fields.long cross join fields.bcd")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 1, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 1, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 1, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 2, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 2, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 2, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 3, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 3, rs.getInt("Id"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 3, rs.getInt("Id"));
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for right join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testRightJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select d5.\"DATE\", d5.\"TIME\" from fields.DATE5 d5 " +
                        " right join fields.DATE7 d on d.\"DATE\" = d5.\"DATE\"")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-01-01", rs.getDate("DATE").toString());
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-02-01", rs.getDate("DATE").toString());
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-01-02", rs.getDate("DATE").toString());
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertNull("Invalid date value.", rs.getDate("DATE"));
            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", "2018-01-01", rs.getDate("DATE").toString());
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for cross join with WHERE clause.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCrossJoinWithWhere() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select Id from fields.long cross join fields.bcd where Id = B")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 1, rs.getInt("Id"));
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    /**
     * Test for manual join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testManualJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select Id from fields.long, fields.bcd where Id = B")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid date value.", 1, rs.getInt("Id"));
            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }
}
