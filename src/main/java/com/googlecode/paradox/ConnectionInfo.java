/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox;

import com.googlecode.paradox.data.filefilters.DirectoryFilter;
import com.googlecode.paradox.utils.Expressions;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores the connection information properties.
 *
 * @version 1.0
 * @since 1.6.0
 */
public final class ConnectionInfo {

    public static final String INFORMATION_SCHEMA = "information_schema";

    /**
     * Driver URL.
     */
    private String url;
    /**
     * Default charset.
     */
    private Charset charset;
    /**
     * Connection locale.
     */
    private Locale locale;
    /**
     * Time zone.
     */
    private TimeZone timeZone;
    /**
     * BCD field precision.
     */
    private boolean bcdRounding;
    /**
     * The current connection schema.
     */
    private File currentSchema;
    /**
     * The current catalog.
     */
    private File currentCatalog;
    /**
     * This connection holdability.
     */
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;

    /**
     * Creates a new instance.
     */
    public ConnectionInfo(final String url) {
        this.url = url;
    }

    /**
     * List the connections schema in selected catalog.
     *
     * @param catalog       the database catalog.
     * @param schemaPattern the schema pattern.
     * @return the schema directories.
     */
    public List<File> getSchemaFiles(final String catalog, final String schemaPattern) {
        if (catalog == null || getCatalog().equalsIgnoreCase(catalog)) {
            final File[] schemas = this.currentCatalog.listFiles(new DirectoryFilter(this.locale, schemaPattern));
            if (schemas != null) {
                return Stream.of(schemas)
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(a -> a))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    /**
     * List the connections schema in selected catalog.
     *
     * @param catalog       the database catalog.
     * @param schemaPattern the schema pattern.
     * @return the schema directories.
     */
    public List<String> getSchemas(final String catalog, final String schemaPattern) {
        final List<String> ret = new ArrayList<>();
        if (catalog == null || getCatalog().equalsIgnoreCase(catalog)) {
            final File[] schemas = this.currentCatalog.listFiles(
                    new DirectoryFilter(this.locale, schemaPattern));
            if (schemas != null) {
                ret.addAll(Stream.of(schemas).filter(Objects::nonNull).map(File::getName).collect(Collectors.toList()));
            }

            if (schemaPattern == null
                    || Expressions.accept(this.locale, INFORMATION_SCHEMA,
                    schemaPattern, false, '\\')) {
                ret.add(INFORMATION_SCHEMA);
            }

            ret.sort(Comparator.comparing(a -> a));
        }

        return ret;
    }

    /**
     * Gets the URL connection.
     *
     * @return the URL connection
     */
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the default charset.
     *
     * @return the default charset.
     */
    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Gets the connection locale.
     *
     * @return the connection locale.
     */
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the BCD rounding.
     *
     * @return <code>true</code> if the BCD rounding is enabled.
     */
    public boolean isBcdRounding() {
        return bcdRounding;
    }

    public void setBcdRounding(boolean bcdRounding) {
        this.bcdRounding = bcdRounding;
    }

    /**
     * Gets the connection time zone.
     *
     * @return the connection time zone.
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Gets the current schema directory.
     *
     * @return the current schema directory.
     */
    public File getCurrentSchema() {
        return this.currentSchema;
    }

    /**
     * Sets the current connection schema.
     *
     * @param currentSchema the current connection schema.
     */
    public void setCurrentSchema(File currentSchema) {
        this.currentSchema = currentSchema;
    }

    public String getSchema() {
        return this.currentSchema.getName();
    }

    public String getCatalog() {
        return currentCatalog.getName();
    }

    public File getCurrentCatalog() {
        return currentCatalog;
    }

    public void setCurrentCatalog(File currentCatalog) {
        this.currentCatalog = currentCatalog;
    }

    public int getHoldability() {
        return holdability;
    }

    public void setHoldability(int holdability) {
        this.holdability = holdability;
    }
}