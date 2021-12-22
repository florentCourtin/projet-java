package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.drew.imaging.ImageProcessingException;

import metadata.IsNotDirectoryException;
import metadata.IsNotFileException;
import metadata.Meta;
import metadata.MetaImage;
import steganographie.Stegano;

public class MetaSteganoGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea centre;
	private JList<File> filesList;
	
	public MetaSteganoGUI() {
		
		//construction de la fenetre
		super("Metadonnees & Steganographie");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(900, 600);
		this.setLocationRelativeTo(null);
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		//creation de la zone nord : extraire les metadonnees
		contentPane.add(createNorth(), BorderLayout.NORTH);
		
		//creation de la zone centrale : affichage des metadonnees
		this.centre =  new JTextArea();
		centre.setEditable(false);
		JScrollPane scrollPaneCentre = new JScrollPane(centre);
		contentPane.add(scrollPaneCentre);
		
		//creation de la zone ouest avec liste de tous les fichiers de types jpeg ou png
		//filesList est rempli lors de la selection du repertoire
		filesList = new JList<File>();
		filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollFilesList = new JScrollPane(filesList);
		scrollFilesList.setPreferredSize(new Dimension(250, 0));
		contentPane.add(scrollFilesList, BorderLayout.WEST);
		
		
		/* quand on clique sur un des elements de liste
		 * les infos principales s'affichent dans la zone sud
		 * (nom, type, taille, date de derniere modif)
		 * + boutons qui proposent la steganographie
		 * si on selectionne "cacher" une fenetre s'ouvre pour saisir le message
		 * si on selectionne "decrypter" une fenetre s'ouvre pour afficher le message decrypte
		 */
		filesList.addListSelectionListener(new ActionClickListElement());
	}
	
	
	private JPanel createNorth() {
		
		JPanel panelSelect = new JPanel(new FlowLayout());
		
		JLabel lblSelect = new JLabel("Selectionnez un fichier pour extraire les metadonnees: ");
		JButton btnOpen = new JButton("Ouvrir...");
		
		panelSelect.add(lblSelect);
		panelSelect.add(btnOpen);
		
		btnOpen.addActionListener(new ActionOpen());
		
		return panelSelect;
	}
	
	
	class ActionClickListElement implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			File f = filesList.getSelectedValue();
			getContentPane().add(createSouth(f), BorderLayout.SOUTH);
			getContentPane().revalidate();
		}
	}
	
	
	private JPanel createSouth(File f) {
		
		JPanel panelSud = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
		
		//PARTIE 1 : INFOS SUR LE FICHIER CLIQUE
		//nom du fichier
		JLabel lblNom = new JLabel(f.getName());
		
		//format du fichier : jpeg ou png
		String format = "";
		try {
			format = new MetaImage(f.getPath()).imageFormat();
		} catch (ImageProcessingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JLabel lblType = new JLabel(format);
		
		//taille du fichier en Ko ou Mo selon sa taille
		JLabel lblTaille = new JLabel("");
		double tailleByte = f.length();
		double tailleOctet = tailleByte / 1024;
		if (tailleOctet>1024) {
			tailleOctet = tailleOctet / 1024;
			String taille = "";
			for (int i=0; i<5; i++) {
				taille += String.valueOf(tailleOctet).charAt(i);
			}
			lblTaille.setText(taille + " Mo");
		} else {
			String taille = "";
			for (int i=0; i<5; i++) {
				taille += String.valueOf(tailleOctet).charAt(i);
			}
			lblTaille.setText(taille + " Ko");
		}
		
		//derniere modif du fichier
		long derModif = f.lastModified();
		//String pattern = "yyyy-MM-dd hh:mm aa";
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date derModifFormat = new Date(derModif);
		JLabel lblDate = new JLabel(derModifFormat.toString());
		
		panelSud.add(lblNom);
		panelSud.add(lblType);
		panelSud.add(lblTaille);
		panelSud.add(lblDate);
		
		//PARTIE 2 : BOUTONS POUR LA STEGANOGRAPHIE
		JButton btnCacher = new JButton("Cacher");
		btnCacher.setToolTipText("Cacher un message dans l'image selectionnee");
		btnCacher.addActionListener(new ActionCacher());
		panelSud.add(btnCacher);
		
		JButton btnExtraire = new JButton("Extraire");
		btnExtraire.setToolTipText("Extraire un message dans l'image selectionnee");
		btnExtraire.addActionListener(new ActionExtraire());
		panelSud.add(btnExtraire);
		
		return panelSud;
	}
	
	
	class ActionOpen implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Ouvrir ");
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int res = jfc.showOpenDialog(null);
			
			if (res == JFileChooser.APPROVE_OPTION) {
				if (jfc.getSelectedFile().isDirectory()) {
					try {
						Meta m = new Meta("-d", jfc.getSelectedFile().getAbsolutePath());
						m.extractMeta();
						centre.setText(m.toString());
						
						ArrayList<MetaImage> imagesArray = m.getMetaImageArray();
						File[] tab = new File[imagesArray.size()];
						for (int i=0; i<tab.length; i++) {
							tab[i] = imagesArray.get(i).getF();
						}
						filesList.setListData(tab);
					} catch (IsNotDirectoryException e1) {
						e1.printStackTrace();
					} catch (IsNotFileException e1) {
						e1.printStackTrace();
					} catch (ImageProcessingException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (jfc.getSelectedFile().isFile()) {
					try {
						MetaImage mi = new MetaImage(jfc.getSelectedFile().getPath());
						mi.extractMetaImage();
						centre.setText(mi.toString());
						File[] tab = new File[1];
						tab[0] = mi.getF();
						filesList.setListData(tab);
					} catch (ImageProcessingException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	

	class ActionCacher implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			String message = (String)JOptionPane.showInputDialog(getContentPane(), "message: ", "Cacher un message", JOptionPane.QUESTION_MESSAGE);
			String path = filesList.getSelectedValue().getPath();
			try {
				Stegano s = new Stegano(path);
				s.cacherMessage(message);
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
		}	
	}
	
	
	class ActionExtraire implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			String path = filesList.getSelectedValue().getPath();
			String messageDecode = null;
			try {
				Stegano s = new Stegano(path);
				s.decoder();
				messageDecode = s.toString();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(getContentPane(), messageDecode, "Extraire un message", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	
	public static void main(String[] args) {
		MetaSteganoGUI msg = new MetaSteganoGUI();
		msg.setVisible(true);
	}
}
