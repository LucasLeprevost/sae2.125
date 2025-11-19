package source.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import source.Controleur;
import source.metier.CheminCritique;
import source.metier.Tache;

/**
 * Classe Vue qui permet l'affichage de l'application
 */
public class FrameGraphe extends JFrame implements ActionListener
{
	private File          dernierFichier = new File("./data");

	private Controleur    ctrl               ;
	private PanelGraphe   graph              ;
	private FrameStart    frameStart         ;
	
	private PanelModifier panelModifier      ;
	private PanelAjouter  panelAjouter       ;
	private PanelSuppr    panelSuppr         ;
	
	private JButton       btnTot             ;
	private JButton       btnTard            ;
	private JButton       btnCheminCritique  ;
	private JButton       btnAfficherTout    ;
	private JCheckBox     cbDate             ;
	
	private JMenuBar      menuBar            ;
	private JMenu         menuFichier        ;
	private JMenu         menuProjet         ;
	
	private JMenuItem     menuiItemQuitter   ;
	private JMenuItem     menuiChargerFichier;
	private JMenuItem     menuiChangerDate   ;
	private JMenuItem     menuiReinitialiser ;
	
	private boolean       ccActif;
	
	/* ===================================== */
	/* CONSTRUCTEUR                          */
	/* ===================================== */
	
	/**
	 * Crée la fenêtre principale avec tous les composants graphiques.
	 * @param ctrl le contrôleur de l'application
	 */
	public FrameGraphe( Controleur ctrl )
	{
		this.ctrl = ctrl;
		this.ccActif = false;
		
		this.frameStart = new FrameStart( ctrl, this );
		this.frameStart.setVisible      ( false      );
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setTitle    ( "Graphe MPM"                        );
		this.setSize     ( screenSize.width, screenSize.height );
		this.setLocation ( 40, 40                              );
		this.setLayout   ( new BorderLayout()                  );
		
		/* ----------------------------- */
		/* Création des Composants       */
		/* ----------------------------- */
		
		JPanel panelOption = new JPanel();
		panelOption.setLayout( new GridLayout   ( 1, 3, 10, 10 )  );
		
		this.panelModifier   = new PanelModifier( this            );
		this.panelAjouter    = new PanelAjouter ( this, this.ctrl );
		this.panelSuppr      = new PanelSuppr   ( this            );
		
		JScrollPane scroll = new JScrollPane();
		
		JPanel panelBoutons;			
		
		//JMenuBar
		this.menuBar             = new JMenuBar  ();
		this.menuFichier         = new JMenu     ( "Fichier"         );
		this.menuiChargerFichier = new JMenuItem ( "Charger Fichier" );
		this.menuiItemQuitter    = new JMenuItem ( "Quitter"         );
		
		this.menuProjet          = new JMenu     ( "Projet"          );
		this.menuiChangerDate    = new JMenuItem ( "Changer dates"   );
		this.menuiReinitialiser  = new JMenuItem ( "Reinitialiser"   );
		
		this.graph = new PanelGraphe( this.ctrl , this );
		scroll.setViewportView      ( this.graph       );
		
		panelBoutons = new JPanel();
		panelBoutons.setBackground( Color.LIGHT_GRAY );
		
		this.cbDate = new JCheckBox( "Format JJ/MM" );
		
		
		this.btnTot            = new JButton ( "Plus tôt"                 );
		this.btnTot            .setBackground( new Color( 125, 210, 120 ) );
		this.btnTot            .setEnabled   ( false                      );
		
		this.btnTard           = new JButton ( "Plus tard"                );
		this.btnTard           .setBackground( new Color( 210, 120, 120 ) );
		this.btnTard           .setEnabled   ( false                      );
		
		this.btnCheminCritique = new JButton ( "Chemin Critique"          );
		this.btnCheminCritique .setBackground( new Color( 210, 200, 120 ) );
		this.btnCheminCritique .setEnabled   ( false                      );
		
		this.btnAfficherTout   = new JButton ( "Afficher tout"            );
		this.btnAfficherTout   .setBackground( new Color( 180, 120, 210 ) );
		this.btnAfficherTout   .setEnabled   ( false                      );
		
		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */
		
		// Ajout des composants du JMenu
		this.menuBar    .add( this.menuFichier             );
		this.menuBar    .add( this.menuProjet              );
		
		this.menuFichier.add( this.menuiChargerFichier     );
		this.menuFichier.addSeparator();
		this.menuFichier.add( this.menuiItemQuitter        );
		
		this.menuProjet .add( this.menuiChangerDate        );
		this.menuProjet.addSeparator();
		this.menuProjet .add( this.menuiReinitialiser      );
		
		this.menuFichier        .setMnemonic( 'F' );
		this.menuiChargerFichier.setMnemonic( 'N' );
		this.menuiItemQuitter   .setMnemonic( 'Q' );
		
		this.menuProjet         .setMnemonic( 'P' );
		this.menuiChangerDate   .setMnemonic( 'C' );
		this.menuiReinitialiser .setMnemonic( 'R' );
		
		this.menuiChargerFichier.setAccelerator ( KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK ) );
		this.menuiItemQuitter   .setAccelerator ( KeyStroke.getKeyStroke( KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK ) );
		
		this.menuiChangerDate   .setAccelerator ( KeyStroke.getKeyStroke( KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK ) );
		this.menuiReinitialiser .setAccelerator ( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK ) );
		
		
		panelBoutons.add ( this.btnTot            );
		panelBoutons.add ( new JLabel("  ")       );
		panelBoutons.add ( this.btnTard           );
		panelBoutons.add ( new JLabel("  ")       );
		panelBoutons.add ( this.btnCheminCritique );
		panelBoutons.add ( new JLabel("  ")       );
		panelBoutons.add ( this.btnAfficherTout   );
		panelBoutons.add ( new JLabel("  ")       );
		panelBoutons.add ( this.cbDate            );
		
		this.add ( scroll      , BorderLayout.CENTER );
		this.add ( panelBoutons, BorderLayout.NORTH  );
		this.add ( panelOption , BorderLayout.SOUTH  );
		
		
		panelOption.add( this.panelAjouter  );
		panelOption.add( this.panelModifier );
		panelOption.add( this.panelSuppr    );
		
		this.setJMenuBar( menuBar );
		
		/* ----------------------------- */
		/* Activation des Composants     */
		/* ----------------------------- */
		
		this.btnTot             .addActionListener ( this );
		this.btnTard            .addActionListener ( this );
		this.btnCheminCritique  .addActionListener ( this );
		this.btnAfficherTout    .addActionListener ( this );
		
		this.menuiItemQuitter   .addActionListener ( this );
		this.menuiChargerFichier.addActionListener ( this );
		this.menuiChangerDate   .addActionListener ( this );
		this.menuiReinitialiser .addActionListener ( this );
		
		this.cbDate             .addActionListener ( this );
		
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setVisible( true );
	}
	
	/* ===================================== */
	/* MODIFICATEURS                         */
	/* ===================================== */
	
	/**
	 * Met à jour la tâche sélectionnée dans les panels.
	 * @param t la tâche à mettre à jour (null si aucune sélection)
	 */
	public void updateTacheActuelle( Tache t ) 
	{ 
		if ( t != null )
		{
			this.panelModifier.instancierTacheActuelle( t );
			this.panelSuppr   .instancierTacheActuelle( t );
		}
		else
		{
			this.panelModifier.instancierTacheActuelle( null );
			this.panelSuppr   .instancierTacheActuelle( null );
		}
		
		this.maj();
	}
	
	/**
	 * Supprime une tâche du projet et met à jour l'interface.
	 * @param tache la tâche à supprimer
	 */
	public void supprimerDonnee ( Tache tache )
	{
		this.ctrl.supprimer(tache);
		this.panelModifier.supprimerDonnee();
		this.panelAjouter.majListeTaches();
	}
	
	/**
	 * Met à jour l'affichage de la fenêtre.
	 */
	public void maj()
	{
		this.repaint();
		this.graph.repaint();
	}
	
	/**
	 * Lance le calcul des dates du projet.
	 */
	public void calculerDates()
	{
		this.ctrl.calculerDates();
	}
	
	/**
	 * Remet à zéro l'affichage du graphe.
	 */
	public void resetFrame()
	{
		JScrollPane scroll = (JScrollPane) this.getContentPane().getComponent( 0 ); // Récupère le JScrollPane
		scroll.setViewportView( null );
		
		this.graph = new PanelGraphe( this.ctrl, this );
		
		scroll.setViewportView( this.graph );
		
		this.cbDate.setSelected( false );
		
		this.btnTot           .setEnabled( true  );
		this.btnTard          .setEnabled( false );
		this.btnCheminCritique.setEnabled( false );
		this.btnAfficherTout  .setEnabled( true  );
		
		this.maj();
	}
	
	/* ===================================== */
	/* ACCESSEURS                            */
	/* ===================================== */
	
	/**
	 * Retourne la tâche de début du projet.
	 * @return la tâche de début
	 */
	public Tache getDebut() { return this.ctrl.getDebut(); }
	
	/**
	 * Retourne la tâche de fin du projet.
	 * @return la tâche de fin
	 */
	public Tache getFin() { return this.ctrl.getFin(); }
	
	/* ===================================== */
	/* AUTRES MÉTHODES                       */
	/* ===================================== */
	
	/**
	 * Gère les événements des boutons et menus.
	 * @param e l'événement déclenché
	 */
	public void actionPerformed( ActionEvent e )
	{
		if ( e.getSource() == this.btnTot  )
		{
			if ( ! this.graph.incrementerRang() )
			{
				this.btnTot .setEnabled( false );
				this.btnTard.setEnabled( true  );
			}
		}
		
		if ( e.getSource() == this.btnTard )
		{
			
			if ( ! this.graph.decrementerRang() )
			{
				this.btnTard          .setEnabled( false );
				this.btnCheminCritique.setEnabled( true  );
			}
		}
		
		if ( e.getSource() == this.btnCheminCritique )
		{
			
			this.ctrl.construireChemin();
			
			List<CheminCritique> lstCheminsCritique;
			
			lstCheminsCritique = new ArrayList<>( this.ctrl.getLstCheminCritique()    );
			this.graph.afficherCheminCritique   ( lstCheminsCritique, this.ccActif    );
			
			this.ccActif = !this.ccActif;
		}
		
		if ( e.getSource() == this.btnAfficherTout )
		{
			this.graph            .afficherTout();
			this.btnTot           .setEnabled( false );
			this.btnTard          .setEnabled( false );
			this.btnCheminCritique.setEnabled( true  );
			this.btnAfficherTout  .setEnabled( false );
		}
		
		if ( e.getSource() == this.menuiItemQuitter )
		{
			System.exit( 0 );
		}
		
		if ( e.getSource() == this.menuiChargerFichier )
		{
			JFileChooser choisirFichier = new JFileChooser();
			choisirFichier.setCurrentDirectory( this.dernierFichier             );
			choisirFichier.setDialogTitle     ( "Charger un fichier de données" );
			
			int retour = choisirFichier.showOpenDialog( this );
			
			if ( retour == JFileChooser.APPROVE_OPTION )
			{
				File fichierSelectionne = choisirFichier.getSelectedFile().getParentFile();
				if ( fichierSelectionne != null )
				{
					this.dernierFichier = fichierSelectionne;
				}
				
				this.ctrl.reset();
				
				String cheminFichier = choisirFichier.getSelectedFile().getAbsolutePath();
				
				this.ctrl.initMPM     ( cheminFichier );
				this.panelAjouter.majListeTaches();
				
				this.btnTot         .setEnabled( true );
				this.btnAfficherTout.setEnabled( true );
				
				this.resetFrame();
			}
		}
		
		if ( e.getSource() == this.menuiChangerDate)
		{
			this.frameStart.setVisible( true );
		}
		
		
		if ( e.getSource() == this.menuiReinitialiser )
		{
			//On vérifie que il y a un fichier actif
			if ( this.ctrl.getFin() != null )
			{
				this.resetFrame();
			}
		}
		
		if ( e.getSource() == this.cbDate )
		{
			this.graph.changerDate();
		}
		
		
		this.maj();
	}
}
