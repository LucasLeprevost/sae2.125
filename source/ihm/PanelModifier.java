package source.ihm           ;

import java.awt.BorderLayout        ;
import java.awt.Color    ;
import java.awt.Dimension        ;
import java.awt.FlowLayout ;
import java.awt.GridLayout   ;
import java.awt.event.*   ;
import java.util.List      ;
import javax.swing.*         ;
import source.metier.Tache;

/**
 * Permet de modifier une tache (hors début et fin) dans le graphe
 */
public class PanelModifier extends JPanel implements ActionListener
{
	private FrameGraphe              frame          ;
	
	private JTextField               txtNom         ;
	private JTextField               txtDuree       ;
	
	private DefaultListModel<String> lstModelPrc    ;
	private JList<String>            lstActTachesPrc;
	
	private DefaultListModel<String> lstModelSvt    ;
	private JList<String>            lstActTachesSvt;
	
	
	private JButton                  btnModifier    ;
	
	private Tache                    tacheActuelle  ;
	
	// ========== CONSTRUCTEUR ==========
	
	/**
	 * Crée un panel pour modifier les tâches
	 * @param frame la fenêtre principale
	 */
	public PanelModifier ( FrameGraphe frame )
	{
		this.setLayout    ( new BorderLayout()          );
		this.setBackground( new Color ( 206, 206, 206 ) );
		
		/* ----------------------------- */
		/* Création des Composants       */
		/* ----------------------------- */
		
		JPanel panelInfo;
		
		JPanel panelNom      ;
		JPanel panelDuree    ;
		JPanel panelPrc      ;
		JPanel panelSvt      ;
		
		JScrollPane scrollPrc;
		JScrollPane scrollSvt;
		
		
		this.frame        = frame;
		
		this.txtNom          = new JTextField ( 20         );
		this.txtNom          .setEnabled      ( false      );
		
		this.txtDuree        = new JTextField ( 20         );
		this.txtDuree        .setEnabled      ( false      );
		
		this.lstModelPrc     = new DefaultListModel<>();
		this.lstActTachesPrc = new JList<>    ( this.lstModelPrc   );
		
		this.lstModelSvt     = new DefaultListModel<>();
		this.lstActTachesSvt = new JList<>    ( this.lstModelSvt   );
		
		
		this.btnModifier     = new JButton    ( "Modifier la tache" );
		this.btnModifier     .setEnabled      ( false               );
		
		this.tacheActuelle   = null;
		
		panelInfo   = new JPanel( new GridLayout( 4, 1 ) );
		
		panelNom    = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		panelDuree  = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		panelPrc    = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		panelSvt    = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		
		
		scrollPrc   = new JScrollPane( this.lstActTachesPrc      );
		scrollPrc   .setPreferredSize( new Dimension ( 225, 20 ) );
		scrollSvt   = new JScrollPane( this.lstActTachesSvt      );
		scrollSvt   .setPreferredSize( new Dimension ( 225, 20 ) );
		
		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */
		
		panelNom  .add( new JLabel( String.format( "%-47s", "Nom de la tache :" ) )               );
		panelNom  .add( this.txtNom                                                               );
		
		panelDuree.add( new JLabel( String.format( "%-46s", "Durée de la tache :" ) )             );
		panelDuree.add( this.txtDuree                                                             );
		
		panelPrc  .add( new JLabel( "Liste de ses taches précédentes :" )                         );
		panelPrc  .add( scrollPrc                                                                 );
		
		panelSvt  .add( new JLabel( String.format( "%-36s", "Liste de ses taches suivantes :" ) ) );
		panelSvt  .add( scrollSvt                                                                 );
		
		panelInfo .add( panelNom   );
		panelInfo .add( panelDuree );
		panelInfo .add( panelPrc   );
		panelInfo .add( panelSvt   );
		
		this.add( panelInfo       , BorderLayout.CENTER );
		this.add( this.btnModifier, BorderLayout.EAST   );
		
		/* ----------------------------- */
		/* Activation des Composants     */
		/* ----------------------------- */
		
		this.btnModifier.addActionListener ( this );
	}
	
	// ========== MODIFICATEURS ==========
	
	/**
	 * Initialise les champs avec les données de la tâche sélectionnée
	 * @param t la tâche à modifier
	 */
	public void instancierTacheActuelle ( Tache t )
	{
		if ( t != null )
		{
			this.tacheActuelle = t;
			
			this.txtNom  .setText         ( this.tacheActuelle.getNom()                     );
			this.txtDuree.setText         ( String.valueOf( this.tacheActuelle.getDuree() ) );
			
			if ( t.equals( this.frame.getDebut() ) || t.equals( this.frame.getFin() ) )
			{
				this.txtNom     .setEnabled ( false );
				this.txtDuree   .setEnabled ( false );
				
				this.btnModifier.setEnabled ( false );
			}
			else
			{
				this.txtNom     .setEnabled      ( true        );
				this.txtNom     .setBackground   ( Color.WHITE );
				
				this.txtDuree   .setEnabled      ( true        );
				this.txtDuree   .setBackground   ( Color.WHITE );
				
				this.btnModifier.setEnabled      ( true        );
			}
			
			this.ajouterListe( this.lstModelPrc, this.tacheActuelle.getLstTachesPrc(), "précédente" );
			this.lstActTachesPrc.setEnabled  ( true                                                 );
			
			this.ajouterListe( this.lstModelSvt, this.tacheActuelle.getLstTachesSvt(), "suivante"   );
			this.lstActTachesSvt.setEnabled  ( true                                                 );
			
		}
		else
		{
			this.supprimerDonnee();
		}
	}
	
	/**
	 * Vide tous les champs et désactive les contrôles
	 */
	public void supprimerDonnee()
	{
		this.txtNom         .setText   ( ""    );
		this.txtNom         .setEnabled( false );
		
		this.txtDuree       .setText   ( ""    );
		this.txtDuree       .setEnabled( false );
		
		this.lstModelPrc    .clear();
		this.lstActTachesPrc.setEnabled( false );
		
		this.lstModelSvt    .clear();
		this.lstActTachesSvt.setEnabled( false );
		
		this.btnModifier    .setEnabled( false );
	}
	
	// ========== AUTRES MÉTHODES ==========
	
	/**
	 * Remplit une liste avec les noms des tâches
	 * @param lstAct le modèle de liste à remplir
	 * @param lstTaches la liste des tâches
	 * @param typeTaches le type de tâches (précédente/suivante)
	 */
	private void ajouterListe ( DefaultListModel<String> lstAct, List<Tache> lstTaches, String typeTaches )
	{
		lstAct.clear();
		
		if ( lstTaches.size() == 0 )
		{
			lstAct.addElement( "Aucune tache " + typeTaches );
		}
		else
		{
			for ( Tache t : lstTaches )
			{
				lstAct.addElement( t.getNom() );
			}
		}
	}
	
	/**
	 * Gère la modification de la tâche
	 * @param e l'événement d'action
	 */
	public void actionPerformed ( ActionEvent e )
	{
		if ( e.getSource() == this.btnModifier )
		{
			if ( this.tacheActuelle != null )
			{
				if ( this.txtDuree.getText().matches ( "[0-9]+" ) && ! this.txtNom.getText().equals( "" ) )
				{
					this.tacheActuelle.setNom  ( this.txtNom.getText()                       );
					this.tacheActuelle.setDuree( Integer.valueOf( this.txtDuree.getText() )  );
					
					this.frame.calculerDates();
					
					this.supprimerDonnee();
					
					this.frame.maj();
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "Veuillez entrez un entier dans la durée et un String non vide pour le nom",
					"Graphe MPM", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
