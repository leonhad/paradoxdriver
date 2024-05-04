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

public class Cp867Charset extends Charset {

    private static final char[] CP_867 = {
            '\u0000', '☺', '☻', '♥', '♦', '♣', '♠', '•', '◘', '○', '◙', '♂', '♀', '♪', '♫', '☼', // 0x00
            '►', '◄', '↕', '‼', '¶', '§', '▬', '↨', '↑', '↓', '→', '←', '∟', '↔', '▲', '▼', // 0x10
            ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', // 0x20
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', // 0x30
            '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', // 0x40
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', // 0x50
            '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', // 0x60
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '⌂', // 0x70
            'א', 'ב', 'ג', 'ד', 'ה', 'ו', 'ז', 'ח', 'ט', 'י', 'ך', 'כ', 'ל', 'ם', 'מ', 'ן', // 0x80
            'נ', 'ס', 'ע', 'ף', 'פ', 'ץ', 'צ', 'ק', 'ר', 'ש', 'ת', '¢', '£', '¥', ' ', '₪', // 0x90
            '\u200E', '\u200F', '\u202A', '\u202B', '\u202D', '\u202E', '\u202C', ' ', ' ', '⌐', '¬', '½', '¼', '€', '«', '»', // 0xA0
            '░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐', // 0xB0
            '└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧', // 0xC0
            '╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀', // 0xD0
            'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 'μ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞', 'φ', 'ε', '∩', // 0xE0
            '≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■', '\u00A0' // 0xF0
    };

    private static final Map<Character, Byte> LOOKUP;

    static {
        Map<Character, Byte> map = new HashMap<>();
        for (int i = 0; i < CP_867.length; i++) {
            map.put(CP_867[i], (byte) i);
        }

        // Add special cases (problems with ancient encoding).
        map.put('♬', (byte) 0x0E);
        map.put('¦', (byte) 0x7C);
        map.put('Δ', (byte) 0x7F);

        LOOKUP = Collections.unmodifiableMap(map);
    }

    public Cp867Charset() {
        super("cp867", new String[]{"cp-867", "IBM867", "IBM-867"});
    }

    @Override
    public boolean contains(Charset cs) {
        return cs.name().equals("cp867");
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
                    char d = CP_867[c & 0xFF];
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
