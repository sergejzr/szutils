package de.l3s.util.stream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class StreamUtils {

	public static String streamToString(InputStream is) {

	BufferedInputStream bis = new BufferedInputStream(is);

	bis.mark(100000);

	BufferedReader br;
	try {
		br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

		String line = null;

		StringBuffer sb = new StringBuffer();
		do {
			try {
				line = br.readLine();
				if (line != null) {
					sb.append(line);
					sb.append('\n');
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} while (line != null);
		try {
			bis.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return sb.toString();
	}catch(UnsupportedEncodingException e){e.printStackTrace();}

	return "";
}

}
