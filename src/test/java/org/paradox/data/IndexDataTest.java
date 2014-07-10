package org.paradox.data;

import java.sql.DriverManager;

import org.junit.Before;
import org.junit.Test;
import org.paradox.Driver;
import org.paradox.ParadoxConnection;
import org.paradox.test.MainTest;

/**
 * Test for IndexData
 * 
 * @author Leonardo Alves da Costa
 * @since 14/03/2009
 * @version 1.1
 */
public class IndexDataTest {

	@Before
	public void setUp() throws ClassNotFoundException {
		Class.forName(Driver.class.getName());
	}

	@Test
	public void testListIndexes() throws Exception {
		final ParadoxConnection conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
		IndexData.listIndexes(conn, "CLIENTE.db");
		// ArrayList<ParadoxIndex> indexes = listIndexes(conn, "CLIENTE.db");
		// for (final ParadoxIndex index : indexes) {
		// System.out.println(index.getName() + "-" + index.getOrder());
		// }
	}

}
