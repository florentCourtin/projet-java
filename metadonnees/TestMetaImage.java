package metadata;

import java.io.IOException;

import com.drew.imaging.ImageProcessingException;

public class TestMetaImage {

	public static void main(String[] args) {
		
		String path = "C:/users/florent/desktop/image.png";
		try {
			MetaImage mi = new MetaImage(path);
			mi.extractMetaImage();
			System.out.println(mi);
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
