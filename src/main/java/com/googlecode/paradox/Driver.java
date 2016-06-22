package com.googlecode.paradox;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.paradox.utils.Constants;

/**
 * PARADOX JDBC type 4 Driver.
 *
 * @author Leonardo Alves da Costa
 * @version 2.2
 * @since 14/03/2009
 */
public class Driver implements java.sql.Driver {

	/**
	 * Driver properties, if has some.
	 */
	private Properties properties = null;

	/**
	 * Logger instance for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

	/**
	 * Register the drive into JDBC API.
	 */
	static {
		try {
			// Register The Paradox Driver
			final Driver driverInst = new Driver();
			DriverManager.registerDriver(driverInst);
		} catch (final SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection connect(final String url, final Properties info) throws SQLException {
		if (acceptsURL(url)) {
			final String dirName = url.substring(Constants.URL_PREFIX.length(), url.length());
			return new ParadoxConnection(new File(dirName), url, info);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean acceptsURL(final String url) throws SQLException {
		return url.startsWith(Constants.URL_PREFIX);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
		if (properties == null) {
			properties = new Properties();
		}
		final ArrayList<DriverPropertyInfo> prop = new ArrayList<DriverPropertyInfo>();

		if (info.getProperty("DBNAME") == null) {
			final DriverPropertyInfo dbProp = new DriverPropertyInfo("name", properties.getProperty("name"));
			dbProp.required = false;
			dbProp.description = "Database name";
			prop.add(dbProp);
		}
		if (info.getProperty("password") == null) {
			final DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", "");
			passwordProp.required = false;
			passwordProp.description = "Password to use for authentication";
			prop.add(passwordProp);
		}
		final DriverPropertyInfo dpi[] = new DriverPropertyInfo[prop.size()];
		return prop.toArray(dpi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMajorVersion() {
		return Constants.MAJOR_VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinorVersion() {
		return Constants.MINOR_VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return Driver.LOGGER;
	}
}
