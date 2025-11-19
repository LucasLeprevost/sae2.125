package source.metier;

import java.util.ArrayList;
import java.util.List;

//Permet de calculer le(s) chemin(s) critique(s)
public class CheminCritique
{
	private static int  nbChemin = 0     ;
	
	private List<Tache> ensTachesCritique;
	
	/**
	 * Crée un chemin critique avec une liste de tâches
	 * @param lstTache liste des tâches du chemin critique
	 */	
	public CheminCritique( List<Tache> lstTache )
	{
		CheminCritique.nbChemin++;
		this.ensTachesCritique = new ArrayList<Tache>( lstTache );
	}
	
	/**
	 * Récupère la liste des tâches critiques
	 * @return liste des tâches
	 */
	public List<Tache> getLstTache() { return this.ensTachesCritique ; }
	
	/**
	 * Récupère le nombre de chemins critiques
	 * @return nombre de chemins
	 */
	public static int  getNbChemin() { return CheminCritique.nbChemin; }
	
	/**
	 * Représentation textuelle du chemin critique
	 * @return chaîne représentant le chemin
	 */
	public String toString()
	{
		String sRet;
		
		sRet = "[";
		
		for ( int cpt = 0; cpt < this.ensTachesCritique.size(); cpt++ )
		{
			if ( ! this.ensTachesCritique.get( cpt ).getNom().equals( "Debut" ) &&
			     ! this.ensTachesCritique.get( cpt ).getNom().equals( "Fin"   )    )
			{
				sRet += this.ensTachesCritique.get( cpt ).getNom() ;
			}
			
			if ( cpt != 0 && cpt != this.ensTachesCritique.size() - 1 && cpt != this.ensTachesCritique.size() - 2 )
			{
				sRet += ",";
			}
		}
		
		sRet+= "]";
		
		return sRet;
	}
}
