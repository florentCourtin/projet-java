package metadata;

import java.io.File;

public class IsNotFileException extends Exception {
	private static final long serialVersionUID = 1L;

	public IsNotFileException(File f) {
		super(f.getName() + " n'est pas un fichier.\nPour extraire l'ensemble des metadonnees d'un repertoire, utilisez l'option -d.");
	}
}
