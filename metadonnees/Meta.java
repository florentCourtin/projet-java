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
	private ArrayList<MetaImage> imageMetaArray;
	
	public Meta(String typeFile, String path) throws IsNotDirectoryException, IsNotFileException {
		this.typeFile = typeFile;
		this.path = path;
		this.f = new File(path);
		this.imageMetaArray = new ArrayList<MetaImage>();
		if (typeFile == "-d" && !f.isDirectory()) {
			throw new IsNotDirectoryException(f);
		} else if (typeFile == "-f" && !f.isFile()) {
			throw new IsNotFileException(f);
			//+else if pour option -h ?
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
					imageMetaArray.add(mi);
					mi.extractMetaImage();
				}
			}
		}
	}
	
	public void extractMeta() throws ImageProcessingException, IOException {
		if (typeFile == "-f") {
			MetaImage m = new MetaImage(path);
			m.extractMetaImage();
			imageMetaArray.add(m);
		}
		if (typeFile == "-d") {
			File[] files = f.listFiles();
			remplissage(files);
		}
	}
	
	public String toString() {
		String result = "Le repertoire contient:\n\n";
		for (int i=0; i<imageMetaArray.size(); i++) {
			result += imageMetaArray.get(i).toString() + "\n";
		}
		return result;
	}
}
