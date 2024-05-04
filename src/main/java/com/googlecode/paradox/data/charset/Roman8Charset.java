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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Roman8Charset extends Charset {

    // Source: https://datatracker.ietf.org/doc/html/rfc1345
    private static final char[] ROMAN_8 = {
            '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\t', '\n', '\u000B', '\u000C', '\r', '\u000E', '\u000F', // 0x00
            '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001A', '\u001B', '\u001C', '\u001D', '\u001E', '\u001F', // 0x10
            0x20, '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', // 0x20
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', // 0x30
            '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', // 0x40
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', // 0x50
            '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', // 0x60
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '▒', // 0x70
            '\u0080', '\u0081', '\u0082', '\u0083', '\u0084', '\u0085', '\u0086', '\u0087', '\u0088', '\u0089', '\u008A', '\u008B', '\u008C', '\u008D', '\u008E', '\u008F', // 0x80
            '\u0090', '\u0091', '\u0092', '\u0093', '\u0094', '\u0095', '\u0096', '\u0097', '\u0098', '\u0099', '\u009A', '\u009B', '\u009C', '\u009D', '\u009E', '\u009F', //0x90
            '\u00A0', 'À', 'Â', 'È', 'Ê', 'Ë', 'Î', 'Ï', '´', '‵', '∧', '¨', '∼', 'Ù', 'Û', '₤', // 0xA0
            '¯', 'Ý', 'ý', '°', 'Ç', 'ç', 'Ñ', 'ñ', '¡', '¿', '¤', '£', '¥', '§', 'ƒ', '¢', // 0xB0
            'â', 'ê', 'ô', 'û', 'á', 'é', 'ó', 'ú', 'à', 'è', 'ò', 'ù', 'ä', 'ë', 'ö', 'ü', // 0xC0
            'Å', 'î', 'Ø', 'Æ', 'å', 'í', 'ø', 'æ', 'Ä', 'ì', 'Ö', 'Ü', 'É', 'ï', 'ß', 'Ô', // 0xD0
            'Á', 'Ã', 'ã', 'Ð', 'ð', 'Í', 'Ì', 'Ó', 'Ò', 'Õ', 'õ', 'Š', 'š', 'Ú', 'Ÿ', 'ÿ', // 0xE0
            'Þ', 'þ', '·', 'µ', '¶', '¾', '—', '¼', '½', 'ª', 'º', '«', '■', '»', '±', '\u0000'  // 0xF0
    };

    private static final Map<Character, Byte> LOOKUP;

    static {
        Map<Character, Byte> map = new HashMap<>();
        for (int i = 0; i < ROMAN_8.length; i++) {
            map.put(ROMAN_8[i], (byte) i);
        }

        // Add special cases (problems with encoding definition by IBM and HP).
        map.put('\u007F', (byte) 0x7F);
        map.put('｀', (byte) 0xA9);
        map.put('‾', (byte) 0xB0);
        map.put('˚', (byte) 0xB3);
        map.put('β', (byte) 0xDE);
        map.put('đ', (byte) 0xE4);
        map.put('μ', (byte) 0xF3);
        map.put('\u00AD', (byte) 0xF6);

        LOOKUP = Collections.unmodifiableMap(map);
    }

    public Roman8Charset() {
        super("roman8", new String[]{"IBM1051", "cp1051", "cp-1051", "IBM-1051", "hp-roman8", "HP_ROMAN8", "ROMAN8", "cp1050", "cp-1050", "IBM1050", "IBM-1050"});
    }

    @Override
    public boolean contains(Charset cs) {
        return cs.name().equals("roman8");
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new CharsetDecoder(this, 1.0F, 1.0F) {
            @Override
            protected CoderResult decodeLoop(ByteBuffer from, CharBuffer to) {
                while (from.hasRemaining()) {
                    if (!to.hasRemaining()) {
                        return CoderResult.OVERFLOW;
                    }

                    byte c = from.get();
                    char d = ROMAN_8[c & 0xFF];
                    to.put(d);
                }

                return CoderResult.UNDERFLOW;
            }
        };
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new CharsetEncoder(this, 1F, 1F) {
            @Override
            protected CoderResult encodeLoop(final CharBuffer from, final ByteBuffer to) {
                while (from.hasRemaining()) {
                    if (!to.hasRemaining()) return CoderResult.OVERFLOW;

                    final Byte v = LOOKUP.get(from.get());
                    if (v == null) {
                        // unconsume the character we consumed
                        from.position(from.position() - 1);
                        return CoderResult.unmappableForLength(1);
                    }

                    to.put(v);
                }

                return CoderResult.UNDERFLOW;
            }
        };
    }
}
