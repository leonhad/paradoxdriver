package com.googlecode.paradox.test;

import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import org.junit.*;

import java.sql.*;
import java.util.ArrayList;

public class BlobTest {

	private ParadoxConnection conn;

	@BeforeClass
	public static void setUp() throws Exception {
		Class.forName(Driver.class.getName());
	}

	@Before
	public void connect() throws Exception {
		conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
	}

	@After
	public void closeConnection() throws Exception {
		if (conn != null) {
			conn.close();
		}
	}

	@Test
	public void testReadBlob() throws Exception {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			rs = stmt.executeQuery("SELECT comments FROM customer");

			Assert.assertTrue("First record not exists", rs.next());
			Assert.assertNotNull("First comment is null", rs.getClob("comments"));
			Assert.assertEquals("Small comment (less 100 symbols)", rs.getClob("comments").getSubString(1,
					(int) rs.getClob("comments").length()));
			Assert.assertTrue("Second record not exists", rs.next());
			Assert.assertNotNull("Second comment is null", rs.getClob("comments"));
			Assert.assertEquals("2 row: Medium comment (about 500 symbols)", 518, rs.getClob("comments").length());
			Assert.assertTrue("2 row: Start with:\tIf you define", rs.getClob("comments").getSubString(1, (int) rs.getClob("comments").length())
					.startsWith("\tThe length of the"));
			Assert.assertTrue("2 row: End with: later in this document.", rs.getClob("comments").getSubString(1, (int) rs.getClob("comments").length())
					.endsWith("later in this document."));
			Assert.assertTrue("Third record not exists", rs.next());
			Assert.assertNotNull("Third comment is null", rs.getClob("comments"));
			Assert.assertEquals("3 row: Medium comment (318 symbols)", 318, rs.getClob("comments").length());
			Assert.assertTrue("3 row: Start with:mvn", rs.getClob("comments").getSubString(1, (int) rs.getClob("comments").length())
					.startsWith("mvn"));

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
			if (conn != null) {
				conn.close();
			}
		}
	}
    @Test
    public void testReadBlob1251() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT note FROM note1251 WHERE id=2");

            Assert.assertTrue("Nation locale: record not exists", rs.next());
            Clob c = rs.getClob("note");
            String expected = "При разработке электронных сервисов необходимо придерживаться следующих спецификаций:\r\n" +
                    "\tспецификация универсального описания, поиска и интеграции электронных сервисов Universal Description " +
                    "Discovery and Integration (UDDI) версии 2.0 - стандарт Организации по развитию стандартов " +
                    "структурированной информации Organization for the Advancement of Structured Information Standards " +
                    "(OASIS) - спецификация носит обязательный характер;\r\n";
            // String expected = "Удивительное устройство USB-флешки Kingston DataTraveler";
            String real = c.getSubString(1, (int) c.length());
            Assert.assertEquals("Unexpect cp1251 text", expected, real);

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}
