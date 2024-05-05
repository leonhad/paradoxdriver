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

package com.googlecode.paradox.metadata.tables;

import com.googlecode.paradox.function.AbstractFunction;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Index;
import com.googlecode.paradox.metadata.Schema;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.results.Column;

/**
 * Table details metadata.
 *
 * @since 1.6.2
 */
public class TableDetails {

    /**
     * The table schema.
     */
    private Schema schema;

    /**
     * The associated table.
     */
    private Table table;

    /**
     * the current field.
     */
    private Field currentField;

    /**
     * The table index.
     */
    private Index index;

    /**
     * The associated function.
     */
    private AbstractFunction function;

    /**
     * The associated function name.
     */
    private String functionName;

    /**
     * The column.
     */
    private Column column;

    /**
     * Gets the schema.
     *
     * @return the schema.
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Sets the schema.
     *
     * @param schema the schema.
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    /**
     * Gets the table.
     *
     * @return the table.
     */
    public Table getTable() {
        return table;
    }

    /**
     * Sets the table.
     *
     * @param table the table.
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Gets the current field.
     *
     * @return the current field.
     */
    public Field getCurrentField() {
        return currentField;
    }

    /**
     * Sets the current field.
     *
     * @param currentField the current field.
     */
    public void setCurrentField(Field currentField) {
        this.currentField = currentField;
    }

    /**
     * Gets the index.
     *
     * @return the index.
     */
    public Index getIndex() {
        return index;
    }

    /**
     * Sets the index.
     *
     * @param index the index.
     */
    public void setIndex(Index index) {
        this.index = index;
    }

    /**
     * Gets the associated function.
     *
     * @return the associated function.
     */
    public AbstractFunction getFunction() {
        return function;
    }

    /**
     * Sets the associated function.
     *
     * @param function the associated function.
     */
    public void setFunction(AbstractFunction function) {
        this.function = function;
    }

    /**
     * Gets the associated function name.
     *
     * @return the associated function name.
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Sets the associated function name.
     *
     * @param functionName the associated function name.
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Gets the column.
     *
     * @return the column.
     */
    public Column getColumn() {
        return column;
    }

    /**
     * Sets the column.
     *
     * @param column the column.
     */
    public void setColumn(Column column) {
        this.column = column;
    }
}
