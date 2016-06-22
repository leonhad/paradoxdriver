package com.googlecode.paradox.parser.planner;

import java.sql.DriverManager;

import com.googlecode.paradox.planner.plan.SelectPlan;
import org.junit.*;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.planner.Planner;
import com.googlecode.paradox.integration.MainTest;

public class PlannerTest {
	public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";
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
	public void tableTest() throws Exception {
		final SQLParser parser = new SQLParser("select * from areacodes a");
		final Planner planner = new Planner(conn);
		SelectPlan plan = (SelectPlan)planner.create(parser.parse().get(0));
		Assert.assertNotNull("No columns", plan.getColumns());
		Assert.assertEquals("Num of columns in table", 3, plan.getColumns().size());
		Assert.assertEquals("First column not 'AC'", "AC", plan.getColumns().get(0).getName());
		Assert.assertEquals("Second column not 'State'", "STATE", plan.getColumns().get(1).getName());
		Assert.assertEquals("Third column not 'Cities'", "CITIES", plan.getColumns().get(2).getName());

	}
}
