package de.l3s.util.text;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;


public class TextUtils {

	public static String join(String[] in, String separator) {
		StringBuffer sb = new StringBuffer();
		for (String s : in) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append(s);
		}

		return sb.toString();
	}
	
	public static String RTF2Text(File fileName)
	{
	try{
	//Reading the RTF file as data input stream
	FileInputStream fin = new FileInputStream(fileName);
	DataInputStream din = new DataInputStream(fin);

	//creating a default blank styled document
	DefaultStyledDocument styledDoc = new DefaultStyledDocument();

	//Creating a RTF Editor kit
	RTFEditorKit rtfKit = new RTFEditorKit();

	//Populating the contents in the blank styled document
	rtfKit.read(din,styledDoc,0);

	// Getting the root document
	Document doc = styledDoc.getDefaultRootElement().getDocument();

	//Printing out the contents of the RTF document as plain text
	return (doc.getText(0,doc.getLength()));

	}catch(Exception ex){
	ex.printStackTrace();
	}
	return null;
	}
}
