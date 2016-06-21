package com.googlecode.paradox.metadata;

import com.googlecode.paradox.utils.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Defines the paradox table default structure
 *
 * @author Leonardo Alves da Costa
 * @since 03/12/2009
 * @version 1.1
 */
public abstract class AbstractTable {

	private final File file;
	private String name;

	public AbstractTable(final File file, final String name) {
		this.file = file;
		this.name = StringUtils.removeDb(name);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public abstract boolean isValid();

	public abstract List<ParadoxField> getFields();

	public ParadoxField findField(String name) {
		if (getFields() != null) {
			for (ParadoxField field: getFields()) {
				if (field.getName().equalsIgnoreCase(name))
					return field;
			}
		}
		return null;
	}
}
