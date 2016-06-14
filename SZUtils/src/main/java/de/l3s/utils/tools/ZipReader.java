package de.l3s.utils.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipReader {
	public static byte[] readFromZEntry(ZipInputStream zin, ZipEntry zentry)
			throws IOException {

		ByteArrayOutputStream fout = new ByteArrayOutputStream();
		for (int c = zin.read(); c != -1; c = zin.read()) {
			fout.write(c);
		}
		zin.closeEntry();

		return fout.toByteArray();
	}
}
