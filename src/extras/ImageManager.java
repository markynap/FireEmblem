package extras;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class ImageManager{
	
	public Image getImage(String path) {
		if (path == null) return null;
		Image tempImage = null;
		try {
			URL imageURL = ImageManager.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch(Exception e) {
			System.out.println("could not find image from path: " + path);
		}
		return tempImage;
	}
	
}
