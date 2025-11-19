package source.ihm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import source.metier.Tache;

/**
 * Permet de supprimer une tache (hors début et fin) dans le graphe
 */
public class PanelSuppr extends JPanel implements ActionListener
{
	private FrameGraphe frame   ;
	
	private JLabel      lblSuppr;
	private JButton     btnSuppr;
	
	private Tache       tacheAct;
	
	// ========== CONSTRUCTEUR ==========
	
	/**
	 * Crée un panel pour supprimer des tâches
	 * @param frame la fenêtre principale
	 */
	public PanelSuppr ( FrameGraphe frame )
	{
		this.setLayout( new GridLayout( 2, 1 ) );
		
		this.frame = frame;
		
		
		/* ----------------------------- */
		/* Création des Composants       */
		/* ----------------------------- */
		
		JPanel panelBtn = new JPanel();
		
		this.lblSuppr   = new JLabel("Veuillez séléctionner une tache", JLabel.CENTER);
		this.lblSuppr   .setEnabled ( false );
		
		this.btnSuppr   = new JButton    ( "Supprimer"               );
		this.btnSuppr   .setPreferredSize( new Dimension( 210, 120 ) );
		this.btnSuppr   .setBackground   ( new Color( 139, 0, 0 )    );
		this.btnSuppr   .setEnabled      ( false                     );
		
		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */
		
		
		this.add( this.lblSuppr );
		this.add( panelBtn      );
		
		panelBtn.add( this.btnSuppr );
		
		/* ----------------------------- */
		/* Activation des Composants     */
		/* ----------------------------- */
		
		this.btnSuppr.addActionListener( this );
	
	}
	
	// ========== MODIFICATEURS ==========
	
	/**
	 * Définit la tâche actuellement sélectionnée
	 * @param t la tâche à supprimer
	 */
	public void instancierTacheActuelle ( Tache t )
	{
		if ( t != null )
		{
			if ( ! t.equals( this.frame.getDebut() ) &&
			     ! t.equals( this.frame.getFin  () )    )
			{
				this.tacheAct = t;
				this.lblSuppr.setEnabled   ( true                                );
				this.lblSuppr.setText      ( "Vous pouvez appuyer sur le bouton" );
				this.btnSuppr.setEnabled   ( true                                );
				this.btnSuppr.setBackground( new Color( 255, 0, 0 )              );
			}
			else
			{
				this.lblSuppr.setEnabled   ( false                             );
				this.lblSuppr.setText      ( "Veuillez séléctionner une tache" );
				this.btnSuppr.setBackground( ( new Color( 139, 0, 0 ) )        );
				this.btnSuppr.setEnabled   ( false                             );
			}
		}
		else
		{
			this.lblSuppr.setEnabled   ( false                             );
			this.lblSuppr.setText      ( "Veuillez séléctionner une tache" );
			this.btnSuppr.setBackground( ( new Color( 139, 0, 0 ) )        );
			this.btnSuppr.setEnabled   ( false                             );
		}
	
	}
	
	// ========== AUTRES MÉTHODES ==========
	
	/**
	 * Gère la suppression de la tâche sélectionnée
	 * @param e l'événement d'action
	 */
	public void actionPerformed ( ActionEvent e)
	{
		if ( e.getSource() == this.btnSuppr )
		{
			// Récupérer la tâche sélectionnée au moment du clic
			if ( this.tacheAct != null )
			{
				this.frame   .supprimerDonnee( this.tacheAct );
				this.frame   .calculerDates  (               ); // Recalculer les dates avant de supprimer
				
				this.btnSuppr.setBackground  ( ( new Color( 139, 0, 0 ) ) );
				
				this.frame   .resetFrame();    // Rafraîchir l'affichage
			}
			
		}
	}
}
