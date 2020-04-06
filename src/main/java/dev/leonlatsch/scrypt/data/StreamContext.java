package dev.leonlatsch.scrypt.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Leon Latsch
 * @since 1.0
 */
public class StreamContext {

	private final InputStream in;
	private final OutputStream out;
	private final long size;
	
	public StreamContext(InputStream in, OutputStream out, long sizeToCopy) {
		this.in = in;
		this.out = out;
		this.size = sizeToCopy;
	}
	
	public InputStream getIn() {
		return in;
	}
	
	public OutputStream getOut() {
		return out;
	}
	
	public long getSize() {
		return size;
	}
	
	public void close() {
		try {
			in.close();
			out.close();
		} catch(IOException e) {
			System.err.println("Error closing file connections");
		}
	}
}
