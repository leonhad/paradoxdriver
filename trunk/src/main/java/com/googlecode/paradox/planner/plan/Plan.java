package com.googlecode.paradox.planner.plan;

import java.sql.SQLException;

public interface Plan {
	public void execute() throws SQLException;
}
