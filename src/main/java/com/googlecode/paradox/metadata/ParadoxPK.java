package com.googlecode.paradox.metadata;

/**
 *
 * @author 72330554168
 */
public class ParadoxPK extends ParadoxDataFile {
	private int indexFieldNumber;

	public ParadoxPK() {
		super(null, null);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ParadoxPK) {
			return getName().equals(((ParadoxPK) obj).getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public int getIndexFieldNumber() {
		return indexFieldNumber;
	}

	public void setIndexFieldNumber(final int indexFieldNumber) {
		this.indexFieldNumber = indexFieldNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return true;
	}
}
