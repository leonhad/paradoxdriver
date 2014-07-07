package org.paradox.parser;

import java.util.ArrayList;

/**
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 16/03/2009
 */
public interface SQLValues {
    public ArrayList<String> getFields();
    public ArrayList<SQLPair> getWhere();
    public ArrayList<String> getFrom();
}
