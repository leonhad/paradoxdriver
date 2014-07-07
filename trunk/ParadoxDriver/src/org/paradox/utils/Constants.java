package org.paradox.utils;


public class Constants {

    /**
     * Prefixo do driver
     */
    public static final String URL_PREFIX = "jdbc:paradox:";
    /**
     * Versão maior
     */
    public static final int MAJOR_VERSION = 1;
    /**
     * Versão menor
     */
    public static final int MINOR_VERSION = 0;
    
    public static final String DRIVER_NAME = "Paradox (OpenParadox)";

    public static final String DRIVER_VERSION = MAJOR_VERSION + "." + MINOR_VERSION;

    private Constants() {
    }
}
