package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.util.Utilities;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class FakultetDialog extends JDialog {
	
	private FakultetDialog ovaj;
	private boolean izmena;
	private String dialogResult = "Cancel";

	private final JPanel contentPanel = new JPanel();
	private JTextField tfNazivFakulteta;
	private JTextField tfAdresa;

	/**
	 * Create the dialog.
	 */
	public FakultetDialog() {
		ovaj = this;
		izmena = false;

		initialize();
	}

	public FakultetDialog(Fakultet fakultet) {
		ovaj = this;
		izmena = true;

		initialize();

		tfNazivFakulteta.setText(fakultet.getNazivFakulteta());
		tfNazivFakulteta.setEditable(false);
		tfAdresa.setText(fakultet.getAdresa());
	}

	public String getDialogResult() {
		return dialogResult;
	}

	private boolean proveriValidnostPolja() {
		if (tfNazivFakulteta.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj,
					"Naziv fakulteta nije popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.isTextValid(tfNazivFakulteta.getText()))) {
			JOptionPane.showMessageDialog(ovaj,
					"Naziv fakulteta nije pravilno popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (tfAdresa.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj, "Adresa nije popunjena!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.isTextValid(tfAdresa.getText()))) {
			JOptionPane.showMessageDialog(ovaj,
					"Adresa nije pravilno popunjena!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else
			return true;
		return false;
	}

	private void initialize() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Fakultet");
		setBounds(100, 100, 355, 175);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNazivFakulteta = new JLabel("Naziv fakulteta:");
			lblNazivFakulteta.setBounds(10, 11, 327, 14);
			contentPanel.add(lblNazivFakulteta);
		}
		{
			this.tfNazivFakulteta = new JTextField();
			this.tfNazivFakulteta.setBounds(10, 25, 327, 20);
			this.tfNazivFakulteta.setColumns(10);
			contentPanel.add(this.tfNazivFakulteta);
		}
		{
			JLabel lblAdresa = new JLabel("Adresa:");
			lblAdresa.setBounds(10, 59, 327, 14);
			contentPanel.add(lblAdresa);
		}
		{
			this.tfAdresa = new JTextField();
			this.tfAdresa.setBounds(10, 73, 327, 20);
			this.tfAdresa.setColumns(10);
			contentPanel.add(this.tfAdresa);
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
							Fakultet fakultet = new Fakultet(
									tfNazivFakulteta.getText(), tfAdresa
											.getText());
							boolean rezultat;
							if (izmena) {
								rezultat = Utilities.getDataAccessFactory()
										.getFakultetDataAccess()
										.azurirajFakultet(fakultet);
								if (!rezultat)
									JOptionPane.showMessageDialog(ovaj,
											"Fakultet nije uspešno ažuriran!",
											"Poruka",
											JOptionPane.INFORMATION_MESSAGE);
							} else {
								rezultat = Utilities.getDataAccessFactory()
										.getFakultetDataAccess()
										.dodajFakultet(fakultet);
								if (!rezultat)
									JOptionPane.showMessageDialog(ovaj,
											"Fakultet nije uspešno dodan!",
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
				okButton.setIcon(new ImageIcon(FakultetDialog.class
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
								FakultetDialog.class
										.getResource(Utilities.IMAGE_RESOURCES_PATH + "Cancel_14.png")));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
}
