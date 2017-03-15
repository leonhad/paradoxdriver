/*
 * ParadoxClobTest.java 07/21/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.rowset;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.integration.MainTest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Clob;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for {@link ParadoxClob} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ParadoxClobTest {
    /**
     * The database connection.
     */
    private ParadoxConnection conn;
    
    /**
     * Register the driver.
     *
     * @throws ClassNotFoundException
     *             in case of connection errors.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }
    
    /**
     * Used to close the test connection.
     *
     * @throws Exception
     *             in case closing of errors.
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
     * @throws Exception
     *             in case of connection errors.
     */
    @Before
    public void connect() throws Exception {
        this.conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }
    
    /**
     * Test for {@link Clob#getAsciiStream()} method.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testAsciiStream() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            final BufferedReader reader = new BufferedReader(new InputStreamReader(clob.getAsciiStream()));
            Assert.assertEquals("Testing for input stream value.", "Small comment (less 100 symbols)",
                    reader.readLine());
        }
    }
    
    /**
     * Test for {@link Clob#getCharacterStream()} method.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testCharacterStream() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            final BufferedReader reader = new BufferedReader(clob.getCharacterStream());
            Assert.assertEquals("Testing for input stream value.", "Small comment (less 100 symbols)",
                    reader.readLine());
        }
    }
    
    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with high position.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testCharacterStreamWithHighPosition() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getCharacterStream(100, 3);
        }
    }
    
    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with invalid length.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testCharacterStreamWithInvalidLength() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getCharacterStream(1, -1);
        }
    }
    
    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with long length.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testCharacterStreamWithLongLength() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getCharacterStream(1, 100);
        }
    }
    
    /**
     * Test for {@link Clob#getCharacterStream(long, long)} with low position.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testCharacterStreamWithLowPosition() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getCharacterStream(0, 3);
        }
    }
    
    /**
     * Test for {@link Clob#getCharacterStream(long, long)} method.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testCharacterStreamWithParameters() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            final BufferedReader reader = new BufferedReader(clob.getCharacterStream(1, 3));
            Assert.assertEquals("Testing for input stream value.", "Sma", reader.readLine());
        }
    }
    
    /**
     * Test for {@link ResultSet#getClob(String)} method.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testReadBlob() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            Assert.assertEquals("Fields are not equals.", rs.getClob(1), rs.getClob("comments"));
            Assert.assertTrue("Second record not exists", rs.next());
            Assert.assertNotNull("Second comment is null", rs.getClob("comments"));
            Assert.assertEquals("2 row: Medium comment (about 500 symbols)", 518, rs.getClob("comments").length());
            Assert.assertTrue("Third record not exists", rs.next());
            Assert.assertNotNull("Third comment is null", rs.getClob("comments"));
            Assert.assertEquals("3 row: Medium comment (318 symbols)", 318, rs.getClob("comments").length());
            Assert.assertTrue("Fourth record not exists", rs.next());
            Assert.assertNotNull("Fourth comment is null", rs.getClob("comments"));
            Assert.assertEquals("4 row: Big comment (56864 symbols)", 56864, rs.getClob("comments").length());
            Assert.assertTrue("Five record not exists", rs.next());
            Assert.assertNotNull("Five comment is null", rs.getClob("comments"));
            Assert.assertEquals("5 row: Small comment (415 symbols)", 426, rs.getClob("comments").length());
        }
    }
    
    /**
     * Test for CLOB with cp1251 charset.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testReadBlob1251() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT note FROM note1251 WHERE id=2")) {
            
            Assert.assertTrue("Nation locale: record not exists", rs.next());
            final Clob c = rs.getClob("note");
            final String expected =
                    "При разработке электронных сервисов необходимо придерживаться следующих " + "спецификаций:\r\n"
                            + "\tспецификация универсального описания, поиска и интеграции электронных сервисов Universal "
                            + "Description "
                            + "Discovery and Integration (UDDI) версии 2.0 - стандарт Организации по развитию стандартов "
                            + "структурированной информации Organization for the Advancement of Structured Information "
                            + "Standards (OASIS) - спецификация носит обязательный характер;\r\n";
            
            final String real = c.getSubString(1, (int) c.length());
            Assert.assertEquals("Testing for cp1251 text.", expected, real);
        }
    }
    
    /**
     * Test for {@link Clob#getSubString(long, int)} method.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testSubString() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            Assert.assertEquals("Testing for input stream value.", "Sma", clob.getSubString(1, 3));
        }
    }
    
    /**
     * Test for {@link Clob#getSubString(long, int)} method with high length.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testSubStringWithHighLength() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getSubString(1, 100);
        }
    }
    
    /**
     * Test for {@link Clob#getSubString(long, int)} method with high position.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testSubStringWithHighPos() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getSubString(100, 3);
        }
    }
    
    /**
     * Test for {@link Clob#getSubString(long, int)} method with invalid length.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testSubStringWithInvalidLength() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getSubString(1, -1);
        }
    }
    
    /**
     * Test for {@link Clob#getSubString(long, int)} method with low position.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testSubStringWithLowPos() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.getSubString(0, 3);
        }
    }
    
    /**
     * Test for {@link Clob#truncate(long)} method.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testTruncate() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.truncate(3);
            Assert.assertEquals("Testing for truncate.", 3, clob.length());
        }
    }
    
    /**
     * Test for {@link Clob#truncate(long)} method with high value.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testTruncateHighValue() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            final long value = clob.length();
            clob.truncate(value + 100);
        }
    }
    
    /**
     * Test for {@link Clob#truncate(long)} method with zero size.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testTruncateWithZeroSize() throws Exception {
        try (Statement stmt = this.conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT comments FROM customer")) {
            Assert.assertTrue("First record not exists", rs.next());
            
            final Clob clob = rs.getClob("comments");
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            
            clob.truncate(0);
            Assert.assertEquals("Testing for truncate.", 0, clob.length());
        }
    }
}
