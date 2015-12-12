package org.cakelab.blender.io.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class BigEndianInputStreamWrapper extends CDataReadWriteAccess {

	private DataInputStream in;

	public BigEndianInputStreamWrapper(InputStream in, int pointerSize) {
		super(pointerSize);
		this.in = new DataInputStream(in);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public byte readByte() throws IOException {
		return in.readByte();
	}

	@Override
	public void writeByte(int value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public short readShort() throws IOException {
		return in.readShort();
	}

	@Override
	public void writeShort(short value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int readInt() throws IOException {
		return in.readInt();
	}

	@Override
	public void writeInt(int value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long readInt64() throws IOException {
		return in.readLong();
	}

	@Override
	public void writeInt64(long value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public float readFloat() throws IOException {
		return in.readFloat();
	}

	@Override
	public void writeFloat(float value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public double readDouble() throws IOException {
		return in.readDouble();
	}

	@Override
	public void writeDouble(double value) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void padding(int alignment, boolean extend) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void padding(int alignment) throws IOException {
		long pos = offset();
		long misalignment = pos%alignment;
		if (misalignment > 0) {
			long len = available();
			long correction = alignment-misalignment;
			if (pos + correction <= len) {
				skip(correction);
			} else {
				throw new IOException("padding beyond file boundary without permission.");
			}
		}
	}

	@Override
	public long skip(long n) throws IOException {
		offset(offset()+n);
		return n;
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void offset(long offset) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long offset() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteOrder getByteOrder() {
		return ByteOrder.BIG_ENDIAN;
	}

}
