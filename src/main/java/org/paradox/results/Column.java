package org.paradox.results;

import org.paradox.ParadoxResultSet;
import org.paradox.utils.SQLStates;
import java.sql.SQLException;
import java.sql.Types;
import org.paradox.metadata.ParadoxField;

/**
 * Column Values from a ResultSet
 *
 * @author Leonardo Alves da Costa
 * @since 02/12/2009
 * @version 1.0
 * @see ParadoxResultSet
 */
public class Column {

    public static String getTypeName(final int type) throws SQLException {
        switch (type) {
            case Types.VARCHAR:
                return "VARCHAR";
            case Types.DATE:
                return "DATE";
            case Types.INTEGER:
                return "INTEGER";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.NUMERIC:
                return "NUMERIC";
            case Types.BOOLEAN:
                return "BOOLEAN";
            case Types.BLOB:
                return "BLOB";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
            case Types.BINARY:
                return "BINARY";
            default:
                throw new SQLException("Type Unknown", SQLStates.TYPE_NOT_FOUND);
        }
    }

    private ParadoxField field;
    /**
     * Column index
     */
    private int index;
    /**
     * Column Name
     */
    private String name;
    /**
     * The SQL Data Type
     * 
     * @see Types
     */
    private int type;
    private boolean autoIncrement = false;
    private int scale = 0;
    private int precision = 0;
    private String tableName;
    private boolean currency = false;
    private boolean nullable = true;
    private boolean readOnly = false;
    private boolean searchable = true;
    private boolean signed = false;
    private boolean writeable = false;
    private int maxSize = 255;

    public Column() {
    }

    public Column(final ParadoxField field) {
        this.field = field;
    }

    public Column(final String name, final int type) {
        this.name = name;
        this.type = type;
    }

    public ParadoxField getField() {
        return field;
    }

    public void setField(ParadoxField field) {
        this.field = field;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the autoIncrement
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * @param autoIncrement the autoIncrement to set
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the currency
     */
    public boolean isCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(boolean currency) {
        this.currency = currency;
    }

    /**
     * @return the nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * @param nullable the nullable to set
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly the readOnly to set
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return the searchable
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * @param searchable the searchable to set
     */
    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    /**
     * @return the signed
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * @param signed the signed to set
     */
    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    /**
     * @return the writeable
     */
    public boolean isWriteable() {
        return writeable;
    }

    /**
     * @param writeable the writeable to set
     */
    public void setWriteable(boolean writeable) {
        this.writeable = writeable;
    }

    /**
     * @return the maxSize
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @param maxSize the maxSize to set
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

}
