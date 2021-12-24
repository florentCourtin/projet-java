package metadata;

import java.io.IOException;

import com.drew.imaging.ImageProcessingException;

import steganographie.Stegano;

public class Main {

	public static void main(String[] args) {
		
		if (args[0].equals("-f")) {
			try {
				Meta m = new Meta("-f", args[1]);
				m.extractMeta();
				System.out.println(m);
			} catch (IsNotDirectoryException e) {
				e.printStackTrace();
			} catch (IsNotFileException e) {
				e.printStackTrace();
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("-d")) {
			try {
				Meta m = new Meta("-d", args[1]);
				m.extractMeta();
				System.out.println(m);
			} catch (IsNotDirectoryException e) {
				e.printStackTrace();
			} catch (IsNotFileException e) {
				e.printStackTrace();
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("-s")) {
			try {
				Stegano s = new Stegano(args[1]);
				s.cacherMessage(args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("-e")) {
			try {
				Stegano s = new Stegano(args[1]);
				s.decoder();
				System.out.println(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("-h")) {
			System.out.println("Vous avez ouvert l'option d'aide -h pour l'extraction des metadonnees et la steganographie"
					+ "\n\nLa classe Meta permet d'extraire les metadonnees d'une image ou de l'ensemble des image d'un repertoire"
					+ "\nCela s'applique uniquement pour les images au format png ou jpeg"
					+ "\n\nLa classe Stegano permet de cacher un message dans une image, ou bien d'en extraire un"
					+ "\nCela s'applique uniquement pour les images au format png ou jpeg"
					+ "\n\n     ###ARGUMENTS POUR METADONNEES###"
					+ "\n-d, chemin                extraie les metadonnees des images d'un repertoire donne"
					+ "\n-f, chemin                extraie les metadonnees d'une image donnee"
					+ "\n\n     ###ARGUMENTS POUR STEGANOGRAPHIE###"
					+ "\n-s, chemin, message       cache un message dans une image donnee"
					+ "\n-e, chemin                extraie un message d'une image donnee, s'il y en a un"
			);
		} else {
			System.out.println("Erreur: affichez l'aide avec l'option -h");
		}
	}
}
