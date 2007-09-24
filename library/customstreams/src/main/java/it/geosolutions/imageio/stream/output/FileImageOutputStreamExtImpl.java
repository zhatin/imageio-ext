/*
 *    JImageIO-extension - OpenSource Java Image translation Library
 *    http://www.geo-solutions.it/
 *	  https://imageio-ext.dev.java.net/
 *    (C) 2007, GeoSolutions
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package it.geosolutions.imageio.stream.output;

import it.geosolutions.imageio.stream.eraf.EnhancedRandomAccessFile;
import it.geosolutions.imageio.stream.input.FileImageInputStreamExtImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.imageio.stream.ImageOutputStreamImpl;

/**
 * 
 * @author Daniele Romagnoli
 * @author Simone Giannecchini
 */

public class FileImageOutputStreamExtImpl extends ImageOutputStreamImpl
		implements FileImageOutputStreamExt {

	protected EnhancedRandomAccessFile eraf;

	protected File file;

	/**
	 * A constructor which accepts a File as input.
	 * 
	 * @param file
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public FileImageOutputStreamExtImpl(File file)
			throws FileNotFoundException, IOException {
		this.file = file;
		eraf = new EnhancedRandomAccessFile(file, "rw");
		// NOTE: this must be done accordingly to what ImageInputStreamImpl
		// does, otherwise some ImageREader subclasses might not work.
		this.eraf.setByteOrder(ByteOrder.BIG_ENDIAN);

	}

	public int read() throws IOException {
		checkClosed();
		bitOffset = 0;
		int val = eraf.read();
		if (val != -1) {
			++streamPos;
		}
		return val;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		checkClosed();
		bitOffset = 0;
		int nbytes = eraf.read(b, off, len);
		if (nbytes != -1) {
			streamPos += nbytes;
		}
		return nbytes;
	}

	public void write(int b) throws IOException {
		checkClosed();
		flushBits();
		eraf.write(b);
		++streamPos;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		checkClosed();
		flushBits();
		eraf.write(b, off, len);
		streamPos += len;
	}

	public long length() {
		try {
			checkClosed();
			return eraf.length();
		} catch (IOException e) {
			return -1L;
		}
	}

	/**
	 * Sets the current stream position and resets the bit offset to 0. It is
	 * legal to seeking past the end of the file; an <code>EOFException</code>
	 * will be thrown only if a read is performed. The file length will not be
	 * increased until a write is performed.
	 * 
	 * @exception IndexOutOfBoundsException
	 *                if <code>pos</code> is smaller than the flushed
	 *                position.
	 * @exception IOException
	 *                if any other I/O error occurs.
	 */
	public void seek(long pos) throws IOException {
		checkClosed();
		if (pos < flushedPos) {
			throw new IndexOutOfBoundsException("pos < flushedPos!");
		}
		bitOffset = 0;
		eraf.seek(pos);
		streamPos = eraf.getFilePointer();
	}

	/**
	 * Closes the underlying {@link EnhancedRandomAccessFile}.
	 * 
	 * @throws IOException
	 *             in case something bad happens.
	 */
	public void close() throws IOException {
		super.close();
		eraf.close();
	}

	/**
	 * Retrieves the {@link File} we are connected to.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Disposes this {@link FileImageInputStreamExtImpl} by closing its
	 * underlying {@link EnhancedRandomAccessFile}.
	 * 
	 */
	public void dispose() {
		try {
			close();
		} catch (IOException e) {

		}

	}
}