package source.metier;

import java.awt.Color;

//Permet de relier 2 noeuds entre eux
public class Arete
{
	private Noeud debut  ;
	private Noeud fin    ;
	private Color couleur;
	
	/**
	 * Crée une arête entre deux noeuds
	 * @param debut noeud de départ
	 * @param fin noeud d'arrivée
	 */
	public Arete( Noeud debut, Noeud fin )
	{
		this.debut   = debut     ;
		this.fin     = fin       ;
		this.couleur = Color.BLUE;
	}
	
	/**
	 * Modifie la couleur de l'arête
	 * @param couleur nouvelle couleur
	 */
	public void setColor( Color couleur )
	{
		if ( couleur != null ) { this.couleur = couleur; }
	}
	
	/**
	 * Modifie le noeud de début
	 * @param debut nouveau noeud de début
	 */
	public void setDebut ( Noeud debut ) { this.debut = debut; }
	
	/**
	 * Modifie le noeud de fin
	 * @param fin nouveau noeud de fin
	 */
	public void setFin   ( Noeud fin   ) { this.fin = fin    ; }
	
	/**
	 * Récupère la couleur de l'arête
	 * @return couleur actuelle
	 */
	public Color getColor()              { return this.couleur; }
	
	/**
	 * Récupère le noeud de début
	 * @return noeud de début
	 */
	public Noeud getDebut()              { return this.debut  ; }
	
	/**
	 * Récupère le noeud de fin
	 * @return noeud de fin
	 */
	public Noeud getFin  ()               { return this.fin    ; }
}