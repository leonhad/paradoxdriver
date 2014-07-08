package org.paradox.utils;

/**
 * Store the SQL States
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 14/03/2009
 */
public interface SQLStates {
    public static final String DIR_NOT_FOUND = "1001";
    public static final String CHANGE_CATALOG_NOT_SUPPORTED = "1003";
    public static final String TYPE_NOT_FOUND = "1004";
    public static final String INVALID_PARAMETER = "1005";
    public static final String INVALID_COMMAND = "1006";
    public static final String INVALID_SQL = "1007";
    public static final String INVALID_ROW = "1008";
    public static final String RESULTSET_CLOSED = "1009";
    public static final String INVALID_COLUMN = "1010";
    public static final String INVALID_STATE = "1011";
    public static final String INVALID_FIELD_VALUE = "1012";
}
