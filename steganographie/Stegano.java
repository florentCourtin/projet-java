package steganographie;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Stegano {
	private String path;
	private BufferedImage image;
	private BufferedImage newImage;
	private WritableRaster trame;
	private Color[][] pixels;
	private int largeur;
	private int hauteur;
	private String messageDecode;
	
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
	
	
	public String convertToBinary(String text) {
		String bitsText = new String();
		for (int i=0; i<text.length(); i++) {
			bitsText += String.format("%8s", Integer.toBinaryString((byte)text.charAt(i) & 0xFF)).replace(' ', '0');
		}
		return bitsText;
	}
	
	//convertit une suite de binaires en caractere ascii
	public String convertToString(String bitsString) {
		String result = "";
		String[] temp  = bitsString.split("(?<=\\G........)");
		for (int i=0; i<temp.length; i++) {
			result += (char)Byte.parseByte(temp[i], 2);
		}
		return result;
	}
	
	
	public void cacherMessage(String message) throws IOException {
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
		ImageIO.write(newImage, "png", new File(path));
	}
	
	
	public void creerTag(String message) { //TAG MAL CACHE AVEC PAINT 3D
		final String TAG = convertToBinary(String.valueOf(message.length()*8) + "T");
		
		/*
		 * le tag sera donc egal a XT, X etant le nombre de bits du message cache
		 * grace a ce X on pourra retrouver la longueur du message lors de l'extraction du texte
		 * on cachera ce tag en partant du dernier pixel de l'image
		 * pour retrouver ce tag, on placera dans un tableau les LSB des pixels en partant de la fin
		 * pour chaque 8 bits recuperes, on aura soit un caractere ascii correspondant a un nombre,
		 * soit un caractere ascii representant un T et dans ce cas on saura que les bits precedents representent 
		 * le nombre de bits du message caches
		 * 
		 */
		
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
	
	public void decoder() {
		int longueurMessageBits = getLongueurMessage();
		/*
		 * on connait donc le nombre de bits qui constitue le message
		 * plus qu'a les recuperer et les convertir
		 */
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
					/*
					 * on stocke les LSB des 20 derniers pixels
					 * car on sait que le tag est a la fin
					 */
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
		/*
		 * trouver une boucle pour automatiser cette etape ?
		 * tab8 contient donc dans chaque case 8 bits correspondant a 1 caractere
		 * ex: si on trouve T dans la troisieme case alors on saura que la taille du message est contenue
		 * dans la case 1 et 2
		 */
		
		int t = decrypteTag(tab8); //la methode decrypteTag retourne la position de la lettre T dans notre tableau
		String longueurMessageBits = "";
		for (int i=0; i<t; i++) {
			longueurMessageBits += tab8[i];
		}
		//longueurMessageBits correspond donc au nombre (en bits) de caracteres du message
		String longueurMessageString = convertToString(longueurMessageBits); //on convertit ce nombre en bits en string
		int longueurMessageInt = Integer.parseInt(longueurMessageString); //on convertit ce nombre en string en int
		//ou: a la place des deux // on peut aussi faire return longueurMessageBits.length
		return longueurMessageInt;
	}
	
	
	public int decrypteTag(String[] tab) {
		final String bitsTag = "01010100"; //correspond a "T"
		int indiceTag = -1;
		for (int i=0; i<tab.length; i++) {
			if (tab[i].equals(bitsTag)) {
				indiceTag = i;
			}
		}
		return indiceTag;
	}
	
	
	public String getLSBRouge(Color pixel) {
		String bit;
		if (pixel.getRed()%2==0) {
			bit = "0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	public String getLSBVert(Color pixel) {
		String bit;
		if (pixel.getGreen()%2==0) {
			bit ="0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	public String getLSBBleu(Color pixel) {
		String bit;
		if (pixel.getBlue()%2==0) {
			bit ="0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	public String getLSBAlpha(Color pixel) {
		String bit;
		if (pixel.getAlpha()%2==0) {
			bit ="0";
		} else {
			bit ="1";
		}
		return bit;
	}
	
	
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
	
	public String toString() {
		return "Le message decode dans cette image est: " + messageDecode;
	}
	
	public static void main(String[] args) {
		
		Stegano s;
		
		if (args[0].equals("-s")) {
			try {
				s = new Stegano(args[1]);
				s.cacherMessage(args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} else if (args[0].equals("-e")) {
			try {
				s = new Stegano(args[1]);
				s.decoder();
				System.out.println(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("-h")) {
			System.out.println("Vous avez ouvert l'option d'aide -h pour la classe Stegano");
			System.out.println("Cette classe permet de cacher un message dans une image et de le decrypter");
			System.out.println("Voici les arguments possibles: ");
			System.out.println("    -s, chemin, message        -s pour cacher un message, chemin pour indiquer ou se trouve l'image, message pour le texte que l'on veut cacher");
			System.out.println("    -e, chemin                 -e pour extraire un message, chemin pour indiquer ou se trouve l'image");
		} else {
			System.out.println("Erreur: pour afficher l'aide, utilisez l'option -h");
		}
	}

}
