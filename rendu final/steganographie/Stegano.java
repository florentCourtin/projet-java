package steganographie;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageProcessingException;

import metadata.MetaImage;

/**
 * Cette classe permet de cacher un message personnalise dans les pixels d'une image, ou d'extraire
 * un message deja cache.
 * @author florent
 * @version JDK1.11
 *
 */
public class Stegano {
	private String path;
	private BufferedImage image;
	private BufferedImage newImage;
	private WritableRaster trame;
	private Color[][] pixels;
	private int largeur;
	private int hauteur;
	private String messageDecode;
	
	/**
	 * Construit une nouvelle instance Stegano.
	 * @param path Le chemin d'acces au fichier.
	 * @throws IOException Si le fichier n'est pas accessible.
	 */
	public Stegano(String path) throws IOException {
		this.path = path;
		this.image = ImageIO.read(new File(path));
		this.messageDecode = "";
		this.largeur = image.getWidth();
		this.hauteur = image.getHeight();
		this.newImage = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);
		this.trame = newImage.getRaster();
		this.pixels = new Color[largeur][hauteur];
		for (int i=0; i<largeur; i++) {
			for (int j=0; j<hauteur; j++) {
				pixels[i][j] = new Color(image.getRGB(i, j), true);
			}
		}
	}
	
	/**
	 * Convertit du texte en binaire (chaine formee de 0 et de 1).
	 * @param text Le texte que l'on veut traduire en binaire.
	 * @return La traduction du texte en binaire.
	 */
	public String convertToBinary(String text) {
		String bitsText = new String();
		for (int i=0; i<text.length(); i++) {
			bitsText += String.format("%8s", Integer.toBinaryString((byte)text.charAt(i) & 0xFF)).replace(' ', '0');
		}
		return bitsText;
	}
	
	/**
	 * Convertit du binaire (chaine formee de 0 et de 1) en texte.
	 * @param bitsText Le texte en binaire que l'on veut traduire.
	 * @return La traduction du texte.
	 */
	public String convertToString(String bitsText) {
		String result = "";
		String[] temp  = bitsText.split("(?<=\\G........)");
		for (int i=0; i<temp.length; i++) {
			result += (char)Byte.parseByte(temp[i], 2);
		}
		return result;
	}
	
	/**
	 * Traite l'image pour y cacher un message et ecrase la version initiale ce celle-ci.
	 * @param message Le message que l'on veut cacher dans l'image.
	 * @throws IOException Si le fichier n'est pas accessible.
	 * @throws ImageProcessingException Si le format du fichier n'est pas accepte.
	 */
	public void cacherMessage(String message) throws IOException, ImageProcessingException {
		String bitsMessage = convertToBinary(message);
		int bit = 0;
		for (int i=0; i<largeur; i++) {
			for (int j=0; j<hauteur; j++) {
				if (bit < bitsMessage.length()) {
					
					pixels[i][j] = setLSBRouge(bitsMessage.charAt(bit), pixels[i][j]);
					pixels[i][j] = setLSBVert(bitsMessage.charAt(bit+1), pixels[i][j]);
					pixels[i][j] = setLSBBleu(bitsMessage.charAt(bit+2), pixels[i][j]);
					pixels[i][j] = setLSBAlpha(bitsMessage.charAt(bit+3), pixels[i][j]);
					bit += 4;
					
					int[] tab = {pixels[i][j].getRed(), pixels[i][j].getGreen(), pixels[i][j].getBlue(), pixels[i][j].getAlpha()};
					trame.setPixel(i, j, tab);
				} else {
					int[] tab = {pixels[i][j].getRed(), pixels[i][j].getGreen(), pixels[i][j].getBlue(), pixels[i][j].getAlpha()};
					trame.setPixel(i, j, tab);
				}
			}
		}
		creerTag(message);
		MetaImage mi = new MetaImage(path);
		String format = mi.imageFormat();
		ImageIO.write(newImage, format, new File(path));
	}
	
	/**
	 * Cree et cache un "tag" dans l'image. Ce tag permettra de determiner la longueur du message et de l'extraire.
	 * @param message Le message que l'on veut cacher dans l'image.
	 */
	public void creerTag(String message) {
		final String TAG = convertToBinary(String.valueOf(message.length()*8) + "T");
		int bit = 0;
		for (int i=largeur-1; i>0; i--) {
			for (int j=hauteur-1; j>0; j--) {
				if (bit < TAG.length()) {
					
					pixels[i][j] = setLSBRouge(TAG.charAt(bit), pixels[i][j]);
					pixels[i][j] = setLSBVert(TAG.charAt(bit+1), pixels[i][j]);
					pixels[i][j] = setLSBBleu(TAG.charAt(bit+2), pixels[i][j]);
					pixels[i][j] = setLSBAlpha(TAG.charAt(bit+3), pixels[i][j]);
					bit += 4;
					
					int[] tab = {pixels[i][j].getRed(), pixels[i][j].getGreen(), pixels[i][j].getBlue(), pixels[i][j].getAlpha()};
					trame.setPixel(i, j, tab);
				}
			}
		}
	}
	
	/**
	 * Permet de decoder un message initialement cache dans une image.
	 */
	public void decoder() {
		int longueurMessageBits = getLongueurMessage();
		int bit = 0;
		String messageDecodeBits = "";
		for (int i=0; i<largeur; i++) {
			for (int j=0; j<hauteur; j++) {
				if (bit < longueurMessageBits) {
					messageDecodeBits += getLSBRouge(pixels[i][j]) + getLSBVert(pixels[i][j]) + getLSBBleu(pixels[i][j]) + getLSBAlpha(pixels[i][j]);
					bit += 4;
				}
			}
		}
		messageDecode = convertToString(messageDecodeBits);
	}
	
	/**
	 * Lorsque l'on veut extraire un message, recupere le tag qui permet de determiner la longueur du message que l'on cherche.
	 * @return La longueur du message.
	 */
	public int getLongueurMessage() { 
		String[] tab4 = new String[20];
		int k = 0;
		for (int i=largeur-1; i>=0; i--) {
			for (int j=hauteur-1; j>=0; j--) {
				if (k < 20) {
					String bitRouge = getLSBRouge(pixels[i][j]);
					String bitVert = getLSBVert(pixels[i][j]);
					String bitBleu = getLSBBleu(pixels[i][j]);
					String bitAlpha = getLSBAlpha(pixels[i][j]);
					tab4[k] = bitRouge + bitVert + bitBleu + bitAlpha;
					k++;
				}
			}
		}
		
		String[] tab8 = new String[10];
		tab8[0] = tab4[0] + tab4[1];
		tab8[1] = tab4[2] + tab4[3];
		tab8[2] = tab4[4] + tab4[5];
		tab8[3] = tab4[6] + tab4[7];
		tab8[4] = tab4[8] + tab4[9];
		tab8[5] = tab4[10] + tab4[11];
		tab8[6] = tab4[12] + tab4[13];
		tab8[7] = tab4[14] + tab4[15];
		tab8[8] = tab4[16] + tab4[17];
		tab8[9] = tab4[18] + tab4[19];
		
		int t = decrypteTag(tab8);
		String longueurMessageBits = "";
		for (int i=0; i<t; i++) {
			longueurMessageBits += tab8[i];
		}
		String longueurMessageString = convertToString(longueurMessageBits);
		int longueurMessageInt = Integer.parseInt(longueurMessageString);
		return longueurMessageInt;
	}
	
	/**
	 * Determine la position du tag pour trouver la longueur du message lors de l'extraction de ce dernier.
	 * @param tab Un tableau de type String contenant des informations sur les derniers pixels qui contiennent le tag.
	 * @return La position du tag dans l'image
	 */
	public int decrypteTag(String[] tab) {
		final String bitsTag = "01010100";
		int indiceTag = -1;
		for (int i=0; i<tab.length; i++) {
			if (tab[i].equals(bitsTag)) {
				indiceTag = i;
			}
		}
		return indiceTag;
	}
	
	/**
	 * Retourne la valeur du dernier bit de la composante rouge d'un pixel.
	 * @param pixel Le pixel a analyser.
	 * @return La valeur du dernier bit de la composante rouge du pixel.
	 */
	public String getLSBRouge(Color pixel) {
		String bit;
		if (pixel.getRed()%2==0) {
			bit = "0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	/**
	 * Retourne la valeur du dernier bit de la composante verte d'un pixel.
	 * @param pixel Le pixel a analyser.
	 * @return La valeur du dernier bit de la composante verte du pixel.
	 */
	public String getLSBVert(Color pixel) {
		String bit;
		if (pixel.getGreen()%2==0) {
			bit ="0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	/**
	 * Retourne la valeur du dernier bit de la composante bleue d'un pixel.
	 * @param pixel Le pixel a analyser.
	 * @return La valeur du dernier bit de la composante bleue du pixel.
	 */
	public String getLSBBleu(Color pixel) {
		String bit;
		if (pixel.getBlue()%2==0) {
			bit ="0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	/**
	 * Retourne la valeur du dernier bit de la composante alpha d'un pixel.
	 * @param pixel Le pixel a analyser.
	 * @return La valeur du dernier bit de la composante alpha du pixel.
	 */
	public String getLSBAlpha(Color pixel) {
		String bit;
		if (pixel.getAlpha()%2==0) {
			bit ="0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	/**
	 * Modifie la valeur du dernier bit de la composante rouge d'un pixel, si celui-ci est different du bit passe en parametre.
	 * @param bit Le bit que l'on veut cacher dans la composante rouge du pixel.
	 * @param pixel Le pixel a modifier.
	 * @return Le pixel modifie.
	 */
	public Color setLSBRouge(char bit, Color pixel) {
		if ( (bit%2==0) && (pixel.getRed()%2==1) ) { //alors bit a cacher = 0 et dernier bit rouge = 1 -> remplacement
			Color temp = pixel;
			pixel = new Color(temp.getRed()-1, temp.getGreen(), temp.getBlue(), temp.getAlpha());
		}
		if ( ( bit%2==1) && (pixel.getRed()%2==0)) { //alors bit a cacher = 1 et dernier bit rouge = 0 -> remplacement
			Color temp = pixel;
			pixel = new Color (temp.getRed()+1, temp.getGreen(), temp.getBlue(), temp.getAlpha());
		}
		//dans les autres cas les 2 bits sont tous les deux egaux a 0 ou 1 -> rien a faire
		return pixel;
	}
	
	/**
	 * Modifie la valeur du dernier bit de la composante verte d'un pixel, si celui-ci est different du bit passe en parametre.
	 * @param bit Le bit que l'on veut cacher dans la composante verte du pixel.
	 * @param pixel Le pixel a modifier.
	 * @return Le pixel modifie.
	 */
	public Color setLSBVert(char bit, Color pixel) {
		if ( (bit%2==0) && (pixel.getGreen()%2==1) ) {
			Color temp = pixel;
			pixel = new Color(temp.getRed(), temp.getGreen()-1, temp.getBlue(), temp.getAlpha());
		}
		if ( (bit%2==1) && (pixel.getGreen()%2==0) ) {
			Color temp = pixel;
			pixel = new Color(temp.getRed(), temp.getGreen()+1, temp.getBlue(), temp.getAlpha());
		}
		return pixel;
	}
	
	/**
	 * Modifie la valeur du dernier bit de la composante bleue d'un pixel, si celui-ci est different du bit passe en parametre.
	 * @param bit Le bit que l'on veut cacher dans la composante bleue du pixel.
	 * @param pixel Le pixel a modifier.
	 * @return Le pixel modifie.
	 */
	public Color setLSBBleu(char bit, Color pixel) {
		if ( (bit%2==0) && (pixel.getBlue()%2==1) ) {
			Color temp = pixel;
			pixel = new Color(temp.getRed(), temp.getGreen(), temp.getBlue()-1, temp.getAlpha());
		}
		if ( (bit%2==1) && (pixel.getBlue()%2==0) ) {
			Color temp = pixel;
			pixel = new Color(temp.getRed(), temp.getGreen(), temp.getBlue()+1, temp.getAlpha());
		}
		return pixel;
	}
	
	/**
	 * Modifie la valeur du dernier bit de la composante alpha d'un pixel, si celui-ci est different du bit passe en parametre.
	 * @param bit Le bit que l'on veut cacher dans la composante alpha du pixel.
	 * @param pixel Le pixel a modifier.
	 * @return Le pixel modifie.
	 */
	public Color setLSBAlpha(char bit, Color pixel) {
		
		if ( (bit%2==0) && (pixel.getAlpha()%2==1) ) {
			Color temp = pixel;
			pixel = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), temp.getAlpha()-1);
		}
		if ( (bit%2==1) && (pixel.getAlpha()%2==0) ) {
			Color temp = pixel;
			pixel = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), temp.getAlpha()+1);
		}
		return pixel;
	}
	
	@Override
	public String toString() {
		return "Le message decode dans cette image est: " + messageDecode;
	}
}