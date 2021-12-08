package metadata;

import java.io.IOException;

import com.drew.imaging.ImageProcessingException;

public class TestMeta {

	public static void main(String[] args) {
		
		try {
			Meta m = new Meta("-d", "C:/users/florent/desktop/rep");
			try {
				m.extractMeta();
				System.out.println(m);
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IsNotDirectoryException e) {
			e.printStackTrace();
		} catch (IsNotFileException e) {
			e.printStackTrace();
		}
	}
}
