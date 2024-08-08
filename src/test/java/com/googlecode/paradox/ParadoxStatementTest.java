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
package com.googlecode.paradox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for Paradox Statement.
 *
 * @since 1.6.0
 */
class ParadoxStatementTest {

    /**
     * The connection string used in tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Register the database driver.
     *
     * @throws Exception in case of failures.
     */
    @BeforeAll
    static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @AfterEach
    void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @BeforeEach
    void connect() throws Exception {
        this.conn = DriverManager.getConnection(CONNECTION_STRING);
    }

    /**
     * Test for max rows statement.
     */
    @Test
    void testAbsoluteEmpty() throws SQLException {
        final int totalRows = 10;
        try (final Statement statement = conn.createStatement()) {
            statement.setMaxRows(totalRows);

            try (final ResultSet rs = statement.executeQuery("SELECT * FROM db.HERCULES")) {
                int total = 0;
                while (rs.next()) {
                    total++;
                }

                assertEquals(totalRows, total);
            }
        }
    }
}
