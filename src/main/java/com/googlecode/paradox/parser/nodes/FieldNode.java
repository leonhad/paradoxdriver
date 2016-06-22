package com.googlecode.paradox.parser.nodes;

/**
 * Stores field values
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 21/07/2014
 */
public class FieldNode extends SQLNode {
	/**
	 * This field table name
	 */
	private final String tableName;
	/**
	 * Field alias (AS)
	 */
	private final String alias;

	/**
	 * Stores field valus (from select statements)
	 *
	 * @param tableName
	 *            this table name
	 * @param fieldName
	 *            field name
	 * @param alias
	 *            field name alias
	 */
	public FieldNode(final String tableName, final String fieldName, final String alias) {
		super(fieldName);
		this.tableName = tableName;
		this.alias = alias;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		if (tableName != null) {
			builder.append(tableName);
			builder.append(".");
		}
		builder.append(getName());
		if (!getName().equals(alias)) {
			builder.append(" AS ");
			builder.append(alias);
		}
		return builder.toString();
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public String getAlias() {
		return alias;
	}

}
