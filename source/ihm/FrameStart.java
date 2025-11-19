package source.ihm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import source.Controleur;

public class FrameStart extends JFrame implements ActionListener
{
	private Controleur   ctrl           ;
	private FrameGraphe  frameGraphe    ;
	
	private JRadioButton rbDateAuPlusTot;
	
	private JButton      btnEnvoyer     ;
	private JSpinner     spinnerDate    ;
	
	// ========== CONSTRUCTEUR ==========
	
	/**
	 * Crée une fenêtre de sélection de date pour le projet
	 * @param ctrl le contrôleur de l'application
	 */
	public FrameStart ( Controleur ctrl, FrameGraphe frameGraphe )
	{
		this.ctrl        = ctrl       ;
		this.frameGraphe = frameGraphe;
		
		this.setTitle    ( "Choix date projet" );
		this.setLocation ( 40, 40              );
		
		/* ----------------------------- */
		/* Création des Composants       */
		/* ----------------------------- */
		
		JPanel panelSaisie;
		JPanel panelLbl   ;
		JPanel panelInfo  ;
		JPanel panelBtn   ;
		JPanel panelDate  ;
		
		JLabel lblChoix;
		
		
		SpinnerDateModel model;
		
		panelInfo     = new JPanel( new GridLayout( 3, 1 ) );
		panelBtn      = new JPanel();
		panelLbl      = new JPanel();
		panelSaisie   = new JPanel();
		panelDate     = new JPanel();
		
		lblChoix = new JLabel( "Veuillez saisir votre date de debut ou de fin de projet" );
		
		this.rbDateAuPlusTot = new JRadioButton( "Date de debut de projet" );
		this.rbDateAuPlusTot .setSelected( true  );
		this.rbDateAuPlusTot .setEnabled ( false );
		
		this.btnEnvoyer = new JButton("Soumettre");
		
		// Création du spinner pour la date
		
		model            = new SpinnerDateModel();
		this.spinnerDate = new JSpinner( model  );
		this.spinnerDate .setEditor( new JSpinner.DateEditor( this.spinnerDate, "dd/MM" ) );
		
		/* ----------------------------- */
		/* Positionnement des Composants */
		/* ----------------------------- */
		
		
		this.add( panelInfo , BorderLayout.CENTER );
		this.add( panelBtn , BorderLayout.SOUTH   );
		
		panelInfo  .add( panelLbl                 );
		panelInfo  .add( panelSaisie              );
		panelInfo  .add( panelDate                );
		
		panelLbl   .add( lblChoix                 );
		
		panelSaisie.add( this.rbDateAuPlusTot     );
		
		panelDate  .add( this.spinnerDate         );  // Ajout du spinner
		
		panelBtn   .add( this.btnEnvoyer          );
		
		/* ----------------------------- */
		/* Activation des Composants     */
		/* ----------------------------- */
		
		this.btnEnvoyer.addActionListener(this);
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.pack();
	}
	
	// ========== AUTRES MÉTHODES ==========
	
	/**
	 * Gère les événements de clic sur les boutons
	 * @param e l'événement d'action
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnEnvoyer)
		{
			// Récupération de la date sélectionnée dans le spinner
			// Cast nécessaire car getValue() retourne un Object
			Date dateSelectionne = (Date) this.spinnerDate.getValue();
			
			Calendar calSelectionne = Calendar.getInstance();
			calSelectionne.setTime(dateSelectionne);
			
			// Création d'un Calendar pour la date actuelle (maintenant)
			Calendar calAujourdhui = Calendar.getInstance();
			
			// Créer un "numéro de jour dans l'année" pour comparer
			//mois = 100 jour = 1
			int jourAnneeSelectionne = calSelectionne.get( Calendar.MONTH) * 100 +
			                           calSelectionne.get( Calendar.DAY_OF_MONTH  );
			                           
			int jourAnneeAujourdhui  = calAujourdhui .get( Calendar.MONTH) * 100 +
			                           calAujourdhui .get( Calendar.DAY_OF_MONTH  );
			
			if ( jourAnneeSelectionne >= jourAnneeAujourdhui )
			{
				this.ctrl.setDateProjet( dateSelectionne, true );
			}
			else
			{
				JOptionPane.showMessageDialog( this, "Veuillez saisir une date valide (aujourd'hui ou dans le futur)",
				"FrameStart", JOptionPane.ERROR_MESSAGE );
			}
			
			this.frameGraphe.maj();
			
			this.setVisible(false);
			
			
		}
	}
}
