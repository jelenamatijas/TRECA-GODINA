package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.entity.StudijskiProgram;
import org.unibl.etf.bp.uniis.util.Utilities;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class StudijskiProgramDialog extends JDialog {
	
	private StudijskiProgramDialog ovaj;
	private boolean izmena;
	private String dialogResult = "Cancel";

	private final JPanel contentPanel = new JPanel();
	private JTextField tfIdSP;
	private JTextField tfNazivSP;
	@SuppressWarnings("rawtypes")
	private JComboBox cbCiklus;
	@SuppressWarnings("rawtypes")
	private JComboBox cbTrajanje;
	private JTextField tfUkupnoECTS;
	private JTextField tfZvanje;
	@SuppressWarnings("rawtypes")
	private JComboBox cbFakultet;

	/**
	 * Create the dialog.
	 */
	public StudijskiProgramDialog() {
		ovaj = this;
		izmena = false;

		initialize();
	}

	public StudijskiProgramDialog(StudijskiProgram studijskiProgram) {
		ovaj = this;
		izmena = true;

		initialize();

		tfIdSP.setText(Integer.toString(studijskiProgram.getIdSP()));
		tfIdSP.setEditable(false);
		tfNazivSP.setText(studijskiProgram.getNazivSP());
		cbCiklus.setSelectedItem(studijskiProgram.getCiklus());
		cbTrajanje.setSelectedItem(studijskiProgram.getTrajanje());
		tfUkupnoECTS.setText(Integer.toString(studijskiProgram
				.getUkupanBrojEcts()));
		tfZvanje.setText(studijskiProgram.getZvanje());
		cbFakultet.setSelectedItem(studijskiProgram.getFakultet());
	}

	public String getDialogResult() {
		return dialogResult;
	}

	private boolean proveriValidnostPolja() {
		if (tfIdSP.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj,
					"Identifikator studijskog programa nije popunjen!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.tryParseInt(tfIdSP.getText()))
				|| Integer.valueOf(tfIdSP.getText()) < 1) {
			JOptionPane
					.showMessageDialog(
							ovaj,
							"Identifikator studijskog programa nije pravilno popunjen!",
							"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (tfNazivSP.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj,
					"Naziv studijskog programa nije popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.isTextValid(tfNazivSP.getText()))) {
			JOptionPane.showMessageDialog(ovaj,
					"Naziv studijskog programa nije pravilno popunjen!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (cbCiklus.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(ovaj, "Ciklus nije odabran!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (cbTrajanje.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(ovaj, "Trajanje nije odabrano!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (tfUkupnoECTS.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj,
					"Ukupan broj ECTS bodova nije popunjen!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.tryParseShort(tfUkupnoECTS.getText()))
				|| Short.valueOf(tfUkupnoECTS.getText()) < 1
				|| Short.valueOf(tfUkupnoECTS.getText()) > 255) {
			JOptionPane.showMessageDialog(ovaj,
					"Ukupan broj ECTS bodova nije pravilno popunjen!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (tfZvanje.getText().length() == 0) {
			JOptionPane.showMessageDialog(ovaj, "Zvanje nije popunjeno!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else if (!(Utilities.isTextValid(tfZvanje.getText()))) {
			JOptionPane.showMessageDialog(ovaj,
					"Zvanje nije pravilno popunjeno!", "Greška",
					JOptionPane.ERROR_MESSAGE);
		} else if (cbFakultet.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(ovaj, "Fakultet nije odabran!",
					"Greška", JOptionPane.ERROR_MESSAGE);
		} else
			return true;
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Studijski program");
		setBounds(100, 100, 355, 310);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel label = new JLabel("Identifikator:");
			label.setBounds(10, 11, 327, 14);
			contentPanel.add(label);
		}
		{
			this.tfIdSP = new JTextField();
			this.tfIdSP.setColumns(10);
			this.tfIdSP.setBounds(10, 25, 327, 20);
			contentPanel.add(this.tfIdSP);
		}
		{
			JLabel lblNazivStudijskogPrograma = new JLabel(
					"Naziv studijskog programa:");
			lblNazivStudijskogPrograma.setBounds(10, 59, 327, 14);
			contentPanel.add(lblNazivStudijskogPrograma);
		}
		{
			this.tfNazivSP = new JTextField();
			this.tfNazivSP.setColumns(10);
			this.tfNazivSP.setBounds(10, 73, 327, 20);
			contentPanel.add(this.tfNazivSP);
		}
		{
			JLabel lblCiklus = new JLabel("Ciklus:");
			lblCiklus.setBounds(10, 104, 102, 14);
			contentPanel.add(lblCiklus);
		}
		{
			cbCiklus = new JComboBox();
			cbCiklus.setModel(new DefaultComboBoxModel(new Byte[] { 1, 2, 3 }));
			cbCiklus.setBounds(10, 118, 102, 20);
			contentPanel.add(cbCiklus);
		}
		{
			JLabel lblTrajanje = new JLabel("Trajanje (sem.):");
			lblTrajanje.setBounds(122, 104, 102, 14);
			contentPanel.add(lblTrajanje);
		}
		{
			cbTrajanje = new JComboBox();
			cbTrajanje.setModel(new DefaultComboBoxModel(new Byte[] { 1, 2, 3,
					4, 5, 6, 7, 8, 9, 10, 11, 12 }));
			cbTrajanje.setBounds(122, 118, 102, 20);
			contentPanel.add(cbTrajanje);
		}
		{
			JLabel lblUkupnoEcts = new JLabel("Ukupno ECTS:");
			lblUkupnoEcts.setBounds(234, 104, 103, 14);
			contentPanel.add(lblUkupnoEcts);
		}
		{
			this.tfUkupnoECTS = new JTextField();
			this.tfUkupnoECTS.setColumns(10);
			this.tfUkupnoECTS.setBounds(234, 118, 103, 20);
			contentPanel.add(this.tfUkupnoECTS);
		}
		{
			JLabel lblZvanje = new JLabel("Zvanje:");
			lblZvanje.setBounds(10, 149, 327, 14);
			contentPanel.add(lblZvanje);
		}
		{
			this.tfZvanje = new JTextField();
			this.tfZvanje.setColumns(10);
			this.tfZvanje.setBounds(10, 163, 327, 20);
			contentPanel.add(this.tfZvanje);
		}
		{
			JLabel lblFakultet = new JLabel("Fakultet:");
			lblFakultet.setBounds(10, 194, 327, 14);
			contentPanel.add(lblFakultet);
		}
		{
			cbFakultet = new JComboBox(Utilities.getDataAccessFactory()
					.getFakultetDataAccess().fakulteti("*")
					.toArray(new Fakultet[] {}));
			cbFakultet.setBounds(10, 208, 327, 20);
			contentPanel.add(cbFakultet);
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
							StudijskiProgram studijskiProgram = new StudijskiProgram(
									Integer.valueOf(tfIdSP.getText()),
									tfNazivSP.getText(), (Byte) cbCiklus
											.getSelectedItem(),
									(Byte) cbTrajanje.getSelectedItem(), Short
											.valueOf(tfUkupnoECTS.getText()),
									tfZvanje.getText(),
									(Fakultet) cbFakultet.getSelectedItem());
							boolean rezultat;
							if (izmena) {
								rezultat = Utilities
										.getDataAccessFactory()
										.getStudijskiProgramDataAccess()
										.azurirajStudijskiProgram(
												studijskiProgram);
								if (!rezultat)
									JOptionPane
											.showMessageDialog(
													ovaj,
													"Studijski program nije uspešno ažuriran!",
													"Poruka",
													JOptionPane.INFORMATION_MESSAGE);
							} else {
								rezultat = Utilities
										.getDataAccessFactory()
										.getStudijskiProgramDataAccess()
										.dodajStudijskiProgram(studijskiProgram);
								if (!rezultat)
									JOptionPane
											.showMessageDialog(
													ovaj,
													"Studijski program nije uspešno dodan!",
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
				okButton.setIcon(new ImageIcon(StudijskiProgramDialog.class
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
								StudijskiProgramDialog.class
										.getResource(Utilities.IMAGE_RESOURCES_PATH + "Cancel_14.png")));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
}
