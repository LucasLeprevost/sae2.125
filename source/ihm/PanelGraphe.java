package source.ihm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import source.Controleur;
import source.metier.Arete;
import source.metier.CheminCritique;
import source.metier.Noeud;
import source.metier.Tache;

/**
 * Classe qui crée et gère l'affichage du graphe MPM (Méthode du Potentiel Métra).
 * Cette classe hérite de JPanel et permet de visualiser graphiquement un réseau de tâches.
 */
public class PanelGraphe extends JPanel
{
	private Controleur       ctrl            ;
	private FrameGraphe      frame           ;
	private Graphics2D       g2              ;
	
	private ArrayList<Noeud> lstNoeud        ;
	private ArrayList<Arete> lstArete        ;
	private Noeud            noeudSelectionne;
	private Point            decalageSouris  ;
	private boolean          date            ;
	
	private int              rangTot         ;
	private int              rangTard        ;
	
	/*========================================*/
	/*            CONSTRUCTEUR                */
	/*========================================*/
	
	/**
	 * Constructeur de la classe GrapheMPM.
	 * Initialise le graphe en positionnant les nœuds et en créant les arêtes
	 * 
	 * @param ctrl Le contrôleur de l'application
	 * @param frame La fenêtre qui contient ce graphe
	 */
	public PanelGraphe( Controleur ctrl, FrameGraphe frame )
	{
		this.setBackground( new Color( 245, 245, 245 ) );

		this.lstNoeud         = new ArrayList<>();
		this.lstArete         = new ArrayList<>();
		
		this.noeudSelectionne = null;
		this.decalageSouris   = null;
		
		this.ctrl             = ctrl;
		this.frame            = frame;
		
		this.date             = false;
		
		this.rangTot          = 0;
		
		Tache fin             = this.ctrl.getFin();
		
		if (fin != null)
		{
			this.rangTard = fin.calculerRang() + 1;
		}
		else
		{
			this.rangTard = 0; // 0 = valeur par défaut
		}
		
		// Rangs : chaque tâche est associée à un niveau de profondeur dans le graphe
		ArrayList< ArrayList<Noeud> > niveaux = new ArrayList<>();
		
		for ( int cpt = 0; cpt < this.ctrl.getNbTaches(); cpt++ )
		{
			Tache tache = this.ctrl .getTache    ( cpt );
			int   rang  =      tache.calculerRang(     );
			
			Noeud noeud = new Noeud( tache, 0, 0 ); // position temporaire
			
			while ( niveaux.size() <= rang )
			{
				niveaux.add( new ArrayList<>() );
			}
			
			niveaux.get( rang ).add( noeud );
			this.lstNoeud      .add( noeud );
		}
		
		// Positionnement des nœuds : placement en V
		int espaceX = 200;
		int espaceY = 100;
		int centreY = 300; // Position Y centrale pour l'alignement
		
		for ( int rang = 0; rang < niveaux.size(); rang++ )
		{
			ArrayList<Noeud> niveau = niveaux.get( rang );
			
			int nbNoeuds = niveau.size();
			int xPos     = 100 + rang * espaceX;
			
			if ( nbNoeuds == 1 )
			{
				// Une seule tâche : positionnement central
				niveau.get( 0 ).setX( xPos    );
				niveau.get( 0 ).setY( centreY );
			}
			else if ( nbNoeuds % 2 == 0 )
			{
				// Nombre pair : placement en V
				positionnerEnV( niveau, xPos, centreY, espaceY );
			}
			else
			{
				// Nombre impair : une tâche au centre, les autres en V
				// Placer la première tâche au centre (index 0)
				niveau.get(0).setX(xPos);
				niveau.get(0).setY(centreY);
				
				// Créer une liste temporaire sans la tâche centrale (première)
				ArrayList<Noeud> noeudsV = new ArrayList<>();
				for ( int i = 1; i < nbNoeuds; i++ )
				{
					noeudsV.add(niveau.get(i));
				}
				
				// Positionner les autres en V
				if ( ! noeudsV.isEmpty() )
				{
					positionnerEnV( noeudsV, xPos, centreY, espaceY );
				}
			}
		}
		
		// Création des arêtes
		for ( Noeud noeud : lstNoeud )
		{
			Tache tache = noeud.getTache();
			for ( Tache tacheSvt : tache.getLstTachesSvt() )
			{
				for ( Noeud noeudSvt : lstNoeud )
				{
					if ( noeudSvt.getTache() == tacheSvt )
					{
						boolean existeDeja = false;
						for ( Arete a : lstArete )
						{
							if ( a.getDebut() == noeud && a.getFin() == noeudSvt )
							{
								existeDeja = true;
								break;
							}
						}
						if ( ! existeDeja )
						{
							lstArete.add( new Arete(noeud, noeudSvt) );
						}
					}
				}
			}
		}
		
		//Calculer la taille de l'ensemble des noeuds pour définir la taille du panel
		int tailleGraphe = calculerTailleGraphe();
		this.setPreferredSize( new Dimension ( tailleGraphe + 100 , tailleGraphe + 100 ));
		
		GereSouris gereSouris = new GereSouris( );
		this.addMouseListener      ( gereSouris );
		this.addMouseMotionListener( gereSouris );
	}

	/*========================================*/
	/*            MODIFICATEURS               */
	/*========================================*/
	
	/**
	 * Bascule entre l'affichage des dates en jours relatifs et en dates réelles.
	 */
	public void changerDate()
	{
		this.date = !this.date;
	}
	
	/**
	 * Met en évidence le chemin critique sur le graphe en colorant les arêtes.
	 * Les arêtes critiques sont affichées en rouge, les autres en bleu.
	 * 
	 * @param lstCritique Liste des chemins critiques à afficher
	 * @param actif Indique si l'affichage du chemin critique est activé
	 */
	public void afficherCheminCritique( List<CheminCritique> lstCritique, boolean actif )
	{
		for ( Arete arete : this.lstArete )
		{
			if ( this.verifierArete( lstCritique, arete ) && !actif )  // Passer la liste des chemins au lieu des tâches
			{
				arete.setColor( Color.ORANGE );
			}
			else
			{
				arete.setColor( Color.BLUE );
			}
		}
		
		this.frame.maj();
	}
	
	/*========================================*/
	/*            ACCESSEURS                  */
	/*========================================*/
	
	/**
	 * Retourne la tâche actuellement sélectionnée dans le graphe.
	 * Exclut les tâches de début et de fin du projet.
	 * 
	 * @return La tâche sélectionnée, ou null si aucune tâche n'est sélectionnée
	 *         ou si c'est la tache Début ou Fin
	 */
	public Tache getTacheSelectionne()
	{
		return this.noeudSelectionne != null && 
		       this.noeudSelectionne != this.lstNoeud.get(0) &&
		       this.noeudSelectionne != this.lstNoeud.get( this.lstNoeud.size() -1 ) ? this.noeudSelectionne.getTache() : null; 
	}
	
	/*========================================*/
	/*         AUTRES MÉTHODES - boolean      */
	/*========================================*/
	
	/**
	 * Vérifie si une arête fait partie d'un chemin critique.
	 * Une arête est critique si elle relie deux tâches consécutives d'un chemin critique
	 * et respecte les conditions de dates et de marge nulle.
	 * 
	 * @param lstCheminsCritiques Liste des chemins critiques du projet
	 * @param arete L'arête à vérifier
	 * @return true si l'arête fait partie d'un chemin critique, false sinon
	 */
	public boolean verifierArete( List<CheminCritique> lstCheminsCritiques, Arete arete )
	{
		Tache tacheDebut = arete.getDebut().getTache();
		Tache tacheFin   = arete.getFin  ().getTache();
		
		for ( CheminCritique chemin : lstCheminsCritiques ) 
		{
			List<Tache> tachesChemin = chemin.getLstTache();
			
			for ( int cpt = 0; cpt < tachesChemin.size() - 1; cpt++ ) 
			{
			
				if ( tachesChemin.get( cpt     ).equals( tacheDebut )                                     &&
				     tachesChemin.get( cpt + 1 ).equals( tacheFin   )                                     &&
				     tacheFin.getDateAuPlusTot() - tacheDebut.getDateAuPlusTot() == tacheDebut.getDuree() &&
				     tacheFin.getMarge() == 0                                                             &&
				     tacheFin.getMarge() == 0                                                                 )
				{
					// Vérifier en plus la condition de dates
					if ( tacheFin.getDateAuPlusTot() - tacheDebut.getDateAuPlusTot() == tacheDebut.getDuree() ) 
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void afficherTout()
	{
		this.rangTot  = this.ctrl.getFin().getRang();
		this.rangTard = 0;
	}
	
	/**
	 * Incrémente le rang total pour la visualisation progressive du projet.
	 * 
	 * @return true si l'incrémentation est possible, false si la limite est atteinte
	 */
	public boolean incrementerRang()
	{
		return ++this.rangTot < this.rangTard - 1;
	}
	
	/**
	 * Décrémente le rang tardif pour la visualisation progressive du projet.
	 * 
	 * @return true si la décrémentation est possible, false si la limite est atteinte
	 */
	public boolean decrementerRang()
	{
		return --this.rangTard > 1;
	}
	
	/*========================================*/
	/*         AUTRES MÉTHODES - void        */
	/*========================================*/
	
	/**
	 * Méthode héritée de JPanel pour dessiner le composant.
	 * Configure le contexte graphique et lance le dessin du graphe.
	 * 
	 * @param g Le contexte graphique fourni par Swing
	 */
	protected void paintComponent( Graphics g )
	{
		this.g2 = ( Graphics2D ) g;
		
		super.paintComponent( this.g2 );
		this.dessinerGraphe ();
	}
	
	/*========================================*/
	/*         MÉTHODES PRIVÉES - int         */
	/*========================================*/
	
	/**
	 * Calcule la taille totale nécessaire pour afficher le graphe complet.
	 * Détermine les dimensions maximales en parcourant tous les nœuds.
	 * 
	 * @return La taille maximale (largeur ou hauteur) nécessaire pour le graphe
	 */
	private int calculerTailleGraphe()
	{
		int maxX = 0;
		int maxY = 0;
		
		for ( Noeud noeud : lstNoeud )
		{
			if ( noeud.getX() + noeud.getLargeur() > maxX )
			{
				maxX = noeud.getX() + noeud.getLargeur();
			}
			
			if ( noeud.getY() + noeud.getHauteur() > maxY )
			{
				maxY = noeud.getY() + noeud.getHauteur();
			}
		}
		
		return Math.max( maxX, maxY );
	}
	
	/*========================================*/
	/*        MÉTHODES PRIVÉES - void        */
	/*========================================*/
	
	/**
	 * Positionne une liste de nœuds selon un arrangement en forme de V.
	 * Les nœuds sont répartis symétriquement autour d'un centre vertical.
	 * 
	 * @param noeuds Liste des nœuds à positionner
	 * @param xPos Position X commune à tous les nœuds
	 * @param centreY Position Y centrale autour de laquelle organiser le V
	 * @param espaceY Espacement vertical entre les nœuds
	 */
	private void positionnerEnV( ArrayList<Noeud> noeuds, int xPos, int centreY, int espaceY )
	{
		int nbNoeuds = noeuds.size();
		
		for ( int i = 0; i < nbNoeuds; i++ )
		{
			Noeud noeud = noeuds.get(i);
			noeud.setX( xPos );
			
			// Calculer la position dans le V
			int positionDansV = i - nbNoeuds / 2;
			
			if ( positionDansV < 0 )
			{
				// Partie haute du V (positions négatives)
				noeud.setY( centreY + positionDansV * espaceY );
			}
			else
			{
				// Partie basse du V (positions positives)
				noeud.setY( centreY + ( positionDansV + 1 ) * espaceY );
			}
		}
	}
	
	/**
	 * Dessine l'ensemble du graphe : arêtes et nœuds.
	 * Active l'antialiasing pour un rendu de meilleure qualité.
	 */
	private void dessinerGraphe()
	{
		this.g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ); // Mettre l'antialiasing pour des trais plus nettes
		for ( Arete arete : lstArete )
		{
			int x1 = arete.getDebut().getX() + arete.getDebut().getLargeur() / 2;
			int y1 = arete.getDebut().getY() + arete.getDebut().getHauteur() / 2;
			int x2 = arete.getFin()  .getX() + arete.getFin  ().getLargeur() / 2;
			int y2 = arete.getFin()  .getY() + arete.getFin  ().getHauteur() / 2;
			
			this.g2.setColor( arete.getColor() );
			
			dessinerFleche  ( x1, y1, x2 - arete.getDebut().getLargeur()/2 , y2, arete.getDebut().getTache().getDuree() );
			
			this.g2.setColor( Color.BLACK );
		}
		
		for ( Noeud noeud : lstNoeud )
		{
			dessinerDivision( noeud );
		}
	}
	
	/**
	 * Dessine une flèche avec un écart au milieu pour afficher la durée de la tâche.
	 * La flèche relie deux points et inclut une tête de flèche orientée.
	 * 
	 * @param x1 Coordonnée X du point de départ
	 * @param y1 Coordonnée Y du point de départ
	 * @param x2 Coordonnée X du point d'arrivée
	 * @param y2 Coordonnée Y du point d'arrivée
	 * @param duree Durée de la tâche à afficher au milieu de la flèche
	 */
	private void dessinerFleche( int x1, int y1, int x2, int y2, int duree )
	{
	
		this.g2.setStroke( new BasicStroke( 3 ) );
		
		int    dx             = x2 - x1;
		int    dy             = y2 - y1;
		double angle          = Math.atan2(dy, dx);
		
		int    tailleFleche   = 15;
		int    angleFleche    = 45;
		
		// Calcule la distance totale entre les deux points
		int    ecart          = 30; // pixels
		double distanceTotale = Math.hypot( dx, dy );
		
		// Calcule les points de séparation pour l'écart
		double ratio1         = 0.5 - ( ecart / 2.0 ) / distanceTotale;
		double ratio2         = 0.5 + ( ecart / 2.0 ) / distanceTotale;
		
		int xDebutEcart       = (int) ( x1 + dx * ratio1 );
		int YDebutEcart       = (int) ( y1 + dy * ratio1 );
		int xFinEcart         = (int) ( x1 + dx * ratio2 );
		int yFinEcart         = (int) ( y1 + dy * ratio2 );
		
		// Dessine le premier segment
		this.g2.drawLine( x1, y1, xDebutEcart, YDebutEcart );
		// Draw second segment
		this.g2.drawLine( xFinEcart, yFinEcart, x2, y2     );
		
		// Dessine le deuxieme segment
		this.g2.drawString( String.valueOf( duree ), ( x1 + x2 ) / 2, ( y1 + y2 ) / 2 );
		
		// Dessine la tête de la fleche
		this.g2.drawLine( x2, y2, (int) ( x2 - tailleFleche * Math.cos( angle - Math.toRadians( angleFleche ) ) )  ,
		                          (int) ( y2 - tailleFleche * Math.sin( angle - Math.toRadians( angleFleche ) ) ) );
		                     
		this.g2.drawLine( x2, y2, (int) ( x2 - tailleFleche * Math.cos( angle + Math.toRadians( angleFleche ) ) )  ,
		                          (int) ( y2 - tailleFleche * Math.sin( angle + Math.toRadians( angleFleche ) ) ) );
	}
	
	/**
	 * Dessine un nœud complet avec sa division en sections pour afficher
	 * le nom de la tâche et les dates au plus tôt/au plus tard.
	 * 
	 * @param noeud Le nœud à dessiner
	 */
	private void dessinerDivision( Noeud noeud )
	{
		int x       = noeud.getX()      ;
		int y       = noeud.getY()      ;
		int largeur = noeud.getLargeur();
		int hauteur = noeud.getHauteur();
		int milieuH = hauteur / 2       ;
		int milieuL = largeur / 2       ;
		
		Font fontOriginal;
		
		Tache t      = noeud.getTache() ;
		
		fontOriginal = this.g2.getFont();
		
		this.g2.setColor( Color.WHITE            );
		this.g2.fillRect( x, y, largeur, milieuH );
		
		this.g2.setColor( Color.BLACK            );
		this.g2.drawRect( x, y, largeur, milieuH );
		
		this.g2.setFont ( new Font( fontOriginal.getName(), Font.BOLD, 15 ) );
		
		FontMetrics fm        = this.g2.getFontMetrics();
		String      nom       = t.getNom()              ;
		int         tailleMax = largeur - 10            ;
		
		while ( fm.stringWidth( nom ) > tailleMax && nom.length() > 1) // Tant que la nom est trop long et qu'il reste des caractères 
		{
			nom = nom.substring( 0, nom.length() - 1 );    // On enlève le dernier caractère
		}
		
		if ( fm.stringWidth( t.getNom() ) > tailleMax ) // Si le nom est toujours trop long après avoir enlevé des caractères
		{
			nom = nom.substring( 0, Math.max( 0, nom.length() - 1) ) + "..."; // On ajoute des points de suspension
		}
		
		int tailleNom = fm.stringWidth( nom );
		
		this.g2.drawString( nom, x + ( largeur - tailleNom ) / 2, y + fm.getAscent() + 10);
		this.g2.setFont( fontOriginal );
		
		
		dessinerPetiteBoite( noeud, true  );
		
		dessinerPetiteBoite( noeud, false );
		
		if ( noeud.getTache().getRang() <= this.rangTot )
		{
			this.g2.setColor( Color.GREEN );
			if ( this.date )
			{
				this.g2.drawString( String.valueOf( noeud.getTache().getDateReelle( noeud.getTache().getDateAuPlusTot() ) ),
				                    x + 10 , y + milieuH + 20);
			}
			else
			{
				this.g2.drawString( String.valueOf( noeud.getTache().getDateAuPlusTot() ), x + 10 , y + milieuH + 20 );
			}
			
		}
		if ( noeud.getTache().getRang() >= this.rangTard )
		{
			this.g2.setColor( Color.RED );
			
			if ( this.date )
			{
				this.g2.drawString( String.valueOf( noeud.getTache().getDateReelle( noeud.getTache().getDateAuPlusTard() ) ),
				                    x + milieuL + 10, y + milieuH + 20);
			}
			else
			{
				this.g2.drawString( String.valueOf(noeud.getTache().getDateAuPlusTard()), x + milieuL + 10, y + milieuH + 20 );
			}
		}
		

	}

	/**
	 * Dessine une petite boîte dans la partie inférieure du nœud pour afficher les dates.
	 * La boîte de gauche (verte) affiche la date au plus tôt,
	 * La boîte de droite (rouge) affiche la date au plus tard.
	 * 
	 * @param noeud Le nœud pour lequel dessiner la boîte
	 * @param sens true pour la date au plus tôt (gauche), false pour au plus tard (droite)
	 */
	private void dessinerPetiteBoite( Noeud noeud, boolean sens )
	{
		int x       = noeud.getX()      ;
		int y       = noeud.getY()      ;
		int largeur = noeud.getLargeur();
		int hauteur = noeud.getHauteur();
		int milieuH = hauteur / 2       ;
		int milieuL = largeur / 2       ;
		
		int rectX   = sens ? x : x + milieuL;
		int rectY   = y + milieuH;
		int rectW   = milieuL;
		int rectH   = milieuH;
		
		this.g2.setColor   ( Color.WHITE                    );
		this.g2.fillRect   ( rectX, rectY, rectW, rectH     );
		this.g2.setColor   ( Color.BLACK                    );
		this.g2.drawRect   ( rectX, rectY, rectW, rectH     );
		this.g2.setColor   ( sens ? Color.GREEN : Color.RED );
	}
	
	/*========================================*/
	/*           CLASSE INTERNE               */
	/*========================================*/
	
	/**
	 * Classe interne pour gérer les événements de souris sur le graphe.
	 * Permet la sélection et le déplacement des nœuds par glisser-déposer.
	 */
	private class GereSouris extends MouseAdapter
	{
		/**
		 * Gère l'événement de pression du bouton de la souris.
		 * Détecte si un nœud est cliqué et prépare le déplacement.
		 * 
		 * @param e L'événement de souris
		 */
		public void mousePressed(MouseEvent e)
		{
			Point mousePoint = e.getPoint();
			for ( Noeud noeud : lstNoeud )
			{
				if ( noeud.contient( (int) mousePoint.getX(), (int) mousePoint.getY() ) )
				{
					noeudSelectionne = noeud;
					decalageSouris   = new Point( e.getX() - noeud.getX(), e.getY() - noeud.getY() );
					break;
				}
			}
		}
		
		/**
		 * Gère l'événement de relâchement du bouton de la souris.
		 * Termine l'opération de déplacement.
		 * 
		 * @param e L'événement de souris
		 */
		public void mouseReleased(MouseEvent e)
		{
			noeudSelectionne = null;
			decalageSouris   = null;
		}
		
		/**
		 * Gère l'événement de glissement de la souris.
		 * Met à jour la position du nœud sélectionné pendant le déplacement.
		 * 
		 * @param e L'événement de souris
		 */
		public void mouseDragged(MouseEvent e)
		{
			if ( PanelGraphe.this.noeudSelectionne != null && decalageSouris != null )
			{
				noeudSelectionne.setX( (int) (e.getX() - decalageSouris.getX() ) );
				noeudSelectionne.setY( (int) (e.getY() - decalageSouris.getY() ) );
				
				PanelGraphe.this.frame.maj();
			}
		}
		
		/**
		 * Gère l'événement de clic de souris.
		 * Sélectionne un nœud et met à jour l'affichage de la tâche actuelle.
		 * 
		 * @param e L'événement de souris
		 */
		public void mouseClicked (MouseEvent e)
		{
			Point mousePoint = e.getPoint();
			boolean noeudTrouve = false;
			
			for ( Noeud noeud : lstNoeud )
			{
				if ( noeud.contient( (int) mousePoint.getX(), (int) mousePoint.getY() ) )
				{
					PanelGraphe.this.noeudSelectionne = noeud;
					
					PanelGraphe.this.frame.updateTacheActuelle( noeudSelectionne.getTache() );
					
					noeudTrouve = true;
					
					break;
				}
			}
			
			if ( ! noeudTrouve ) { PanelGraphe.this.frame.updateTacheActuelle( null ); }
		}
	}
}
