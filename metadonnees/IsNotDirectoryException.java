package metadata;

import java.io.File;

public class IsNotDirectoryException extends Exception {
	private static final long serialVersionUID = 1L;

	public IsNotDirectoryException(File f) { 
		super(f.getName() + " n'est pas un repertoire.\nPour plus de precision, utilisez l'option -h.");
	}
}
