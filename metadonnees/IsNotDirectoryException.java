package metadata;

import java.io.File;

public class IsNotDirectoryException extends Exception {
	private static final long serialVersionUID = 1L;

	public IsNotDirectoryException(File f) { 
		super(f.getName() + " n'est pas un repertoire.\nPour extraire les metadonnees d'un fichier, utilisez l'option -f.");
	}
}
