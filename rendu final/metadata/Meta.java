package metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tika.Tika;

import com.drew.imaging.ImageProcessingException;
/**
 * Cette classe permet d'extraire les metadonnees des images contenues
 * dans un repertoire qui sont au format png ou jpg/jpeg.
 * @author sofiane
 * @version JDK1.11
 */
public class Meta {
	private String typeFile;
	private File f;
	private String path;
	private ArrayList<MetaImage> metaImageArray;
	
	/**
	 * Construit une nouvelle instance Meta.
	 * @param typeFile Le type du fichier a exploiter. -d pour un repertoire, -f pour un fichier.
	 * @param path Le chemin d'acces au fichier.
	 * @throws IsNotDirectoryException Si le typeFile est -d mais que le fichier n'est pas un repertoire.
	 * @throws IsNotFileException Si le typeFile est -f mais que le fichier n'est pas un fichier image.
	 */
	public Meta(String typeFile, String path) throws IsNotDirectoryException, IsNotFileException {
		this.typeFile = typeFile;
		this.path = path;
		this.f = new File(path);
		this.metaImageArray = new ArrayList<MetaImage>();
		if (typeFile == "-d" && !f.isDirectory()) {
			throw new IsNotDirectoryException(f);
		} else if (typeFile == "-f" && !f.isFile()) {
			throw new IsNotFileException(f);
		}
	}
	
	/**
	 * Remplit recursivement un ArrayList de type MetaImage uniquement si les fichiers images sont de type png ou jpg/jpeg
	 * et extrait ses metadonnees.
	 * @param files Le tableau de type File que l'on veut traiter
	 * @throws ImageProcessingException Si le format du fichier n'est pas accepte.
	 * @throws IOException Si le fichier n'est pas accesible.
	 */
	public void remplissage(File[] files) throws ImageProcessingException, IOException {
		for (int i=0; i<files.length; i++) {
			if (files[i].isDirectory()) {
				remplissage(files[i].listFiles());
			} else {
				String mimeType = new Tika().detect(files[i].getPath());
				if ((mimeType.equals("image/jpeg")) || (mimeType.equals("image/png"))) {
					MetaImage mi = new MetaImage(files[i].getPath());
					metaImageArray.add(mi);
					mi.extractMetaImage();
				}
			}
		}
	}
	
	/**
	 * Fait appel a la methode remplissage pour l'extraction des metadonnees si c'est un repertoire. Extrait directement
	 * les metadonnees si c'est un fichier image.
	 * @throws ImageProcessingException Si le format du fichier n'est pas accepte.
	 * @throws IOException Si le fichier n'est pas accessible.
	 */
	public void extractMeta() throws ImageProcessingException, IOException {
		if (typeFile == "-f") {
			MetaImage m = new MetaImage(path);
			m.extractMetaImage();
			metaImageArray.add(m);
		}
		if (typeFile == "-d") {
			File[] files = f.listFiles();
			remplissage(files);
		}
	}

	/**
	 * Retour l'ArrayList contenant les objets de type MetaImage.
	 * @return L'arrayList des MetaImage.
	 */
	public ArrayList<MetaImage> getMetaImageArray() {
		return metaImageArray;
	}

	@Override
	public String toString() {
		String result = "Le repertoire " + f.getName() + " contient:\n\n";
		for (int i=0; i<metaImageArray.size(); i++) {
			result += metaImageArray.get(i).toString() + "\n";
		}
		return result;
	}
}