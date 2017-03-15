/*
 * BlobTest.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.integration;

import com.googlecode.paradox.ParadoxConnection;
import java.sql.Clob;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Integration test for BLOB type.
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @since 1.2
 * @version 1.1
 */
@Category(IntegrationTest.class)
public class BlobTest {
    
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
     * Test for BLOB reading.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testReadBlob() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = this.conn.createStatement();
            
            rs = stmt.executeQuery("SELECT comments FROM customer");
            
            Assert.assertTrue("First record not exists", rs.next());
            Assert.assertNotNull("First comment is null", rs.getClob("comments"));
            Assert.assertEquals("Small comment (less 100 symbols)",
                    rs.getClob("comments").getSubString(1, (int) rs.getClob("comments").length()));
            Assert.assertTrue("Second record not exists", rs.next());
            Assert.assertNotNull("Second comment is null", rs.getClob("comments"));
            Assert.assertEquals("2 row: Medium comment (about 500 symbols)", 518, rs.getClob("comments").length());
            Assert.assertTrue("2 row: Start with:\tIf you define", rs.getClob("comments")
                    .getSubString(1, (int) rs.getClob("comments").length()).startsWith("\tThe length of the"));
            Assert.assertTrue("2 row: End with: later in this document.", rs.getClob("comments")
                    .getSubString(1, (int) rs.getClob("comments").length()).endsWith("later in this document."));
            Assert.assertTrue("Third record not exists", rs.next());
            Assert.assertNotNull("Third comment is null", rs.getClob("comments"));
            Assert.assertEquals("3 row: Medium comment (318 symbols)", 318, rs.getClob("comments").length());
            Assert.assertTrue("3 row: Start with:mvn",
                    rs.getClob("comments").getSubString(1, (int) rs.getClob("comments").length()).startsWith("mvn"));
            
            Assert.assertTrue("Fourth record not exists", rs.next());
            Assert.assertNotNull("Fourth comment is null", rs.getClob("comments"));
            Assert.assertEquals("4 row: Big comment (56864 symbols)", 56864, rs.getClob("comments").length());
            
            Assert.assertTrue("Five record not exists", rs.next());
            Assert.assertNotNull("Five comment is null", rs.getClob("comments"));
            Assert.assertEquals("5 row: Small comment (415 symbols)", 426, rs.getClob("comments").length());
            
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (this.conn != null) {
                this.conn.close();
            }
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
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = this.conn.createStatement();
            
            rs = stmt.executeQuery("SELECT note FROM note1251 WHERE id=2");
            
            Assert.assertTrue("Nation locale: record not exists", rs.next());
            final Clob c = rs.getClob("note");
            final String expected =
                    "При разработке электронных сервисов необходимо придерживаться следующих спецификаций:\r\n"
                            + "\tспецификация универсального описания, поиска и интеграции электронных сервисов Universal Description "
                            + "Discovery and Integration (UDDI) версии 2.0 - стандарт Организации по развитию стандартов "
                            + "структурированной информации Organization for the Advancement of Structured Information Standards "
                            + "(OASIS) - спецификация носит обязательный характер;\r\n";
            final String real = c.getSubString(1, (int) c.length());
            Assert.assertEquals("Unexpected cp1251 text", expected, real);
            
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (this.conn != null) {
                this.conn.close();
            }
        }
    }
}
