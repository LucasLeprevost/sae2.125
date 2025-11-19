package source.ihm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import source.Controleur;
import source.metier.Tache;

/**
 * Permet d'ajouter une nouvelle tache dans le graphe
 */
public class PanelAjouter extends JPanel implements ActionListener
{
	private FrameGraphe  frame          ;
	private Controleur   ctrl           ;
	
	private JTextField   txtNom         ;
	private JTextField   txtDuree       ;
	private JList<Tache> lstTachesPrc   ;
	private JList<Tache> lstTachesSvt   ;
	private JButton      btnAjouterTache;
	
	// ========== CONSTRUCTEUR ==========
	
	/**
	 * Crée un panel pour ajouter des tâches
	 * @param frame la fenêtre principale
	 * @param ctrl le contrôleur de l'application
	 */
	public PanelAjouter( FrameGraphe frame, Controleur ctrl )
	{
		this.setOpaque( false );
		
		this.frame = frame;
		this.ctrl  = ctrl ;
		
		this.setLayout( new BorderLayout() );
		
		/* ----------------------------- */
		/* Création des Composants */
		/* ----------------------------- */
		
		JPanel panelSaisie   = new JPanel();
		panelSaisie.setLayout( new BoxLayout( panelSaisie, BoxLayout.Y_AXIS ) );
		
		JPanel panelCentre   = new JPanel( new BorderLayout()     );
		JPanel panelListes   = new JPanel( new GridLayout( 1, 2 ) );
		
		this.txtNom          = new JTextField( 15 );
		this.txtDuree        = new JTextField( 5  );
		
		this.btnAjouterTache = new JButton( "Ajouter Tâche" );
		
		// Listes pour tâches précédentes et suivantes
		this.lstTachesPrc = new JList<>    ( new DefaultListModel<>()                       );
		this.lstTachesPrc .setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		this.lstTachesSvt = new JList<>    ( new DefaultListModel<>()                       );
		this.lstTachesSvt .setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION   );
		
		if ( this.ctrl.getLstTache() != null && !this.ctrl.getLstTache().isEmpty() )
		{
			this.majListeTaches();
		}
		else
		{
			this.lstTachesPrc.setModel( new DefaultListModel<>() );
			this.lstTachesSvt.setModel( new DefaultListModel<>() );
		}
		
		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */
		
		panelSaisie.add( new JLabel   ( "Nom de la tâche :" ) );
		panelSaisie.add( this.txtNom                          );
		panelSaisie.add( new JLabel   ( "Durée :")            );
		panelSaisie.add( this.txtDuree                        );
		
		
		panelListes.add( new JScrollPane( this.lstTachesPrc ) );
		panelListes.add( new JScrollPane( this.lstTachesSvt ) );
		
		
		panelCentre.add( panelSaisie, BorderLayout.NORTH  );
		panelCentre.add( panelListes, BorderLayout.CENTER );
		
		this.add( panelCentre, BorderLayout.CENTER         );
		this.add( this.btnAjouterTache, BorderLayout.SOUTH );
		
		/* ----------------------------- */
		/*   Activation des Composants   */
		/* ----------------------------- */
		
		this.lstTachesPrc.setCellRenderer( new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Tache)
				{
					setText(((Tache) value).getNom());
				}
				return this;
			}
		}
		);
		this.lstTachesSvt.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Tache)
				{
					setText(((Tache) value).getNom());
				}
				return this;
			}
		});
		
		this.btnAjouterTache.addActionListener(this);
		
		this.setVisible(true);
	}
	
	// ========== AUTRES MÉTHODES ==========
	
	/**
	 * Met à jour les listes de tâches disponibles
	 */
	public void majListeTaches()
	{
		DefaultListModel<Tache> modelPrc = new DefaultListModel<>();
		DefaultListModel<Tache> modelSvt = new DefaultListModel<>();
		for (Tache t : this.ctrl.getLstTache())
		{
			if (!t.getNom().equals("Début") && !t.getNom().equals("Fin"))
			{
				modelPrc.addElement(t);
				modelSvt.addElement(t);
			}
		}
		this.lstTachesPrc.setModel(modelPrc);
		this.lstTachesSvt.setModel(modelSvt);
	}
	
	/**
	 * Gère l'ajout d'une nouvelle tâche
	 * @param e l'événement d'action
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.btnAjouterTache)
		{
			String nomTache = this.txtNom  .getText();
			String duree    = this.txtDuree.getText();
			
			if (nomTache.isEmpty() || duree.isEmpty())
			{
				JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try
			{
				int dureeInt = Integer.parseInt(duree);
				if (dureeInt <= 0)
				{
					JOptionPane.showMessageDialog(this, "La durée doit être un nombre positif.", "Erreur", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Tache nouvelleTache = new Tache(nomTache, dureeInt);
				
				//Protetion pour ne pas séléctionner des taches suivantes de degré supérieur au plus haut degré de la liste de tanches précédentes
				Tache tacheSvt = this.lstTachesSvt.getSelectedValue();
				if (tacheSvt != null)
				{
					for (Tache tache : this.lstTachesPrc.getSelectedValuesList())
					{
						if (tache.getRang() >= tacheSvt.getRang())
						{
							JOptionPane.showMessageDialog(this,
									"La tâche suivante doit avoir un rang supérieur à la tâche précédente.", "Erreur",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
				
				List<Tache> tacheDebut = new ArrayList<>();
				tacheDebut.add(this.ctrl.getDebut());
				// Si on a pas de taches précédentes sélectionnées, la tache précédente est la tâche "Début"
				List<Tache> tachesPrc = this.lstTachesPrc.getSelectedValuesList();
				            tacheSvt  = this.lstTachesSvt.getSelectedValue();
				
				List<Tache> tachesPrecedentes = tachesPrc.isEmpty() ? List.of(this.ctrl.getDebut()) : tachesPrc;
				Tache tacheSuivante           = (tacheSvt == null)  ? this.ctrl.getFin()            : tacheSvt;
				
				// Cas particulier : rien de sélectionné dans les deux listes
				if (tachesPrc.isEmpty() && tacheSvt == null)
				{
					this.ctrl.ajouterTache(nouvelleTache, List.of(this.ctrl.getDebut()), this.ctrl.getFin());
				}
				else
				{
					this.ctrl.ajouterTache(nouvelleTache, tachesPrecedentes, tacheSuivante);
				}
				
				
				
				JOptionPane.showMessageDialog(this, "Tâche ajoutée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
				
				this.txtNom.setText("");
				this.txtDuree.setText("");
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(this, "La durée doit être un nombre entier.", "Erreur", JOptionPane.ERROR_MESSAGE);
			}
			this.majListeTaches();
			this.frame.calculerDates();
			this.frame.resetFrame();
			
		}
	}
}
