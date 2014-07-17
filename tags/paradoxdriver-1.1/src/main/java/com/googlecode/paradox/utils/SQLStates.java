package com.googlecode.paradox.utils;

/**
 * Store the SQL States
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 14/03/2009
 */
public interface SQLStates {
    /**
     * Used when some directory is not found
     */
    String DIR_NOT_FOUND = "1001";
    /**
     * This driver can't change catalog.
     */
    String CHANGE_CATALOG_NOT_SUPPORTED = "1003";
    String TYPE_NOT_FOUND = "1004";
    String INVALID_PARAMETER = "1005";
    String INVALID_COMMAND = "1006";
    String INVALID_SQL = "1007";
    String INVALID_ROW = "1008";
    String RESULTSET_CLOSED = "1009";
    String INVALID_COLUMN = "1010";
    String INVALID_STATE = "1011";
    String INVALID_FIELD_VALUE = "1012";
}
