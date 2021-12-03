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
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.png.PngDirectory;

public class Metadonnee {
	private Metadata metadata;
	private ArrayList<Tag> metadataArray;
	
	public Metadonnee(String path) throws ImageProcessingException, IOException {
		File f = new File(path);
		this.metadata = ImageMetadataReader.readMetadata(f);
		this.metadataArray = new ArrayList<Tag>();
	}
	
	public void extractMetadonnee() { 
		
		FileTypeDirectory fileTypeDir = metadata.getFirstDirectoryOfType(FileTypeDirectory.class); //utile?
		String typeFileTag = fileTypeDir.getDescription(FileTypeDirectory.TAG_DETECTED_FILE_TYPE_NAME);
		
		if (typeFileTag.equals("JPEG") || typeFileTag.equals("JPG")) {
			ExifIFD0Directory exifDir1 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			ExifSubIFDDirectory exifDir2 = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
			for (Tag tag : exifDir1.getTags()) {
				metadataArray.add(tag);
			}
			for (Tag tag : exifDir2.getTags()) {
				metadataArray.add(tag);
			}
			if (gpsDir != null) {
				for (Tag tag : gpsDir.getTags()) {
					metadataArray.add(tag);
				}
			}
		}
		if (typeFileTag.equals("PNG")) {
			PngDirectory pngDir = metadata.getFirstDirectoryOfType(PngDirectory.class);
			for (Tag tag : pngDir.getTags()) {
				metadataArray.add(tag);
			}
		}
	}
	
	public String toString() {
		String result = "Les metadonnees sont:\n";
		for (int i=0 ; i<metadataArray.size(); i++) {
			result += metadataArray.get(i).getTagName().toString() + " " + metadataArray.get(i).getDescription().toString() + "\n";
		}
		return result;
	}
}
