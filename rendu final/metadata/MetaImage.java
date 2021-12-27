package metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.jfif.JfifDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.png.PngDirectory;

/**
 * Cette classe permet d'extraire les metadonnees d'une image qui est
 * au format png ou jpg/jpeg.
 * Les metadonnees sont les informations complementaires stockees dans
 * le fichier image mais qui ne font pas partie de l'ensemble des pixels.
 * @author sofiane
 * @version JDK1.11
 */
public class MetaImage {
	private File f;
	private Metadata metadata;
	private ArrayList<Tag> metadataArray;
	
	/**
	 * Construit une nouvelle instance MetaImage.
	 * @param path Le chemin d'acces au fichier.
	 * @throws ImageProcessingException Si le format du fichier n'est pas accepte.
	 * @throws IOException Si le fichier n'est pas accessible.
	 */
	public MetaImage(String path) throws ImageProcessingException, IOException {
		this.f = new File(path);
		this.metadata = ImageMetadataReader.readMetadata(f);
		this.metadataArray = new ArrayList<Tag>();
	}
	
	/**
	 * Extrait les metadonnees du fichier image uniquement si ce dernier est au format png ou jpg/jpeg.
	 */
	public void extractMetaImage() { 
		String mime = imageFormat();
		if (mime.equals("jpeg")) {
			ExifIFD0Directory exifDir1 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			ExifSubIFDDirectory exifDir2 = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			JpegDirectory jpegDir = metadata.getFirstDirectoryOfType(JpegDirectory.class);
			JfifDirectory jfifDir = metadata.getFirstDirectoryOfType(JfifDirectory.class);
			GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
			
			if (exifDir1 != null) {
				for (Tag tag : exifDir1.getTags()) {
					metadataArray.add(tag);
				}
			}
			if (exifDir2 != null) {
				for (Tag tag : exifDir2.getTags()) {
					metadataArray.add(tag);
				}
			}
			if (jpegDir != null) {
				for (Tag tag : jpegDir.getTags()) {
					metadataArray.add(tag);
				}
			}
			if (jfifDir != null) {
				for (Tag tag : jfifDir.getTags()) {
					metadataArray.add(tag);
				}
			}
			if (gpsDir != null) {
				for (Tag tag : gpsDir.getTags()) {
					metadataArray.add(tag);
				}
			}
		}
		if (mime.equals("png")) {
			PngDirectory pngDir = metadata.getFirstDirectoryOfType(PngDirectory.class);
			FileSystemDirectory fileSysDir = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
			
			for (Tag tag : pngDir.getTags()) {
				metadataArray.add(tag);
			}
			
			for (Tag tag : fileSysDir.getTags()) {
				metadataArray.add(tag);
			}
		}
	}
	
	/**
	 * Determine le type MIME du fichier image. Cela permet de verifier que le fichier est au format
	 * png ou jpg/jpeg. Si ce n'est pas le cas une chaine de caracteres vide est renvoyee.
	 * @return Le format du fichier detecte.
	 */
	public String imageFormat() {
		String format = "";
		String mime = metadata.getFirstDirectoryOfType(FileTypeDirectory.class).getDescription(FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE);
		if (mime.equals("image/jpeg")) {
			format = "jpeg";
		}
		if (mime.equals("image/png")) {
			format = "png";
		}
		return format;
	}
	
	/**
	 * Retourne un fichier image de type File.
	 * @return Le fichier.
	 */
	public File getF() {
		return f;
	}

	@Override
	public String toString() {
		String result = "Les metadonnees de " + f.getName() + ":\n";
		for (int i=0 ; i<metadataArray.size(); i++) {
			result += metadataArray.get(i).getTagName() + " - " + metadataArray.get(i).getDescription() + "\n";
		}
		return result;
	}
}