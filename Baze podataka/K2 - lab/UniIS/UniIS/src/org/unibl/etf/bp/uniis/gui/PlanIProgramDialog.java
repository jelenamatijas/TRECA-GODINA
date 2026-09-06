package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.unibl.etf.bp.uniis.entity.Predmet;
import org.unibl.etf.bp.uniis.entity.PredmetNaStudijskomProgramu;
import org.unibl.etf.bp.uniis.entity.StudijskiProgram;
import org.unibl.etf.bp.uniis.util.Utilities;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class PlanIProgramDialog extends JDialog {
	
	private PlanIProgramDialog ovaj;
	private boolean izmena;
	private String dialogResult = "Cancel";

	private StudijskiProgram studijskiProgram;
	private Predmet predmet;

	private final JPanel contentPanel = new JPanel();
	private JTextField tfIdPredmeta;
	private JButton btnOdaberiPredmet;
	private JTextField tfPredmet;
	@SuppressWarnings("rawtypes")
	private JComboBox cbSemestar;
	@SuppressWarnings("rawtypes")
	private JComboBox cbTipPredmeta;

	/**
	 * Create the dialog.
	 * 
	 * @wbp.parser.constructor
	 */
	public PlanIProgramDialog(StudijskiProgram studijskiProgram) {
		ovaj = this;
		izmena = false;
		this.studijskiProgram = studijskiProgram;

		initialize();

		this.setTitle("Predmet na studijskom programu '" + studijskiProgram
				+ "'");
	}

	public PlanIProgramDialog(PredmetNaStudijskomProgramu predmetNaSP) {
		ovaj = this;
		izmena = true;
		this.studijskiProgram = predmetNaSP.getStudijskiProgram();
		this.predmet = predmetNaSP.getPredmet();

		initialize();

		this.setTitle("Predmet na studijskom programu '" + studijskiProgram
				+ "'");
		tfIdPredmeta.setText(Integer.toString(predmet.getIdPredmeta()));
		tfIdPredmeta.setEditable(false);
		tfPredmet.setText(predmet.toString());
		btnOdaberiPredmet.setEnabled(false);
		cbSemestar.setSelectedItem(predmetNaSP.getSemestar());
		cbTipPredmeta.setSelectedItem(predmetNaSP.getTipPredmeta());
	}

	public String getDialogResult() {
		return dialogResult;
	}

	private boolean proveriValidnostPolja() {
		if (predmet == null) {
			JOptionPane.showMessageDialog(ovaj, "Predmet nije odabran!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (cbSemestar.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(ovaj, "Semestar nije odabran!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (cbTipPredmeta.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(ovaj, "Tip predmeta nije odabran!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else
			return true;
		return false;
	}

	private void pronadjiPredmet(boolean tiho) {
		tfPredmet.setText("");
		predmet = null;
		if (Utilities.tryParseInt(tfIdPredmeta.getText())) {
			predmet = Utilities.getDataAccessFactory().getPredmetDataAccess()
					.predmet(Integer.valueOf(tfIdPredmeta.getText()));
			if (predmet != null)
				tfPredmet.setText(predmet.toString());
		} else if (!tiho)
			JOptionPane.showMessageDialog(ovaj,
					"Identifikator predmeta nije pravilno popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 500, 175);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblPredmet = new JLabel("Predmet:");
			lblPredmet.setBounds(10, 11, 125, 14);
			contentPanel.add(lblPredmet);
		}
		{
			this.tfIdPredmeta = new JTextField();
			this.tfIdPredmeta.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent arg0) {
					pronadjiPredmet(true);
				}
			});
			this.tfIdPredmeta.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
						pronadjiPredmet(false);
				}
			});
			this.tfIdPredmeta.setColumns(10);
			this.tfIdPredmeta.setBounds(10, 28, 125, 20);
			contentPanel.add(this.tfIdPredmeta);
		}
		{
			btnOdaberiPredmet = new JButton("");
			this.btnOdaberiPredmet.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ovaj.setVisible(false);
					final PredmetiFrame pf = new PredmetiFrame(true);
					pf.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							if (pf.getOdabraniPredmet() != null) {
								predmet = pf.getOdabraniPredmet();
								tfIdPredmeta.setText(Integer.toString(predmet
										.getIdPredmeta()));
								tfPredmet.setText(predmet.toString());
							}
							pf.dispose();
							ovaj.setVisible(true);
							ovaj.toFront();
						}
					});
					pf.setVisible(true);
				}
			});
			this.btnOdaberiPredmet
					.setIcon(new ImageIcon(
							PlanIProgramDialog.class
									.getResource(Utilities.IMAGE_RESOURCES_PATH + "Lookup_14.png")));
			this.btnOdaberiPredmet.setBounds(145, 27, 30, 23);
			contentPanel.add(this.btnOdaberiPredmet);
		}
		{
			this.tfPredmet = new JTextField();
			this.tfPredmet.setEditable(false);
			this.tfPredmet.setColumns(10);
			this.tfPredmet.setBounds(185, 28, 300, 20);
			contentPanel.add(this.tfPredmet);
		}
		{
			JLabel lblSemestar = new JLabel("Semestar:");
			lblSemestar.setBounds(10, 59, 125, 14);
			contentPanel.add(lblSemestar);
		}
		{
			this.cbSemestar = new JComboBox();
			this.cbSemestar.setModel(new DefaultComboBoxModel(new Byte[] { 1,
					2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }));
			this.cbSemestar.setSelectedIndex(0);
			this.cbSemestar.setBounds(10, 73, 125, 20);
			contentPanel.add(this.cbSemestar);
		}
		{
			JLabel lblTipPredmeta = new JLabel("Tip predmeta:");
			lblTipPredmeta.setBounds(145, 59, 125, 14);
			contentPanel.add(lblTipPredmeta);
		}
		{
			this.cbTipPredmeta = new JComboBox();
			this.cbTipPredmeta.setModel(new DefaultComboBoxModel(new String[] {
					"O", "I" }));
			this.cbTipPredmeta.setSelectedIndex(0);
			this.cbTipPredmeta.setBounds(145, 73, 125, 20);
			contentPanel.add(this.cbTipPredmeta);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Sačuvati");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (proveriValidnostPolja()) {
							PredmetNaStudijskomProgramu predmetNaSP = new PredmetNaStudijskomProgramu(
									predmet, studijskiProgram,
									(Byte) cbSemestar.getSelectedItem(),
									(String) cbTipPredmeta.getSelectedItem());
							boolean rezultat;
							if (izmena) {
								rezultat = Utilities.getDataAccessFactory()
										.getPredmetNaStudijskomProgramuDataAccess()
										.azurirajPredmetNaSP(predmetNaSP);
								if (!rezultat)
									JOptionPane
											.showMessageDialog(
													ovaj,
													"Predmet na studijskom programu nije uspešno ažuriran!",
													"Poruka",
													JOptionPane.INFORMATION_MESSAGE);
							} else {
								rezultat = Utilities.getDataAccessFactory()
										.getPredmetNaStudijskomProgramuDataAccess()
										.dodajPredmetNaSP(predmetNaSP);
								if (!rezultat)
									JOptionPane
											.showMessageDialog(
													ovaj,
													"Predmet nije uspešno dodan na studijski program!",
													"Poruka",
													JOptionPane.INFORMATION_MESSAGE);
							}
							if (rezultat) {
								dialogResult = e.getActionCommand();
								ovaj.getToolkit()
										.getSystemEventQueue()
										.postEvent(
												new WindowEvent(
														ovaj,
														WindowEvent.WINDOW_CLOSING));
							}

						}
					}
				});
				okButton.setIcon(new ImageIcon(PlanIProgramDialog.class
						.getResource(Utilities.IMAGE_RESOURCES_PATH + "Check_14.png")));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				// getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Otkazati");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialogResult = e.getActionCommand();
						ovaj.getToolkit()
								.getSystemEventQueue()
								.postEvent(
										new WindowEvent(ovaj,
												WindowEvent.WINDOW_CLOSING));
					}
				});
				cancelButton
						.setIcon(new ImageIcon(
								PlanIProgramDialog.class
										.getResource(Utilities.IMAGE_RESOURCES_PATH + "Cancel_14.png")));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
}
