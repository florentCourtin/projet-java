package metadata;

import java.io.IOException;

import com.drew.imaging.ImageProcessingException;

public class TestMetadonnee {

	public static void main(String[] args) {
		
		String pathPng = "C:/users/florent/desktop/metadonnees/image.png";
    String pathJpeg = "C:/users/florent/desktop/metadonnees/jpegFile.jpeg";
		try {
			Metadonnee metaPng = new Metadonnee(pathPng);
			metaPng.extractMetadonnee();
			System.out.println(metaPng);
      
      Metadonnee metaJpeg = new Metadonnee(pathJpeg);
      metaJpeg.extractMetadonnees();
      System.out.println(metaJpeg);
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
