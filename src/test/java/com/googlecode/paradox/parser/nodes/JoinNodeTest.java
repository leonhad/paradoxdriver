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
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test {@link JoinNode} class.
 *
 * @since 1.3
 */
class JoinNodeTest {

    /**
     * The connection string used in  tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeAll
    static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for new instance.
     */
    @Test
    void testInstance() {
        final JoinNode node = new JoinNode(null, null, null, JoinType.INNER, null);
        assertNull(node.getName());
    }

    /**
     * Test join type.
     */
    @Test
    void testJoinType() {
        final JoinNode node = new JoinNode(null, null, null, JoinType.LEFT, null);
        assertEquals(JoinType.LEFT, node.getJoinType());
    }

    /**
     * Test for inner join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select c.CountyID, ac.AreaCode " +
                        "from geog.tblAC ac " +
                        "     join geog.tblsttes st on st.State = ac.State " +
                        "     inner join geog.County c on c.StateID = st.State " +
                        "where c.CountyID = 205")) {
            assertTrue(rs.next());
            assertEquals(205, rs.getInt("CountyID"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for left join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLeftJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select d.\"DATE\", d.\"TIME\" from fields.DATE7 d " +
                        "left join fields.DATE5 d5 on d5.\"DATE\" = d.\"DATE\"")) {

            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getDate("DATE").toString());
            assertTrue(rs.next());
            assertEquals("2018-02-01", rs.getDate("DATE").toString());
            assertTrue(rs.next());
            assertEquals("2018-01-02", rs.getDate("DATE").toString());
            assertTrue(rs.next());
            assertNull(rs.getDate("DATE"));
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getDate("DATE").toString());
            assertFalse(rs.next());
        }
    }

    /**
     * Test for cross join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCrossJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select Id from fields.long cross join fields.bcd")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(2, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(2, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(2, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(3, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(3, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(3, rs.getInt("Id"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test count for cross join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCrossJoinCount() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select count(*) from fields.long cross join fields.bcd")) {

            assertTrue(rs.next());
            assertEquals(9, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for right join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testRightJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select d5.\"DATE\", d5.\"TIME\" from fields.DATE5 d5 " +
                        " right join fields.DATE7 d on d.\"DATE\" = d5.\"DATE\"")) {

            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getDate("DATE").toString());
            assertTrue(rs.next());
            assertEquals("2018-02-01", rs.getDate("DATE").toString());
            assertTrue(rs.next());
            assertEquals("2018-01-02", rs.getDate("DATE").toString());
            assertTrue(rs.next());
            assertNull(rs.getDate("DATE"));
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getDate("DATE").toString());
            assertFalse(rs.next());
        }
    }

    /**
     * Test for cross join with WHERE clause.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCrossJoinWithWhere() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select Id from fields.long cross join fields.bcd where Id = B")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for manual join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testManualJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select Id from fields.long, fields.bcd where Id = B")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt("Id"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for full join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFullJoin() throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "select a.id a, b.id b from joins.joina A full join joins.joinb b on b.Id = a.ID")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt("a"));
            assertNull(rs.getObject("b"));
            assertTrue(rs.next());
            assertEquals(2, rs.getInt("a"));
            assertEquals(2, rs.getInt("b"));
            assertTrue(rs.next());
            assertEquals(3, rs.getInt("a"));
            assertEquals(3, rs.getInt("b"));
            assertTrue(rs.next());
            assertNull(rs.getObject("a"));
            assertEquals(4, rs.getInt("b"));
            assertFalse(rs.next());
        }
    }

}
