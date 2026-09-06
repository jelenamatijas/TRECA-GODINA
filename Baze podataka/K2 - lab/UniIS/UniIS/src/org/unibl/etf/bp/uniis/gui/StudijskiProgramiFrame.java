package org.unibl.etf.bp.uniis.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.entity.StudijskiProgram;
import org.unibl.etf.bp.uniis.util.Utilities;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

@SuppressWarnings("serial")
public class StudijskiProgramiFrame extends JFrame {
	
	private StudijskiProgramiFrame ovaj;
	private List<StudijskiProgram> studijskiProgrami;

	private StudijskiProgram odabraniStudijskiProgram;

	private JPanel contentPane;
	private JPanel panelPodaci;
	private JPanel panel;
	private JPanel panelOpcije;
	private JPanel panelPretraga;
	private JScrollPane scrollPane;
	private JTable table;
	private JButton btnDodati;
	private JButton btnIzmeniti;
	private JButton btnObrisati;
	private JButton btnPrihvatiti;
	private JLabel lblNazivStudijskogPrograma;
	private JTextField tfNazivSP;
	private JLabel lblFakultet;
	@SuppressWarnings("rawtypes")
	private JComboBox cbFakultet;
	private JButton btnPretraziti;
	private JButton btnPrikazatiSve;
	private JLabel lblCiklus;
	@SuppressWarnings("rawtypes")
	private JComboBox cbCiklus;

	/**
	 * Create the frame.
	 */
	public StudijskiProgramiFrame(boolean odabirStudijskogPrograma) {
		ovaj = this;
		studijskiProgrami = Utilities.getDataAccessFactory()
				.getStudijskiProgramDataAccess().studijskiProgrami("*", null, null);

		initialize();

		if (!odabirStudijskogPrograma)
			btnPrihvatiti.setVisible(false);
	}

	public StudijskiProgram getOdabraniStudijskiProgram() {
		return odabraniStudijskiProgram;
	}

	private void osveziTabelu() {
		if (Utilities.isSearchPatternValid(tfNazivSP.getText())) {
			String nazivFakulteta = null;
			Byte ciklus = null;
			if (cbCiklus.getSelectedIndex() != -1)
				ciklus = (Byte) cbCiklus.getSelectedItem();
			if (cbFakultet.getSelectedItem() != null)
				nazivFakulteta = ((Fakultet) cbFakultet.getSelectedItem())
						.getNazivFakulteta();

			studijskiProgrami = Utilities
					.getDataAccessFactory()
					.getStudijskiProgramDataAccess()
					.studijskiProgrami(tfNazivSP.getText(), ciklus,
							nazivFakulteta);

			StudijskiProgramTableModel ftm = (StudijskiProgramTableModel) table
					.getModel();
			ftm.setPodaci(studijskiProgrami);
			ftm.fireTableDataChanged();
		}
	}

	private void initialize() {
		setTitle("Studijski programi");
		setBounds(100, 100, 960, 420);
		setLocationRelativeTo(null);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		this.contentPane.add(getPanel(), BorderLayout.NORTH);
		this.contentPane.add(getPanelPodaci(), BorderLayout.CENTER);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getPanelOpcije(), BorderLayout.NORTH);
			panel.add(getPanelPretraga(), BorderLayout.SOUTH);
		}
		return panel;
	}

	private JPanel getPanelPodaci() {
		if (panelPodaci == null) {
			panelPodaci = new JPanel();
			panelPodaci.setLayout(new BorderLayout(0, 0));
			panelPodaci.add(getScrollPane(), BorderLayout.CENTER);
		}
		return panelPodaci;
	}

	private JPanel getPanelOpcije() {
		if (panelOpcije == null) {
			panelOpcije = new JPanel();
			panelOpcije.setPreferredSize(new Dimension(200, 68));
			panelOpcije.setLayout(null);
			panelOpcije.add(getBtnDodati());
			panelOpcije.add(getBtnIzmeniti());
			panelOpcije.add(getBtnObrisati());
			panelOpcije.add(getBtnPrihvatiti());
		}
		return panelOpcije;
	}

	private JPanel getPanelPretraga() {
		if (panelPretraga == null) {
			panelPretraga = new JPanel();
			panelPretraga.setPreferredSize(new Dimension(200, 70));
			panelPretraga.setBorder(new TitledBorder(null, "Pretraga",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panelPretraga.setLayout(null);
			panelPretraga.add(getLblNazivStudijskogPrograma());
			panelPretraga.add(getTfNazivSP());
			panelPretraga.add(getLblFakultet());
			panelPretraga.add(getCbFakultet());
			panelPretraga.add(getBtnPretraziti());
			panelPretraga.add(getBtnPrikazatiSve());
			panelPretraga.add(getLblCiklus());
			panelPretraga.add(getCbCiklus());
		}
		return panelPretraga;
	}

	private JButton getBtnDodati() {
		if (btnDodati == null) {
			btnDodati = new JButton("");
			btnDodati.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					StudijskiProgramDialog spd = new StudijskiProgramDialog();
					spd.setVisible(true);
					if (spd.getDialogResult().equalsIgnoreCase("OK")) {
						osveziTabelu();
						JOptionPane.showMessageDialog(ovaj,
								"Novi studijski program je uspešno dodan!",
								"Poruka", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			btnDodati.setIcon(new ImageIcon(StudijskiProgramiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Add_32.png")));
			btnDodati.setToolTipText("Dodati novi studijski program");
			btnDodati.setBounds(0, 0, 58, 58);
		}
		return btnDodati;
	}

	private JButton getBtnIzmeniti() {
		if (btnIzmeniti == null) {
			btnIzmeniti = new JButton("");
			btnIzmeniti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(ovaj,
								"Studijski program nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						StudijskiProgram odabraniStudijskiProgram = ((StudijskiProgramTableModel) table
								.getModel()).getStudijskiProgramAtRow(table
								.getSelectedRow());
						StudijskiProgramDialog spd = new StudijskiProgramDialog(
								odabraniStudijskiProgram);
						spd.setVisible(true);
						if (spd.getDialogResult().equalsIgnoreCase("OK")) {
							osveziTabelu();
							JOptionPane.showMessageDialog(ovaj,
									"Studijski program je uspešno ažuriran!",
									"Poruka", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			btnIzmeniti.setIcon(new ImageIcon(StudijskiProgramiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Edit_32.png")));
			btnIzmeniti.setToolTipText("Izmeniti odabrani studijski program");
			btnIzmeniti.setBounds(68, 0, 58, 58);
		}
		return btnIzmeniti;
	}

	private JButton getBtnObrisati() {
		if (btnObrisati == null) {
			btnObrisati = new JButton("");
			btnObrisati.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(ovaj,
								"Studijski program nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						StudijskiProgram odabraniStudijskiProgram = ((StudijskiProgramTableModel) table
								.getModel()).getStudijskiProgramAtRow(table
								.getSelectedRow());
						int odabir = JOptionPane
								.showOptionDialog(
										ovaj,
										"Da li ste sigurni da želite obrisati odabrani studijski program?",
										"Potvrda brisanja",
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE, null,
										Utilities.YES_NO_OPTIONS,
										Utilities.YES_NO_OPTIONS[1]);
						if (odabir == JOptionPane.YES_OPTION) {
							if (Utilities
									.getDataAccessFactory()
									.getStudijskiProgramDataAccess()
									.obrisiStudijskiProgram(
											odabraniStudijskiProgram.getIdSP())) {
								osveziTabelu();
								JOptionPane
										.showMessageDialog(
												ovaj,
												"Studijski program je uspešno obrisan!",
												"Poruka",
												JOptionPane.INFORMATION_MESSAGE);
							} else
								JOptionPane
										.showMessageDialog(
												ovaj,
												"Studijski program nije uspešno obrisan!",
												"Poruka",
												JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			});
			btnObrisati.setIcon(new ImageIcon(StudijskiProgramiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Delete_32.png")));
			btnObrisati.setToolTipText("Obrisati odabrani studijski program");
			btnObrisati.setBounds(136, 0, 58, 58);
		}
		return btnObrisati;
	}

	private JButton getBtnPrihvatiti() {
		if (btnPrihvatiti == null) {
			btnPrihvatiti = new JButton("");
			btnPrihvatiti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(ovaj,
								"Studijski program nije odabran!", "Greška",
								JOptionPane.ERROR_MESSAGE);
					} else {
						odabraniStudijskiProgram = ((StudijskiProgramTableModel) table
								.getModel()).getStudijskiProgramAtRow(table
								.getSelectedRow());
						ovaj.getToolkit()
								.getSystemEventQueue()
								.postEvent(
										new WindowEvent(ovaj,
												WindowEvent.WINDOW_CLOSING));
					}
				}
			});
			btnPrihvatiti.setIcon(new ImageIcon(StudijskiProgramiFrame.class
					.getResource(Utilities.IMAGE_RESOURCES_PATH + "Check_32.png")));
			btnPrihvatiti
					.setToolTipText("Prihvatiti odabrani studijski program");
			btnPrihvatiti.setBounds(204, 0, 58, 58);
		}
		return btnPrihvatiti;
	}

	private JLabel getLblNazivStudijskogPrograma() {
		if (lblNazivStudijskogPrograma == null) {
			lblNazivStudijskogPrograma = new JLabel(
					"Naziv studijskog programa:");
			lblNazivStudijskogPrograma.setBounds(10, 20, 254, 14);
		}
		return lblNazivStudijskogPrograma;
	}

	private JTextField getTfNazivSP() {
		if (tfNazivSP == null) {
			tfNazivSP = new JTextField();
			tfNazivSP.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
						btnPretraziti.doClick();
				}
			});
			tfNazivSP.setText("*");
			tfNazivSP.setColumns(10);
			tfNazivSP.setBounds(10, 37, 254, 20);
		}
		return tfNazivSP;
	}

	private JLabel getLblFakultet() {
		if (lblFakultet == null) {
			lblFakultet = new JLabel("Fakultet:");
			lblFakultet.setBounds(274, 20, 254, 14);
		}
		return lblFakultet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getCbFakultet() {
		if (cbFakultet == null) {
			cbFakultet = new JComboBox(Utilities.getDataAccessFactory()
					.getFakultetDataAccess().fakulteti("*")
					.toArray(new Fakultet[] {}));
			cbFakultet.setSelectedIndex(-1);
			cbFakultet.setBounds(274, 37, 254, 20);
		}
		return cbFakultet;
	}

	private JLabel getLblCiklus() {
		if (lblCiklus == null) {
			lblCiklus = new JLabel("Ciklus:");
			lblCiklus.setBounds(538, 20, 49, 14);
		}
		return lblCiklus;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getCbCiklus() {
		if (cbCiklus == null) {
			cbCiklus = new JComboBox();
			cbCiklus.setModel(new DefaultComboBoxModel(new Byte[] { 1, 2, 3 }));
			cbCiklus.setSelectedIndex(-1);
			cbCiklus.setBounds(538, 37, 49, 20);
		}
		return cbCiklus;
	}

	private JButton getBtnPretraziti() {
		if (btnPretraziti == null) {
			btnPretraziti = new JButton("Pretražiti");
			btnPretraziti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (Utilities.isSearchPatternValid(tfNazivSP.getText()))
						osveziTabelu();
					else
						JOptionPane
								.showMessageDialog(
										ovaj,
										"Naziv studijskog programa nije pravilno popunjen!",
										"Greška", JOptionPane.ERROR_MESSAGE);
				}
			});
			btnPretraziti.setBounds(597, 34, 100, 23);
		}
		return btnPretraziti;
	}

	private JButton getBtnPrikazatiSve() {
		if (btnPrikazatiSve == null) {
			btnPrikazatiSve = new JButton("Prikazati sve");
			btnPrikazatiSve.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tfNazivSP.setText("*");
					cbFakultet.setSelectedIndex(-1);
					cbCiklus.setSelectedIndex(-1);
					osveziTabelu();
				}
			});
			btnPrikazatiSve.setBounds(707, 34, 100, 23);
		}
		return btnPrikazatiSve;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setViewportView(getTable());
		}
		return scrollPane;
	}

	private JTable getTable() {
		if (table == null) {
			table = new JTable(
					new StudijskiProgramTableModel(studijskiProgrami));
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setFillsViewportHeight(true);
			table.getColumnModel().getColumn(0).setPreferredWidth(100);
			table.getColumnModel().getColumn(1).setPreferredWidth(350);
			table.getColumnModel().getColumn(2).setPreferredWidth(100);
			table.getColumnModel().getColumn(3).setPreferredWidth(100);
			table.getColumnModel().getColumn(4).setPreferredWidth(100);
			table.getColumnModel().getColumn(5).setPreferredWidth(300);
			table.getColumnModel().getColumn(6).setPreferredWidth(300);
		}
		return table;
	}
	
}
