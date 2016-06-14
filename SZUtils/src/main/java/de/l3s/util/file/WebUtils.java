package de.l3s.util.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class WebUtils {

	private static final Logger logger = LogManager.getLogger(WebUtils.class);
	
	public static void copy(File outDir, URL url) throws IOException {
		/*
		if (outDir == null || !outDir.isDirectory()) {
			throw new IOException("is not a directory: " + outDir);
		}*/
		
		String filename=url.getFile();
		
		if(filename.contains("/"))
			filename=url.getFile().substring(url.getFile().lastIndexOf("/")+1);

		// System.out.println("Opening connection to " + url + "...");
		// URLConnection urlC = url.openConnection();
		// Copy resource to local file, use remote file
		// if no local file name specified
		InputStream is = url.openStream();
		// Print info about resource
		/*
		 * System.out .print("Copying resource (type: " +
		 * urlC.getContentType()); Date date = new Date(urlC.getLastModified());
		 * System.out.println(", modified on: " + date.toLocaleString() +
		 * ")...");
		 */

		FileOutputStream fos = null;
if(outDir.isDirectory()){
	File resfile = new File(outDir.getAbsolutePath(),filename);
	if(resfile.exists()) return;
		fos = new FileOutputStream(resfile);
}else
{
	fos = new FileOutputStream(new File(outDir.getAbsolutePath()));
	}

		byte[] buffer = new byte[4096];
		int bytesRead;

		while ((bytesRead = is.read(buffer)) != -1)
			fos.write(buffer, 0, bytesRead); // write

		is.close();
		fos.close();

		logger.info(url + " copied");
	}

	public static void copy(File outDir, String extention,URL... urls) throws IOException {
		if (outDir == null || !outDir.isDirectory()) {
			throw new IOException("is not a directory: " + outDir);
		}
		for (URL url : urls) {
			

			// System.out.println("Opening connection to " + url + "...");
			// URLConnection urlC = url.openConnection();
			// Copy resource to local file, use remote file
			// if no local file name specified
			InputStream is = url.openStream();
			// Print info about resource
			/*
			 * System.out .print("Copying resource (type: " +
			 * urlC.getContentType()); Date date = new Date(urlC.getLastModified());
			 * System.out.println(", modified on: " + date.toLocaleString() +
			 * ")...");
			 */

			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			//FileOutputStream fos = null;

		//	fos = new FileOutputStream(new File(outDir.getAbsolutePath());

			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = is.read(buffer)) != -1)
				baos.write(buffer, 0, bytesRead); // write

			is.close();
			baos.close();
			
			String content=baos.toString("UTF-8");
			FileWriter fos = null;
			fos = new FileWriter(new File(outDir.getAbsolutePath(),Math.abs(content.hashCode())+"."+extention));
			fos.write(content);
			fos.flush();
			fos.close();
			logger.info(url + " copied");
		}

	}

}
