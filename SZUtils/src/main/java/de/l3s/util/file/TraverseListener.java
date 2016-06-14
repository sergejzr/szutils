package de.l3s.util.file;

import java.io.File;

public interface TraverseListener {

	public void directoryFound(File examine);

	public void fileFound(File examine);

}
