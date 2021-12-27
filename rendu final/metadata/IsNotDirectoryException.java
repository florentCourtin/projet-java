package metadata;

import java.io.File;

/**
 * Indique que le fichier passe en parametre n'est pas un repertoire.
 * @author florent
 * @version JDK1.11
 */
public class IsNotDirectoryException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Construit l'exception en indiquant que ce n'est pas le format attendu.
	 * @param f Le fichier qui n'est pas au format attendu.
	 */
	public IsNotDirectoryException(File f) { 
		super(f.getName() + " n'est pas un repertoire.\nPour plus de precision, utilisez l'option -h.");
	}
}