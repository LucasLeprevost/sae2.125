package source.metier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import source.Controleur;


/**
 * Classe Métier qui initialise un graphe et ses taches
 */
public class MPM
{
	public static  Date          dateDebut = null ;
	
	private Controleur           ctrl             ;
	
	private List<Tache>          lstTache         ;
	
	private List< List<Tache> >  lstNiveau        ;
	private List<Tache>          lstTacheTot      ;
	private List<Tache>          lstTacheTard     ;
	
	private List<CheminCritique> lstCheminCritique; //cheminCritique
	
	private int nbNiveau = 0;
	
	/*========================================*/
	/*            CONSTRUCTEUR                */
	/*========================================*/
	/**
	 * Constructeur de la classe MPM
	 * @param ctrl le contrôleur associé
	 */
	public MPM( Controleur ctrl )
	{
		this.ctrl = ctrl;
		this.lstCheminCritique = new ArrayList<CheminCritique>();
		this.lstTache = new ArrayList<Tache>();
	}
	
	/*========================================*/
	/*            MODIFICATEURS               */
	/*========================================*/
	
	/**
	 * Définit la date de début du projet
	 * @param date la date à définir
	 * @param typeDate true pour date de début
	 */
	public void setDateProjet ( Date date , boolean typeDate)
	{
		if ( typeDate ) { MPM.dateDebut = date; }		
	}
	
	/**
	 * Ajoute une nouvelle tâche au graphe avec ses dépendances
	 * @param tache la tâche à ajouter
	 * @param lstTachePrc liste des tâches précédentes
	 * @param tacheSvt tâche suivante
	 * @return true si l'ajout a réussi
	 */
	public boolean ajouterTache( Tache tache, List<Tache> lstTachePrc, Tache tacheSvt)
	{
		if ( tache == null )
		{
			return false;
		}
		
		this.lstTache.add( tache ); // Ajoute la tâche à la liste des tâches
		
		if ( lstTachePrc != null )
		{
			for ( Tache tachePrc : lstTachePrc )
			{
				tache.ajouterTachePrc( tachePrc );
				tachePrc.ajouterTacheSvt( tache );
			}
		}
		
		if ( tacheSvt != null )
		{
			tache.ajouterTacheSvt( tacheSvt );
			tacheSvt.ajouterTachePrc( tache );
		}
		
		this.calculerDates();
		
		return true;
	}
	
	/**
	 * Supprime une tâche du graphe en recréant les liaisons nécessaires
	 * @param tacheSuppr la tâche à supprimer
	 */
	public void supprimer ( Tache tacheSuppr )
	{
		// Partie 1 : Gérer les liaisons avant de supprimer la tâche
		
		Tache debut = this.getDebut();
		Tache fin 	= this.getFin();
		
		
		List<Tache> tachesPrecedentes   = new ArrayList<>();
		List<Tache> tachesSuivantes     = new ArrayList<>();
		
		// Copie des prc et svt de la tache a supprimer dans des list
		for ( int cpt = 0; cpt < tacheSuppr.getNbTachesPrc(); cpt++ )
		{
			tachesPrecedentes.add( tacheSuppr.getTachesPrc( cpt ) );
		}
		
		for ( int cpt = 0; cpt < tacheSuppr.getNbTachesSvt(); cpt++ )
		{
			tachesSuivantes  .add( tacheSuppr.getTachesSvt( cpt ) );
		}
		
		// Supprimer la tâche des listes de précédents et suivants de toutes les autres tâches
		for ( Tache t : this.lstTache ) 
		{
			t.getLstTachesPrc().remove(tacheSuppr);
			t.getLstTachesSvt().remove(tacheSuppr);
		}
		
		// Reconnecter les tâches si besoin
		for ( Tache tacheSuivante : tachesSuivantes )
		{
			// Si la tâche suivante n'a plus de précédents, la relier au début
			if (  tacheSuivante.getNbTachesPrc() == 0  && 
			    ! tacheSuivante.equals( this.getFin() ) ) 
			{
				tacheSuivante.ajouterTachePrc( debut );
				debut.ajouterTacheSvt( tacheSuivante );
			}
		}
		
		for ( Tache tachePrecedente : tachesPrecedentes ) 
		{
			// Si la tâche précédente n'a plus de suivants, la relier à la fin
			if (  tachePrecedente.getNbTachesSvt() == 0    &&
				! tachePrecedente.equals( this.getDebut() ) ) 
			{
				tachePrecedente.ajouterTacheSvt( fin );
				fin.ajouterTachePrc( tachePrecedente );
			}
		}
		
		//partie 2
		
		this.lstTache.remove( tacheSuppr ); // on remove la tache de lstTache
		
		if ( this.lstCheminCritique != null )      // on remove la tache des / du chemins Critiques
		{
			 // Créer une copie pour éviter les problèmes de modification concurrente
			List<CheminCritique> copieChemins = new ArrayList<>( this.lstCheminCritique );
			
			for ( CheminCritique chemin : copieChemins ) 
			{
				chemin.getLstTache().remove( tacheSuppr );  // remove la tâche du chemin
				
				// Si le chemin ctritique est vide, on le remove 
				if ( chemin.getLstTache().isEmpty() )
				{
					this.lstCheminCritique.remove( chemin );
				}
			}
		}
	}
	
	/**
	 * Remet à zéro toutes les données du MPM
	 */
	public void reset()
	{
		this.lstTache          = new ArrayList<>();
		this.lstNiveau         = null;
		this.lstTacheTot       = null;
		this.lstTacheTard      = null;
		this.lstCheminCritique = new ArrayList<>();
		this.nbNiveau          = 0;
	}
	
	/*========================================*/
	/*            ACCESSEURS                  */
	/*========================================*/
	// --- Accesseurs retournant int ---
	
	/**
	 * @return le nombre total de tâches
	 */
	public int getNbTaches() { return this.lstTache.size(); }
	
	// --- Accesseurs retournant Tache ---
	
	/**
	 * Récupère une tâche à l'index donné
	 * @param ind l'index de la tâche
	 * @return la tâche ou null si index invalide
	 */
	public Tache getTache( int ind )
	{
		if ( ind < 0 && ind >= this.lstTache.size() ) { return null; }
		
		return this.lstTache.get( ind );
	}
	
	/**
	 * Trouve la tâche "Début"
	 * @return la tâche de début ou null si non trouvée
	 */
	public Tache getDebut () 
	{
		for ( Tache t : this.lstTache ) 
		{
			if ( t.getNom().equals( "Début" ) ) { return t; }
		}
		
		return null;
	}
	
	/**
	 * Trouve la tâche "Fin"
	 * @return la tâche de fin ou null si non trouvée
	 */
	public Tache getFin () 
	{
		for (Tache t : this.lstTache) 
		{
			if ( t.getNom().equals( "Fin" ) ) { return t; }
		}
		
		return null;
	}
	
	// --- Accesseurs retournant List ---
	
	/**
	 * @return la liste des chemins critiques
	 */
	public List<CheminCritique> getLstCheminCritique()
	{
		return this.lstCheminCritique; 
	}
	
	/**
	 * @return la liste complète des tâches
	 */
	public List<Tache> getLstTache() { return this.lstTache; }
	
	/*========================================*/
	/*         AUTRES MÉTHODES - void         */
	/*========================================*/
	
	/**
	 * Charge les tâches depuis un fichier et crée le graphe MPM
	 * @param data chemin vers le fichier de données
	 */
	public void initMPM( String data )
	{
		List<Tache>    lstTachesLues = new ArrayList<>(); // Toutes les tâches (Début et Fin incluses)
		List<String[]> lstPrcLues    = new ArrayList<>(); // Antécédents par nom
		
		// Ajoute "Début"
		Tache debut   = new Tache( "Début", 0, 0 );
		lstTachesLues .add        ( debut        );
		lstPrcLues    .add        ( null         );
		
		try ( BufferedReader br = new BufferedReader( new FileReader( data ) ) )
		{
			String lig;
			while ( ( lig = br.readLine() ) != null )
			{
				String[] parts = lig.split( "\\|"  );
				
				String nom  = parts[0];
				int duree   = Integer.parseInt( parts[1] );
				
				Tache tache = new Tache( nom, duree      );
				lstTachesLues.add      ( tache           );
				
				if ( parts.length == 3 && ! parts[2].isEmpty() )
				{
					String[] partsTachesPrc = parts[2].split( "," );
					lstPrcLues.add( partsTachesPrc );
				}
				else
				{
					lstPrcLues.add( null ); // Aucune dépendance : dépendra de "Début"
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		
		// Ajoute "Fin"
		Tache fin    = new Tache( "Fin", 0 );
		lstTachesLues.add( fin    );
		lstPrcLues   .add( null ); // sera reliée plus tard aux tâches sans successeur
		
		// Ajoute toutes les tâches au MPM
		this.lstTache.addAll( lstTachesLues );
		
		// Établit les dépendances en fonction des noms
		for ( int cpt = 0; cpt < this.lstTache.size(); cpt++ )
		{
			Tache    tache   = this.lstTache.get( cpt );
			String[] nomsAnt = lstPrcLues   .get( cpt );
			
			if ( tache.getNom().equals( "Début" ) || tache.getNom().equals( "Fin" ) ) { continue; }
			
			if ( nomsAnt != null )
			{
				for ( String nomTachePrc : nomsAnt )
				{
					for ( Tache t : this.lstTache )
					{
						if ( nomTachePrc.equals( t.getNom() ) )
						{
							tache.ajouterTachePrc( t );
							t.ajouterTacheSvt( tache );
							break;
						}
					}
				}
			}
			else
			{
				tache.ajouterTachePrc( debut );
				debut.ajouterTacheSvt( tache );
			}
		}
		
		// Relie toutes les tâches sans successeur à "Fin"
		for ( Tache t : lstTachesLues )
		{
			if ( t.getNbTachesSvt() == 0 && ! t.getNom().equals( "Fin" ) )
			{
				t  .ajouterTacheSvt( fin );
				fin.ajouterTachePrc( t   );
			}
		}
		
		this.calculerDates(); 
	}
	
	/**
	 * Calcule les dates au plus tôt et au plus tard pour toutes les tâches
	 */
	public void calculerDates()
	{
		// Réinitialisation des dates
		for (Tache t : this.lstTache) 
		{
			t.setDateAuPlusTot ( -1                );
			t.setDateAuPlusTard( Integer.MAX_VALUE );
			t.setRang          ( null              ); // Important pour recalculer le rang
		}
		
		// Trie les tâches par rang
		this.lstTache.sort( Comparator.comparingInt( Tache::calculerRang ) );
		
		// Calcul au plus tôt (ordre croissant)
		for ( Tache t : this.lstTache ) 
		{
			t.dateAuPlusTot();
		}
		
		// Calcul au plus tard (ordre décroissant)
		for ( int cpt = this.lstTache.size() - 1; cpt >= 0; cpt-- ) 
		{
			this.lstTache.get( cpt ).dateAuPlusTard();
		}
	}
	
	/**
	 * Lance la construction de tous les chemins critiques
	 */
	public void construireChemin ()
	{
		List<Tache> lstTachedebut = new ArrayList<Tache>();
		
		for ( Tache tache : this.lstTache ) 
		{
			if ( tache.getTachesPrc( 0 ) == null  && tache.getMarge() == 0 )
			{
				lstTachedebut.add( tache ); 
				construireCheminCritique( tache, lstTachedebut ); 
			}
		}
	}
	
	/*========================================*/
	/*        MÉTHODES PRIVÉES - void        */
	/*========================================*/
	
	/**
	 * Construit récursivement un chemin critique à partir d'une tâche
	 * @param tacheActuelle la tâche courante
	 * @param ensTachesCritique le chemin en cours de construction
	 */
	private void construireCheminCritique( Tache tacheActuelle, List<Tache> ensTachesCritique )
	{
		List<Tache> tachesSuivantesDirectes = new ArrayList<>();
		
		// Trouver toutes les tâches suivantes critiques directement connectées
		for ( int cpt = 0; cpt < tacheActuelle.getNbTachesSvt(); cpt++ )
		{
			Tache tmp = tacheActuelle.getTachesSvt( cpt );
			
			if ( tmp.getMarge() == 0 && 
			     tmp.getDateAuPlusTot() - tacheActuelle.getDateAuPlusTot() == tacheActuelle.getDuree() ) // on verif que la marge est bien de 0 et
			{                                                                                            // que les dates correspondent
				tachesSuivantesDirectes.add( tmp );
			}
		}
		
		if ( tachesSuivantesDirectes.isEmpty() ) // Fin du chemin critique donc on crée le chemincritique
		{
			this.lstCheminCritique.add( new CheminCritique( new ArrayList<>(ensTachesCritique) ) );
		} 
		else if ( tachesSuivantesDirectes.size() == 1 )  // Un seul chemin possible donc on continue
		{
			Tache suivante = tachesSuivantesDirectes.get( 0 );

			ensTachesCritique.add   ( suivante                    );
			construireCheminCritique( suivante, ensTachesCritique );
		}
		else // Plusieurs chemins possibles - on fait une branche pour chacun
		{
			for ( Tache tacheSuivante : tachesSuivantesDirectes )
			{
				List<Tache> nouveauChemin = new ArrayList<>( ensTachesCritique            );
				nouveauChemin.add                          ( tacheSuivante                );
				construireCheminCritique                   ( tacheSuivante, nouveauChemin );
			}
		}
	}
	
	// ==================== MÉTHODE TOSTRING ====================
	
	/**
	 * @return représentation textuelle de toutes les tâches
	 */
	public String toString()
	{
		String res = "";
		
		if ( this.lstTache.size() != 0 )
		{
			for ( Tache tache : this.lstTache )
			{
				res += tache.toString() + "\n";
			}
		}
		
		return res;
	}
}
