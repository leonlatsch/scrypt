package dev.leonlatsch.scrypt.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Leon Latsch
 * @since 1.0
 */
public class InfoCreator {

	public static void create(File tmpFile) throws IOException {
		InputStream in = InfoCreator.class.getResourceAsStream("/info.html");
		FileOutputStream out = new FileOutputStream(tmpFile);
		
		copy(in, out);
	}
	
	private static void copy(InputStream in, OutputStream out) throws IOException {
    	int i;
    	byte[] b = new byte[1024];
    	
    	while((i=in.read(b))!=-1) {
    		out.write(b, 0, i);
    	}
    }
}
