package source.metier;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static source.metier.MPM.*;

//Classe qui permet de créer une tache MPM
public class Tache
{
	
	private String      nomTache      ;
	private int         dureeTache    ;
	
	private List<Tache> lstTachesPrc  ;
	private List<Tache> lstTachesSvt  ;
	
	private int         dateAuPlusTot ;
	private int         dateAuPlusTard;

	private Integer     rang          ;
	
	/**
	 * Crée une tâche avec nom et durée
	 * @param nom nom de la tâche
	 * @param dureeTache durée en jours
	 */
	public Tache ( String nom, int dureeTache )
	{
		this( nom, dureeTache, -1 );
	}
	
	/**
	 * Crée une tâche avec nom, durée et date
	 * @param nom nom de la tâche
	 * @param dureeTache durée en jours
	 * @param dateTot date au plus tôt
	 */
	public Tache ( String nom, int dureeTache, int dateTot )
	{
		this.nomTache       = nom;
		this.dureeTache     = dureeTache;
		
		this.lstTachesPrc   = new ArrayList<Tache>();
		this.lstTachesSvt   = new ArrayList<Tache>();
		
		this.dateAuPlusTot  = dateTot;
		this.dateAuPlusTard = dateTot;
		
		this.rang           = null;
	}
	
	/**
	 * Modifie le nom de la tâche
	 * @param s nouveau nom
	 */
	public void setNom   ( String s )
	{
		if ( ! s.equals( "" ) ) { this.nomTache = s; }
	}
	
	/**
	 * Modifie la durée de la tâche
	 * @param duree nouvelle durée
	 */
	public void setDuree ( int duree )
	{
		if ( duree >= 0 ) { this.dureeTache = duree; }
	}
	
	/**
	 * Modifie la date au plus tôt
	 * @param date nouvelle date au plus tôt
	 */
	public void setDateAuPlusTot ( int date )
	{
		if ( date >= 0 || date > this.dateAuPlusTot )
		{
			this.dateAuPlusTot = date;
		}
	}
	
	/**
	 * Modifie la date au plus tard
	 * @param date nouvelle date au plus tard
	 */
	public void setDateAuPlusTard( int date )
	{
		if ( date >= this.dateAuPlusTot && date < this.dateAuPlusTard )
		{
			this.dateAuPlusTard = date;
		}
	}
	
	/**
	 * Modifie le rang de la tâche
	 * @param rang nouveau rang
	 */
	public void setRang ( Integer rang )
	{
		if ( rang != null && rang >= 0 )
		{
			this.rang = rang;
		}
		else
		{
			this.rang = null;
		}
	}
	
	/**
	 * Récupère le nom de la tâche
	 * @return nom de la tâche
	 */
	public String      getNom           () { return this.nomTache                           ; }
	
	/**
	 * Récupère la durée de la tâche
	 * @return durée en jours
	 */
	public int         getDuree         () { return this.dureeTache                         ; }
	
	/**
	 * Récupère la date au plus tôt
	 * @return date au plus tôt
	 */
	public int         getDateAuPlusTot () { return this.dateAuPlusTot                      ; }
	
	/**
	 * Récupère la date au plus tard
	 * @return date au plus tard
	 */
	public int         getDateAuPlusTard() { return this.dateAuPlusTard                     ; }
	
	/**
	 * Calcule la marge de la tâche
	 * @return marge en jours
	 */
	public int         getMarge         () { return this.dateAuPlusTard - this.dateAuPlusTot; }
	
	/**
	 * Récupère le nombre de tâches précédentes
	 * @return nombre de tâches précédentes
	 */
	public int         getNbTachesPrc   () { return this.lstTachesPrc.size()                ; }
	
	/**
	 * Récupère le nombre de tâches suivantes
	 * @return nombre de tâches suivantes
	 */
	public int         getNbTachesSvt   () { return this.lstTachesSvt.size()                ; }

	/**
	 * Récupère le rang de la tâche
	 * @return rang de la tâche
	 */
	public Integer     getRang          () { return this.rang                               ; }
	
	/**
	 * Récupère la liste des tâches suivantes
	 * @return liste des tâches suivantes
	 */
	public List<Tache> getLstTachesSvt  () { return this.lstTachesSvt                       ; }
	
	/**
	 * Récupère la liste des tâches précédentes
	 * @return liste des tâches précédentes
	 */
	public List<Tache> getLstTachesPrc  () { return this.lstTachesPrc                       ; }
	
	/**
	 * Récupère une tâche précédente par index
	 * @param ind index de la tâche
	 * @return tâche précédente ou null
	 */
	public Tache       getTachesPrc     ( int ind )
	{
		if   ( ind >= 0 && ind < this.lstTachesPrc.size() ) { return this.lstTachesPrc.get( ind ); }
		else                                                { return null                        ; }
	}
	
	/**
	 * Récupère une tâche suivante par index
	 * @param ind index de la tâche
	 * @return tâche suivante ou null
	 */
	public Tache  getTachesSvt     ( int ind )
	{
		if   ( ind >= 0 && ind < this.lstTachesSvt.size() ) { return this.lstTachesSvt.get( ind ); }
		else                                                { return null                        ; }
	}

	/**
	 * Convertit une date en format JJ/MM
	 * @param date date à convertir
	 * @return date formatée
	 */
	public String getDateReelle( int date )
	{
		GregorianCalendar calendar = new GregorianCalendar();

		if ( MPM.dateDebut != null )
		{
			calendar.setTime( MPM.dateDebut );
		}
		
		calendar.add( GregorianCalendar.DAY_OF_MONTH, date );
		
		int jour = calendar.get( GregorianCalendar.DAY_OF_MONTH )    ;
		int mois = calendar.get( GregorianCalendar.MONTH        ) + 1; 
		
		return String.format( "%02d/%02d", jour, mois );
	}
	
	/**
	 * Ajoute une tâche précédente
	 * @param t tâche à ajouter
	 */
	public void ajouterTachePrc( Tache t )
	{
		if ( t != null )
		{
			this.lstTachesPrc.add( t );
		}
	}
	
	/**
	 * Ajoute une tâche suivante
	 * @param t tâche à ajouter
	 */
	public void ajouterTacheSvt( Tache t )
	{
		if ( t != null )
		{
			this.lstTachesSvt.add( t );
		}
	}
	
	/**
	 * Calcule le rang de la tâche
	 * @return rang calculé
	 */
	public int calculerRang()
	{
		if ( this.getRang() != null ) // Si le rang est déjà calculé, on le retourne
		{
			return this.getRang();
		}
		
		if ( this.getLstTachesPrc().isEmpty() ) // Si la tâche n'a pas de prédécesseurs, son rang est 0
		{
			this.setRang( 0 );
		}
		
		int maxRang = 0;
		for ( Tache prc : this.getLstTachesPrc() )
		{
			maxRang = Math.max( maxRang, prc.calculerRang () ); // On calcule le rang de chaque prédécesseur
		}
		this.setRang( maxRang + 1 );

		return this.getRang();
	}
	
	/**
	 * Calcule la date au plus tôt de la tâche
	 */
	public void dateAuPlusTot()
	{
		if ( this.lstTachesPrc.size() != 0 )
		{
			int maxDate = this.lstTachesPrc.get( 0 ).getDateAuPlusTot() + this.lstTachesPrc.get( 0 ).getDuree();
			this.dateAuPlusTot = maxDate;
			
			for ( int cpt = 1; cpt < this.lstTachesPrc.size(); cpt++ )
			{
				Tache t = this.lstTachesPrc.get( cpt );
				
				if ( t.getDateAuPlusTot() + t.getDuree() > maxDate )
				{
					maxDate = t.getDateAuPlusTot() + t.getDuree();
					
					this.dateAuPlusTot = maxDate;
				}
			}
		}
		else
		{
			this.dateAuPlusTot = 0;
		}
	}
	
	/**
	 * Calcule la date au plus tard de la tâche
	 */
	public void dateAuPlusTard()
	{
		if ( this.lstTachesSvt.size() != 0 )
		{
			int minDate = this.lstTachesSvt.get( 0 ).getDateAuPlusTard() - this.getDuree();
			this.dateAuPlusTard = minDate;
			
			for ( int cpt = 1; cpt < this.lstTachesSvt.size(); cpt++ )
			{
				Tache t = this.lstTachesSvt.get( cpt );
				
				if ( t.getDateAuPlusTard() - this.getDuree() < minDate )
				{
					minDate = t.getDateAuPlusTard() - this.getDuree();
					
					this.dateAuPlusTard = minDate;
				}
			}
			
		}
		else
		{
			this.dateAuPlusTard = this.dateAuPlusTot;
		}
	}
	
	
	
	/**
	 * Représentation textuelle de la tâche
	 * @return description complète de la tâche
	 */
	public String toString()
	{
		String sRet = "";
		
		//Met le pluriel si la durée de la tache est de 2j ou +
		String jourDuree = " jour";
		if   ( this.dureeTache > 1 ) { jourDuree += "s" ; }
		
		sRet += nomTache     +   " : " + String.valueOf( dureeTache ) + jourDuree + "\n  ";
		
		sRet += "Date au plus tôt  : " + this.getDateReelle( dateAuPlusTot  )     + "\n  ";
		sRet += "Date au plus tard : " + this.getDateReelle( dateAuPlusTard )     + "\n  ";
		
		//Met le pluriel si la marge est de 2j ou +
		String margeDuree = " jour";
		if   ( this.getMarge() > 1 ) { margeDuree += "s" ; }
		
		sRet += "marge             : " + this.getMarge() + margeDuree             + "\n  ";
		
		if ( this.lstTachesPrc.size() == 0 )
		{
			sRet += "Cette tache ne possède aucune tache précédente.";
		}
		else
		{
			sRet += "Liste des taches précédentes : \n     ";
			
			for ( int cpt = 0; cpt < this.lstTachesPrc.size(); cpt++ )
			{
				Tache tPrc = this.lstTachesPrc.get( cpt );
				sRet      += tPrc.getNom();
				
				if ( cpt < this.lstTachesPrc.size() - 1 ) { sRet += ", "; }
			}
		}
		
		sRet += "\n  ";
		
		if ( this.lstTachesSvt.size() == 0 )
		{
			sRet += "Cette tache ne possède aucune tache suivante.";
		}
		else
		{
			sRet += "Liste des taches suivantes : \n     ";
			
			for ( int cpt = 0; cpt < this.lstTachesSvt.size(); cpt++ )
			{
				Tache tSvt = this.lstTachesSvt.get( cpt );
				sRet      += tSvt.getNom();
				
				if ( cpt < this.lstTachesSvt.size() - 1 ) { sRet += ", "; }
			}
		}
		
		sRet += "\n";
		
		return sRet;
	}
}