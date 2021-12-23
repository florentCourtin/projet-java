package metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tika.Tika;

import com.drew.imaging.ImageProcessingException;

public class Meta {
	private String typeFile;
	private File f;
	private String path;
	private ArrayList<MetaImage> metaImageArray;
	
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
	
	public String getTypeFile() {
		return typeFile;
	}

	public ArrayList<MetaImage> getMetaImageArray() {
		return metaImageArray;
	}

	public String toString() {
		String result = "Le repertoire " + f.getName() + " contient:\n\n";
		for (int i=0; i<metaImageArray.size(); i++) {
			result += metaImageArray.get(i).toString() + "\n";
		}
		return result;
	}
	
	public static void main(String[] args) {
		Meta m;
		
		if (args[0].equals("-d")) {
			try {
				m = new Meta("-d", args[1]);
				m.extractMeta();
				System.out.println(m);
			} catch (IsNotDirectoryException e) {
				e.printStackTrace();
			} catch (IsNotFileException e) {
				e.printStackTrace();
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("-f")) {
			try {
				m = new Meta("-f", args[1]);
				m.extractMeta();
				System.out.println(m);
			} catch (IsNotDirectoryException e) {
				e.printStackTrace();
			} catch (IsNotFileException e) {
				e.printStackTrace();
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		} else if (args[0].equals("-h") || (args[0].isEmpty())) {
			System.out.println("Vous avez ouvert l'option d'aide -h pour la classe Meta");
			System.out.println("Cette classe permet d'extraire les metadonnees d'un fichier image, ou d'un repertoire");
			System.out.println("Voici les arguments possibles: ");
			System.out.println("    -d, chemin        -d pour preciser que c'est un repertoire, chemin pour retrouver le repertoire en question");
			System.out.println("    -f, chemin        -f pour preciser que c'est un fichier, chemin pour retrouver le fichier en question");
		} else {
			System.out.println("Erreur: pour afficher l'aide, utilisez l'option -h.");
		}
	}
}
