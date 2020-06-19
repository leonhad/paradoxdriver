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
package com.googlecode.paradox.data;

import com.googlecode.paradox.metadata.ParadoxDataFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

public final class ParadoxBuffer {

    private ByteBuffer buffer;
    private final boolean encrypted;
    private final long encryptedData;
    private final int blockSize;

    public ParadoxBuffer(final ParadoxDataFile dataFile, final int bufferSize, final int blockSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.encrypted = dataFile.isEncrypted();
        this.encryptedData = dataFile.getEncryptedData();
        this.blockSize = blockSize;
    }

    public ParadoxBuffer(final int bufferSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.encrypted = false;
        this.encryptedData = 0;
        this.blockSize = 0;
    }

    public ParadoxBuffer(final byte[] bytes) {
        this.buffer = ByteBuffer.wrap(bytes);
        this.encrypted = false;
        this.encryptedData = 0;
        this.blockSize = 0;
    }

    public void reallocate(final int bufferSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
    }

    public void read(final ReadableByteChannel channel) throws IOException {
        channel.read(buffer);
    }

    public void read(final ReadableByteChannel channel, long blockNumber) throws IOException {
        channel.read(buffer);

        if (encrypted) {
            byte[] b = buffer.array();
            EncryptedData.decryptDBBlock(b, encryptedData, blockSize, blockNumber);
        }
    }

    public void order(final ByteOrder byteOrder) {
        buffer.order(byteOrder);
    }

    public void clear() {
        buffer.clear();
    }

    public void flip() {
        buffer.flip();
    }

    public void position(int pos) {
        buffer.position(pos);
    }

    public byte get() {
        return buffer.get();
    }

    public int getInt() {
        return buffer.getInt();
    }

    public short getShort() {
        return buffer.getShort();
    }

    public long getLong() {
        return buffer.getLong();
    }
}
