package com.googlecode.paradox.metadata;

import static java.nio.charset.Charset.forName;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 *
 * @author 72330554168
 */
public class ParadoxIndex extends ParadoxDataFile {

	private String sortOrderID;
	private String fatherName;
	private ArrayList<Short> fieldsOrder;
	private Charset charset = forName("Cp437");

	public ParadoxIndex(final File file, final String name) {
		super(file, name);
	}

	public ArrayList<ParadoxField> getPrimaryKeys() {
		final ArrayList<ParadoxField> ret = new ArrayList<ParadoxField>();
		for (int loop = 0; loop < primaryFieldCount; loop++) {
			ret.add(fields.get(loop));
		}
		return ret;
	}

	public String getOrder() {
		switch (referencialIntegrity) {
		case 0:
		case 1:
		case 0x20:
		case 0x21:
			return "A";
		case 0x10:
		case 0x11:
		case 0x30:
			return "D";
		default:
			return "A";
		}
	}

	/**
	 * If this table is valid
	 *
	 * @return true if this table is valid
	 */
	@Override
	public boolean isValid() {
		switch (type) {
		case 3:
		case 5:
		case 6:
		case 8:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return the fieldsOrder
	 */
	public ArrayList<Short> getFieldsOrder() {
		return fieldsOrder;
	}

	/**
	 * @return the charset
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * @param charset
	 *            the charset to set
	 */
	public void setCharset(final Charset charset) {
		this.charset = charset;
	}

	/**
	 * @return the sortOrderID
	 */
	public String getSortOrderID() {
		return sortOrderID;
	}

	/**
	 * @param sortOrderID
	 *            the sortOrderID to set
	 */
	public void setSortOrderID(final String sortOrderID) {
		this.sortOrderID = sortOrderID;
	}

	/**
	 * @return the fatherName
	 */
	public String getFatherName() {
		return fatherName;
	}

	/**
	 * @param fatherName
	 *            the fatherName to set
	 */
	public void setFatherName(final String fatherName) {
		this.fatherName = fatherName;
	}

	/**
	 * @param fieldsOrder
	 *            the fieldsOrder to set
	 */
	public void setFieldsOrder(final ArrayList<Short> fieldsOrder) {
		this.fieldsOrder = fieldsOrder;
	}
}
