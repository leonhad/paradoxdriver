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
package com.googlecode.paradox.data.charset;

import com.googlecode.paradox.ConnectionInfo;
import com.googlecode.paradox.metadata.paradox.ParadoxDataFile;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The charset lookup and translation util.
 *
 * @since 1.6.2
 */
public class CharsetUtil {

    private static final List<CharsetData> CHARSET_TABLE = new ArrayList<>();

    static {
        final Charset windows1250 = Charset.forName("windows-1250");
        final Charset windows1251 = Charset.forName("windows-1251");
        final Charset windows1252 = Charset.forName("windows-1252");
        final Charset windows1253 = Charset.forName("windows-1253");
        final Charset windows1254 = Charset.forName("windows-1254");
        final Charset windows1255 = Charset.forName("windows-1255");
        final Charset windows31j = Charset.forName("windows-31j");
        final Charset cp437 = Charset.forName("cp437");
        final Charset cp850 = Charset.forName("cp850");
        final Charset cp852 = Charset.forName("cp852");
        final Charset cp857 = Charset.forName("cp857");
        final Charset cp860 = Charset.forName("cp860");
        final Charset cp861 = Charset.forName("cp861");
        final Charset cp862 = Charset.forName("cp862");
        final Charset cp863 = Charset.forName("cp863");
        final Charset cp865 = Charset.forName("cp865");
        final Charset cp866 = Charset.forName("cp866");
        final Charset cp867 = Cp867CharsetProvider.cp867();
        final Charset cp737 = Charset.forName("cp737");
        final Charset cp936 = Charset.forName("cp936");
        final Charset cp1051 = Roman8CharsetProvider.roman8();

        // Borland set
        CHARSET_TABLE.add(new CharsetData(20, 1252, "BLLT1DA0", "Borland DAN Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(24, 1252, "BLLT1DE0", "Borland DEU Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(47, 1252, "BLLT1UK0", "Borland ENG Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(55, 1252, "BLLT1US0", "Borland ENU Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(39, 1252, "BLLT1ES0", "Borland ESP Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(30, 1252, "BLLT1FI0", "Borland FIN Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(30, 1252, "BLLT1FR0", "Borland FRA Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(19, 1252, "BLLT1CA0", "Borland FRC Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(43, 1252, "BLLT1IS0", "Borland ISL Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(44, 1252, "BLLT1IT0", "Borland ITA Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(41, 1252, "BLLT1NL0", "Borland NLD Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(41, 1252, "BLLT1NO0", "Borland NOR Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(51, 1252, "BLLT1PT0", "Borland PTG Latin-1", windows1252));
        CHARSET_TABLE.add(new CharsetData(51, 1252, "BLLT1SV0", "Borland SVE Latin-1", windows1252));

        // dBASE set
        CHARSET_TABLE.add(new CharsetData(242, 850, "DB852CZ0", "dBASE CSY cp852", cp852));
        CHARSET_TABLE.add(new CharsetData(248, 867, "DB867CZ0", "dBASE CSY cp867", cp867));
        CHARSET_TABLE.add(new CharsetData(222, 865, "DB865DA0", "dBASE DAN cp865", cp865));
        CHARSET_TABLE.add(new CharsetData(221, 865, "DB437DE0", "dBASE DEU cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(220, 850, "DB850DE0", "dBASE DEU cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(109, 737, "db437gr0", "dBASE ELL GR437", cp737));
        CHARSET_TABLE.add(new CharsetData(244, 437, "DB437UK0", "dBASE ENG cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(243, 437, "DB850UK0", "dBASE ENG cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(252, 437, "DB437US0", "dBASE ENU cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(251, 850, "DB850US0", "dBASE ENU cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(237, 437, "DB437ES1", "dBASE ESP cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(235, 850, "DB850ES0", "dBASE ESP cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(227, 437, "DB437FI0", "dBASE FIN cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(236, 437, "DB437FR0", "dBASE FRA cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(235, 850, "DB850FR0", "dBASE FRA cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(220, 850, "DB850CF0", "dBASE FRC cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(225, 863, "DB863CF1", "dBASE FRC cp863", cp863));
        CHARSET_TABLE.add(new CharsetData(148, 852, "db852hdc", "dBASE HUN cp852", cp852));
        CHARSET_TABLE.add(new CharsetData(241, 437, "DB437IT0", "dBASE ITA cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(241, 850, "DB850IT1", "dBASE ITA cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(238, 437, "DB437NL0", "dBASE NLD cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(237, 850, "DB850NL0", "dBASE NLD cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(246, 865, "DB865NO0", "dBASE NLD cp865", cp865));
        CHARSET_TABLE.add(new CharsetData(116, 852, "db852po0", "dBASE PLK cp852", cp852));
        CHARSET_TABLE.add(new CharsetData(247, 850, "DB850PT0", "dBASE PTB cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(248, 860, "DB860PT0", "dBASE PTB cp850", cp860));
        CHARSET_TABLE.add(new CharsetData(129, 866, "db866ru0", "dBASE RUS cp866", cp866));
        CHARSET_TABLE.add(new CharsetData(116, 852, "db852sl0", "dBASE SLO cp852", cp852));
        CHARSET_TABLE.add(new CharsetData(253, 437, "DB437SV0", "dBASE SVE cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(253, 850, "DB850SV1", "dBASE SVE cp850", cp850));
        CHARSET_TABLE.add(new CharsetData(112, 437, "db437th0", "dBASE THA cp437", cp437));
        CHARSET_TABLE.add(new CharsetData(0, 857, "DB857TR0", "dBASE TRK cp857", cp857));

        // Others
        CHARSET_TABLE.add(new CharsetData(0x4C, 1252, "DBWINUS0", "'ascii' ANSI", windows1252));
        CHARSET_TABLE.add(new CharsetData(35, 862, "dbHebrew", "Hebrew dBASE", cp862));
        CHARSET_TABLE.add(new CharsetData(27, 850, "ORAWE850", "Oracle SQL WE850", cp862));
        CHARSET_TABLE.add(new CharsetData(60, 1252, "DBWINES0", "'Spanish' ANSI", windows1252));
        CHARSET_TABLE.add(new CharsetData(209, 437, "SYDC437", "Sybase SQL Dic437", cp437));
        CHARSET_TABLE.add(new CharsetData(208, 850, "SYDC850", "Sybase SQL Dic850", cp850));
        CHARSET_TABLE.add(new CharsetData(64, 1252, "DBWINWE0", "'WEurope' ANSI", windows1252));
        CHARSET_TABLE.add(new CharsetData(0, 932, null, "'Japanese'", windows31j));
        CHARSET_TABLE.add(new CharsetData(106, 936, null, "'Simplified Chinese'", cp936));
        CHARSET_TABLE.add(new CharsetData(20, 0, "BLROM800", "SQL Link ROMAN8", cp1051));

        // Paradox set
        CHARSET_TABLE.add(new CharsetData(76, 1255, "ANHEBREW", "Paradox ANSI HEBREW", windows1255));
        CHARSET_TABLE.add(new CharsetData(0, 850, "ascii", "Paradox 'ascii'", cp850));
        CHARSET_TABLE.add(new CharsetData(192, 866, "cyrr", "Paradox Cyrr 866", cp866));
        CHARSET_TABLE.add(new CharsetData(13, 852, "czech", "Paradox Czech 852", cp852));
        CHARSET_TABLE.add(new CharsetData(266, 867, "cskamen", "Paradox Czech 867", cp867));
        CHARSET_TABLE.add(new CharsetData(22, 437, "SPANISH", "Paradox ESP 437", cp437));
        CHARSET_TABLE.add(new CharsetData(74, 737, "grcp437", "Paradox Greek GR437", cp737));
        CHARSET_TABLE.add(new CharsetData(125, 862, "hebrew", "Paradox 'hebrew'", cp862));
        CHARSET_TABLE.add(new CharsetData(177, 852, "hun852dc", "Paradox Hun 852 DC", cp852));
        CHARSET_TABLE.add(new CharsetData(183, 437, "intl", "Paradox 'intl'", cp437));
        CHARSET_TABLE.add(new CharsetData(84, 850, "intl850", "Paradox 'intl' 850", cp850));
        CHARSET_TABLE.add(new CharsetData(208, 861, "iceland", "Paradox ISL 861", cp861));
        CHARSET_TABLE.add(new CharsetData(130, 865, "nordan", "Paradox 'nordan'", cp865));
        CHARSET_TABLE.add(new CharsetData(230, 865, "nordan40", "Paradox 'nordan40'", cp865));
        CHARSET_TABLE.add(new CharsetData(143, 852, "polish", "Paradox Polish 852", cp852));
        CHARSET_TABLE.add(new CharsetData(252, 852, "slovene", "Paradox Slovene 852", cp852));
        CHARSET_TABLE.add(new CharsetData(240, 437, "swedfin", "Paradox 'swedfin'", cp437));
        CHARSET_TABLE.add(new CharsetData(166, 437, "thai", "Paradox Thai 437", cp437));
        CHARSET_TABLE.add(new CharsetData(198, 857, "turk", "Paradox 'turk'", cp857));
        CHARSET_TABLE.add(new CharsetData(143, 1251, "ancyrr", "Pdox ANSI Cyrillic", windows1251));
        CHARSET_TABLE.add(new CharsetData(220, 1250, "anczech", "Pdox ANSI Czech", windows1250));
        CHARSET_TABLE.add(new CharsetData(14, 1253, "angreek1", "Pdox ANSI Greek", windows1253));
        CHARSET_TABLE.add(new CharsetData(225, 1250, "anhundc", "Pdox ANSI Hun. DC", windows1250));
        CHARSET_TABLE.add(new CharsetData(98, 1252, "ANSIINTL", "Pdox ANSI Intl", windows1252));
        CHARSET_TABLE.add(new CharsetData(17, 1252, "ANSII850", "Pdox ANSI Intl850", windows1252));
        CHARSET_TABLE.add(new CharsetData(94, 1250, "anpolish", "Pdox ANSI Polish", windows1250));
        CHARSET_TABLE.add(new CharsetData(111, 1250, "ansislov", "Pdox ANSI Slovene", windows1250));
        CHARSET_TABLE.add(new CharsetData(93, 1252, "ANSISPAN", "Pdox ANSI Spanish", windows1252));
        CHARSET_TABLE.add(new CharsetData(105, 1252, "ANSISWFN", "Pdox ANSI Swedfin", windows1252));
        CHARSET_TABLE.add(new CharsetData(117, 1252, "anthai", "Pdox ANSI Thai", windows1252));
        CHARSET_TABLE.add(new CharsetData(213, 1254, "ANTURK", "Pdox ANSI Turkish", windows1254));
    }

    /**
     * Creates a new instance.
     */
    private CharsetUtil() {
        super();
    }

    /**
     * Gets the default charset.
     *
     * @param connectionInfo the connection info.
     * @return the default charset.
     */
    public static Charset getDefault(ConnectionInfo connectionInfo) {
        return Optional.ofNullable(connectionInfo.getCharset()).orElse(StandardCharsets.US_ASCII);
    }

    /**
     * Gets the {@link Charset} based on code page, sort order ID and connection info.
     *
     * @param codePage       the code page.
     * @param sortOrderId    the sort order ID.
     * @param connectionInfo the connection info.
     * @return the charset.
     */
    public static Charset get(int codePage, String sortOrderId, final ConnectionInfo connectionInfo) {
        CharsetData data = get(codePage, sortOrderId);
        if (data == null) {
            connectionInfo.addWarning(String.format("Charset not found for width sort order %s and code page %d", sortOrderId, codePage));
            return getDefault(connectionInfo);
        }

        return data.getCharset();
    }

    /**
     * Gets the charset data based on code page and sort order ID.
     *
     * @param codePage    the code page.
     * @param sortOrderId the sort order ID.
     * @return the charset data.
     */
    public static CharsetData get(int codePage, String sortOrderId) {
        if (sortOrderId != null && !sortOrderId.isEmpty()) {
            return CHARSET_TABLE.stream()
                    .filter(x -> sortOrderId.equalsIgnoreCase(x.getSortOrderId()))
                    .findFirst().orElse(null);
        }

        return CHARSET_TABLE.stream()
                .filter(x -> x.getCodePage() == codePage)
                .findFirst().orElse(null);
    }

    /**
     * Translate a string using the data original charset.
     *
     * @param data   the data with the charset to use.
     * @param buffer the buffer to translate.
     * @return the string translated.
     */
    public static String translate(final ParadoxDataFile data, final ByteBuffer buffer) {
        if (data.getCharset() != null) {
            return data.getCharset().decode(buffer).toString();
        }

        return StandardCharsets.US_ASCII.decode(buffer).toString();
    }

    /**
     * Gets the original encoding name (in Paradox).
     *
     * @param data the data with charset to use.
     * @return the original encoding name.
     */
    public static String getOriginalName(ParadoxDataFile data) {
        CharsetData charsetData = get(data.getCodePage(), data.getSortOrderID());
        if (charsetData == null) {
            return "Unknown";
        }

        return charsetData.getName();
    }
}
