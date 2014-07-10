package org.paradox.data;

import java.sql.DriverManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.paradox.Driver;
import org.paradox.ParadoxConnection;
import org.paradox.metadata.ParadoxField;
import org.paradox.test.MainTest;

/**
 * 
 * @author 72330554168
 */
public class ViewDataTest {

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
	public void testListViews() throws Exception {
		ViewData.listViews(conn);
	}

	@Test
	public void testParseExpression() throws Exception {
		final ParadoxField field = new ParadoxField();
		ViewData.parseExpression(field, "_PC, CALC _PC*_QTD AS CUSTOTOTAL");
		Assert.assertEquals(true, field.isChecked());
		Assert.assertEquals("_PC", field.getJoinName());
		Assert.assertEquals("CALC _PC*_QTD", field.getExpression());
		Assert.assertEquals("CUSTOTOTAL", field.getAlias());

		Assert.assertTrue(field.isChecked());
	}
}
