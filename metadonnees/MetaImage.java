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
import com.drew.metadata.jfif.JfifDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.png.PngDirectory;

public class MetaImage {
	private String name;
	private Metadata metadata;
	private ArrayList<Tag> metadataArray;
	
	public MetaImage(String path) throws ImageProcessingException, IOException {
		File f = new File(path);
		this.name = f.getName();
		this.metadata = ImageMetadataReader.readMetadata(f);
		this.metadataArray = new ArrayList<Tag>();
		
	}
	
	public void extractMetaImage() { 
		String mime = metadata.getFirstDirectoryOfType(FileTypeDirectory.class).getDescription(FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE);
		if (mime == "image/jpeg") {
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
		if (mime == "image/png") {
			PngDirectory pngDir = metadata.getFirstDirectoryOfType(PngDirectory.class);
			for (Tag tag : pngDir.getTags()) {
				metadataArray.add(tag);
			}
		}
	}

	public String toString() {
		String result = "Les metadonnees de " + name + ":\n";
		for (int i=0 ; i<metadataArray.size(); i++) {
			result += metadataArray.get(i).getTagName() + " - " + metadataArray.get(i).getDescription() + "\n";
		}
		return result;
	}
}
