package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.entity.Predmet;
import org.unibl.etf.bp.uniis.util.Utilities;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class PredmetDialog extends JDialog {
	
	private PredmetDialog ovaj;
	private boolean izmena;
	private String dialogResult = "Cancel";

	private final JPanel contentPanel = new JPanel();
	private JTextField tfIdPredmeta;
	private JTextField tfNazivPredmeta;
	private JTextField tfEcts;
	@SuppressWarnings("rawtypes")
	private JComboBox cbMaticniFakultet;

	/**
	 * Create the dialog.
	 */
	public PredmetDialog() {
		ovaj = this;
		izmena = false;

		initialize();
	}

	public PredmetDialog(Predmet predmet) {
		ovaj = this;
		izmena = true;

		initialize();

		tfIdPredmeta.setText(Integer.toString(predmet.getIdPredmeta()));
		tfIdPredmeta.setEditable(false);
		tfNazivPredmeta.setText(predmet.getNazivPredmeta());
		tfEcts.setText(Integer.toString(predmet.getEcts()));
		cbMaticniFakultet.setSelectedItem(predmet.getFakultet());
	}

	public String getDialogResult() {
		return dialogResult;
	}

	private boolean proveriValidnostPolja() {
		if (tfIdPredmeta.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj,
					"Identifikator predmeta nije popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.tryParseInt(tfIdPredmeta.getText()))
				|| Integer.valueOf(tfIdPredmeta.getText()) < 1) {
			JOptionPane.showMessageDialog(ovaj,
					"Identifikator predmeta nije pravilno popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (tfNazivPredmeta.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj,
					"Naziv predmeta nije popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.isTextValid(tfNazivPredmeta.getText()))) {
			JOptionPane.showMessageDialog(ovaj,
					"Naziv predmeta nije pravilno popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (tfEcts.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj, "ECTS bodovi nisu popunjeni!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.tryParseByte(tfEcts.getText()))
				|| Byte.valueOf(tfEcts.getText()) < 1) {
			JOptionPane.showMessageDialog(ovaj,
					"ECTS bodovi nisu pravilno popunjeni!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (cbMaticniFakultet.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(ovaj,
					"Matični fakultet nije odabran!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else
			return true;
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Predmet");
		setBounds(100, 100, 355, 220);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblIdentifikator = new JLabel("Identifikator:");
			lblIdentifikator.setBounds(10, 11, 327, 14);
			contentPanel.add(lblIdentifikator);
		}
		{
			this.tfIdPredmeta = new JTextField();
			this.tfIdPredmeta.setColumns(10);
			this.tfIdPredmeta.setBounds(10, 25, 327, 20);
			contentPanel.add(this.tfIdPredmeta);
		}
		{
			JLabel lblNazivPredmeta = new JLabel("Naziv predmeta:");
			lblNazivPredmeta.setBounds(10, 59, 327, 14);
			contentPanel.add(lblNazivPredmeta);
		}
		{
			this.tfNazivPredmeta = new JTextField();
			this.tfNazivPredmeta.setColumns(10);
			this.tfNazivPredmeta.setBounds(10, 73, 327, 20);
			contentPanel.add(this.tfNazivPredmeta);
		}
		{
			JLabel lblEcts = new JLabel("ECTS:");
			lblEcts.setBounds(10, 104, 40, 14);
			contentPanel.add(lblEcts);
		}
		{
			this.tfEcts = new JTextField();
			this.tfEcts.setColumns(10);
			this.tfEcts.setBounds(10, 118, 40, 20);
			contentPanel.add(this.tfEcts);
		}
		{
			JLabel lblMatiniFakultet = new JLabel("Matični fakultet:");
			lblMatiniFakultet.setBounds(60, 104, 277, 14);
			contentPanel.add(lblMatiniFakultet);
		}
		{
			this.cbMaticniFakultet = new JComboBox(Utilities
					.getDataAccessFactory().getFakultetDataAccess().fakulteti("*")
					.toArray(new Fakultet[] {}));
			this.cbMaticniFakultet.setBounds(60, 118, 277, 20);
			contentPanel.add(this.cbMaticniFakultet);
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
							Predmet predmet = new Predmet(Integer
									.valueOf(tfIdPredmeta.getText()),
									tfNazivPredmeta.getText(), Byte
											.valueOf(tfEcts.getText()),
									(Fakultet) cbMaticniFakultet
											.getSelectedItem());
							boolean rezultat;
							if (izmena) {
								rezultat = Utilities.getDataAccessFactory()
										.getPredmetDataAccess()
										.azurirajPredmet(predmet);
								if (!rezultat)
									JOptionPane.showMessageDialog(ovaj,
											"Predmet nije uspešno ažuriran!",
											"Poruka",
											JOptionPane.INFORMATION_MESSAGE);
							} else {
								rezultat = Utilities.getDataAccessFactory()
										.getPredmetDataAccess().dodajPredmet(predmet);
								if (!rezultat)
									JOptionPane.showMessageDialog(ovaj,
											"Predmet nije uspešno dodan!",
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
				okButton.setIcon(new ImageIcon(PredmetDialog.class
						.getResource(Utilities.IMAGE_RESOURCES_PATH + "Check_14.png")));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
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
								PredmetDialog.class
										.getResource(Utilities.IMAGE_RESOURCES_PATH + "Cancel_14.png")));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
}
