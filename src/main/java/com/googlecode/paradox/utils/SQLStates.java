package com.googlecode.paradox.utils;

import java.sql.ResultSet;

/**
 * Store the SQL States.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public interface SQLStates {
    /**
     * This driver can't change catalog.
     */
    String CHANGE_CATALOG_NOT_SUPPORTED = "1003";

    String COLUMN_AMBIQUOUS = "1015";

    /**
     * Used when some directory is not found
     */
    String DIR_NOT_FOUND = "1001";
    String INVALID_COLUMN = "1010";
    String INVALID_COMMAND = "1006";
    String INVALID_FIELD_VALUE = "1012";

    /**
     * There is an error in I/O subsystem.
     */

    String INVALID_IO = "1016";
    /**
     * Invalid parameter format or value.
     */

    String INVALID_PARAMETER = "1005";

    /**
     * Column not found.
     */
    String INVALID_ROW = "1008";
    String INVALID_SQL = "1007";
    String INVALID_STATE = "1011";

    /**
     * Table format is invalid.
     */
    String INVALID_TABLE = "1014";

    /**
     * Error in data load.
     */
    String LOAD_DATA = "1100";

    /**
     * {@link ResultSet} not open for use.
     */
    String RESULTSET_CLOSED = "1009";

    /**
     * Type not valid or unsupported.
     */
    String TYPE_NOT_FOUND = "1004";
}
