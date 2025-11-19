package source.metier;

//Permet de stocker une tache et de l'afficher sur le graphe
public class Noeud
{
	private static final int LARGEUR = 100;
	private static final int HAUTEUR = 60;
	
	private Tache t;
	
	private int   x;
	private int   y;
	
	/**
	 * Crée un noeud avec une tâche et sa position
	 * @param t tâche associée au noeud
	 * @param x position horizontale
	 * @param y position verticale
	 */
	public Noeud( Tache t, int x, int y )
	{
		this.t = t;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Modifie la position X
	 * @param x nouvelle position horizontale
	 */
	public void  setX( int x ) { this.x = x          ; }
	
	/**
	 * Modifie la position Y
	 * @param y nouvelle position verticale
	 */
	public void  setY( int y ) { this.y = y          ; }
	
	/**
	 * Récupère la tâche du noeud
	 * @return tâche associée
	 */
	public Tache getTache   () { return this.t       ; }
	
	/**
	 * Récupère la position X
	 * @return position horizontale
	 */
	public int   getX       () { return this.x       ; }
	
	/**
	 * Récupère la position Y
	 * @return position verticale
	 */
	public int   getY       () { return this.y       ; }
	
	/**
	 * Récupère la largeur du noeud
	 * @return largeur en pixels
	 */
	public int   getLargeur () { return Noeud.LARGEUR; }
	
	/**
	 * Récupère la hauteur du noeud
	 * @return hauteur en pixels
	 */
	public int   getHauteur () { return Noeud.HAUTEUR; }
	
	/**
	 * Vérifie si un point est dans le noeud
	 * @param mx coordonnée X du point
	 * @param my coordonnée Y du point
	 * @return true si le point est dans le noeud
	 */
	public boolean contient( int mx, int my )
	{
		return mx >= this.x && mx <= this.x + Noeud.LARGEUR && my >= this.y && my <= this.y + Noeud.HAUTEUR;
	}
}
