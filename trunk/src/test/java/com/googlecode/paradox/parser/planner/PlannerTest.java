package com.googlecode.paradox.parser.planner;

import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.planner.Planner;
import com.googlecode.paradox.test.MainTest;

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
		planner.create(parser.parse().get(0));
	}
}
