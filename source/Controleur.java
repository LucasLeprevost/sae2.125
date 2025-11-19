/* Création d'une appli pour des graphes MPM
 * SAE S2.01/S2.02/S2.05
 * @author LEHEURTEUR Maxence A1
 * @author HAZET Alex         A1
 * @author LEPREVOST Lucas    B2
 * @author CONSTANTIN Alexis  B2
 * @version du 12/06/2025
 */

package source;

import java.util.List;
import java.util.Date;

import source.ihm.FrameGraphe;
import source.ihm.FrameStart;
import source.metier.CheminCritique;
import source.metier.MPM;
import source.metier.Tache;


//La classe contrôleur fait la passerelle entre le métier (mpm) et la vue (frameGraphe)
public class Controleur
{
	private MPM         mpm;
	private FrameGraphe frameGraphe;
	
	public Controleur()
	{
		this.mpm         = new MPM        ( this );
		this.frameGraphe = new FrameGraphe( this );
	}
	
	public Tache                getTache            ( int index ) { return this.mpm.getTache(index)          ; }
	public List<CheminCritique> getLstCheminCritique()            { return this.mpm.getLstCheminCritique()   ; }
	public List<Tache>          getLstTache         ()            { return this.mpm.getLstTache()            ; }
	public int                  getNbTaches         ()            { return this.mpm.getNbTaches()            ; }
	public Tache                getDebut            ()            { return this.mpm.getDebut()               ; }
	public Tache                getFin              ()            { return this.mpm.getFin()                 ; }
	
	public void setDateProjet ( Date date, boolean typeDate  )    { this.mpm.setDateProjet( date, typeDate ) ; }
	
	public void    construireChemin                 ()            { this.mpm.construireChemin()              ; }
	
	public void    initMPM ( String data )                        { this.mpm.initMPM( data )                 ; }

	public void supprimer (Tache tache) 
	{
		this.mpm.supprimer(tache);
	}
	
	public void    reset                            ()            { this.mpm.reset()                         ; }

	public void calculerDates ()
	{
		this.mpm.calculerDates();
	}

	public boolean ajouterTache ( Tache tache , List<Tache> lstPrc, Tache tacheSvt )
	{
		return this.mpm.ajouterTache( tache, lstPrc, tacheSvt );
	}
	
	
	public static void main ( String[] args ) { new Controleur()  ; }
}
